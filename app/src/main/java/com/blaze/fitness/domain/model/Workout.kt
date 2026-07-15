package com.blaze.fitness.domain.model

import java.time.Instant

data class WorkoutSet(
    val setNumber: Int,
    val reps: Int,
    val weightKg: Float,
    val completed: Boolean = false,
)

data class WorkoutExercise(
    val exercise: Exercise,
    val sets: List<WorkoutSet>,
    val warnings: List<ExerciseWarning> = emptyList(),
)

data class WorkoutPlan(
    val id: String,
    val title: String,
    val goal: Goal,
    val exercises: List<WorkoutExercise>,
)

data class WorkoutSession(
    val id: String,
    val planId: String,
    val startedAt: Instant,
    val completedAt: Instant?,
    val loggedExercises: List<WorkoutExercise>,
)

data class ExerciseWarning(
    val exerciseId: String,
    val riskFactor: RiskFactor,
    val message: String,
    val severity: Severity,
) {
    enum class Severity { INFO, CAUTION, AVOID }
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val unlockedAt: Instant?,
)
