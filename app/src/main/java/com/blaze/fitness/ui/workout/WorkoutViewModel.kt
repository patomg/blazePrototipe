package com.blaze.fitness.ui.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaze.fitness.data.repository.ExerciseRepository
import com.blaze.fitness.data.repository.QuestionnaireRepository
import com.blaze.fitness.data.repository.WorkoutRepository
import com.blaze.fitness.domain.model.WorkoutPlan
import com.blaze.fitness.domain.model.WorkoutSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

data class WorkoutUiState(
    val plan: WorkoutPlan? = null,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
)

class WorkoutViewModel(
    private val userId: String,
    private val exerciseRepository: ExerciseRepository,
    private val questionnaireRepository: QuestionnaireRepository,
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    var uiState by mutableStateOf(WorkoutUiState())
        private set

    init {
        viewModelScope.launch {
            val exercises = exerciseRepository.observeCatalog().first()
            val questionnaire = questionnaireRepository.get(userId)
            val plan = if (questionnaire != null) {
                workoutRepository.buildRoutine(exercises, questionnaire)
            } else {
                null
            }
            uiState = uiState.copy(plan = plan, isLoading = false)
        }
    }

    fun updateSet(exerciseId: String, setNumber: Int, reps: Int, weightKg: Float, completed: Boolean) {
        val plan = uiState.plan ?: return
        val updatedExercises = plan.exercises.map { workoutExercise ->
            if (workoutExercise.exercise.id != exerciseId) return@map workoutExercise
            val updatedSets = workoutExercise.sets.map { set ->
                if (set.setNumber == setNumber) set.copy(reps = reps, weightKg = weightKg, completed = completed) else set
            }
            workoutExercise.copy(sets = updatedSets)
        }
        uiState = uiState.copy(plan = plan.copy(exercises = updatedExercises))
    }

    fun finishSession() {
        val plan = uiState.plan ?: return
        viewModelScope.launch {
            val session = WorkoutSession(
                id = UUID.randomUUID().toString(),
                planId = plan.id,
                startedAt = Instant.now(),
                completedAt = Instant.now(),
                loggedExercises = plan.exercises,
            )
            workoutRepository.logSession(session)
            uiState = uiState.copy(isFinished = true)
        }
    }
}
