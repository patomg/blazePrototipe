package com.blaze.fitness.domain.agent

import com.blaze.fitness.domain.model.WorkoutSession

/**
 * Turns completed [WorkoutSession] history into a progressive-overload suggestion for the
 * next session of a given exercise: nudge weight up when every set was completed at the
 * target reps, hold steady otherwise.
 */
object ProgressAdvisor {

    private const val WEIGHT_STEP_KG = 2.5f
    private const val MIN_SESSIONS_FOR_PROGRESSION = 2

    data class Suggestion(val exerciseId: String, val nextWeightKg: Float, val rationale: String)

    fun suggestNextWeight(exerciseId: String, history: List<WorkoutSession>): Suggestion? {
        val pastSets = history
            .sortedByDescending { it.startedAt }
            .mapNotNull { session -> session.loggedExercises.find { it.exercise.id == exerciseId } }

        if (pastSets.size < MIN_SESSIONS_FOR_PROGRESSION) return null

        val recent = pastSets.first()
        val lastWeight = recent.sets.maxOfOrNull { it.weightKg } ?: return null
        val allSetsCompleted = recent.sets.all { it.completed }
        val previousAlsoCompleted = pastSets[1].sets.all { it.completed }

        return if (allSetsCompleted && previousAlsoCompleted) {
            Suggestion(
                exerciseId = exerciseId,
                nextWeightKg = lastWeight + WEIGHT_STEP_KG,
                rationale = "Completaste todas las series en las últimas 2 sesiones: sube la carga progresivamente.",
            )
        } else {
            Suggestion(
                exerciseId = exerciseId,
                nextWeightKg = lastWeight,
                rationale = "Mantén la carga hasta completar todas las series de forma consistente.",
            )
        }
    }
}
