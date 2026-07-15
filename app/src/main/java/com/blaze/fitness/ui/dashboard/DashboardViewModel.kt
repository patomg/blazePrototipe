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
import java.time.temporal.ChronoUnit

data class DashboardUiState(
    val workoutsThisWeek: Int = 0,
    val currentStreakDays: Int = 0,
    val totalSessions: Int = 0,
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
                )
            }
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

object NutritionTips {
    private val tips = listOf(
        "Hidrátate antes, durante y después de entrenar.",
        "Prioriza proteína magra en cada comida para recuperar tejido muscular.",
        "El descanso es parte del entrenamiento: duerme al menos 7 horas.",
        "Progresa en pequeños incrementos; la consistencia gana a la intensidad.",
    )

    fun random(): String = tips.random()
}
