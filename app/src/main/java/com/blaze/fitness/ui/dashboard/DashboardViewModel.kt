package com.blaze.fitness.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaze.fitness.data.repository.ExerciseRepository
import com.blaze.fitness.data.repository.WorkoutRepository
import com.blaze.fitness.domain.model.WorkoutSession
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/** One point of the "last 7 days" volume bar chart. */
data class DailyVolume(val dayLabel: String, val volumeKg: Float, val isToday: Boolean)

data class DashboardUiState(
    val workoutsThisWeek: Int = 0,
    val currentStreakDays: Int = 0,
    val totalSessions: Int = 0,
    val totalVolumeKg: Float = 0f,
    val dailyVolumes: List<DailyVolume> = emptyList(),
    val volumeTrend: List<Float> = emptyList(),
    val nutritionTip: String = NutritionTips.random(),
)

class DashboardViewModel(
    private val workoutRepository: WorkoutRepository,
    exerciseRepository: ExerciseRepository,
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        viewModelScope.launch {
            exerciseRepository.refresh()
        }
        viewModelScope.launch {
            workoutRepository.observeHistory().collect { sessions ->
                uiState = uiState.copy(
                    workoutsThisWeek = sessions.count { it.startedAt.isAfter(Instant.now().minus(7, ChronoUnit.DAYS)) },
                    currentStreakDays = computeStreak(sessions),
                    totalSessions = sessions.size,
                    totalVolumeKg = sessions.sumOf { it.volumeKg().toDouble() }.toFloat(),
                    dailyVolumes = computeDailyVolumes(sessions),
                    volumeTrend = sessions
                        .sortedBy { it.startedAt }
                        .takeLast(8)
                        .map { it.volumeKg() },
                )
            }
        }
    }

    private fun computeDailyVolumes(sessions: List<WorkoutSession>): List<DailyVolume> {
        val zone = ZoneId.systemDefault()
        val today = Instant.now().atZone(zone).toLocalDate()
        val volumeByDate = sessions
            .groupBy { it.startedAt.atZone(zone).toLocalDate() }
            .mapValues { (_, daySessions) -> daySessions.sumOf { it.volumeKg().toDouble() }.toFloat() }

        return (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong())
            DailyVolume(
                dayLabel = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale("es")).uppercase(),
                volumeKg = volumeByDate[date] ?: 0f,
                isToday = date == today,
            )
        }
    }

    private fun computeStreak(sessions: List<WorkoutSession>): Int {
        val days = sessions.map { it.startedAt.truncatedTo(ChronoUnit.DAYS) }.distinct().sortedDescending()
        var streak = 0
        var cursor = Instant.now().truncatedTo(ChronoUnit.DAYS)
        for (day in days) {
            if (day == cursor) {
                streak++
                cursor = cursor.minus(1, ChronoUnit.DAYS)
            } else {
                break
            }
        }
        return streak
    }
}

private fun WorkoutSession.volumeKg(): Float =
    loggedExercises
        .flatMap { it.sets }
        .filter { it.completed }
        .sumOf { (it.reps * it.weightKg).toDouble() }
        .toFloat()

object NutritionTips {
    private val tips = listOf(
        "Hidrátate antes, durante y después de entrenar.",
        "Prioriza proteína magra en cada comida para recuperar tejido muscular.",
        "El descanso es parte del entrenamiento: duerme al menos 7 horas.",
        "Progresa en pequeños incrementos; la consistencia gana a la intensidad.",
    )

    fun random(): String = tips.random()
}
