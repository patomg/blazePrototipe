package com.blaze.fitness.data.remote.dto

data class WorkoutSetDto(
    val setNumber: Int,
    val reps: Int,
    val weightKg: Float,
    val completed: Boolean,
)

data class WorkoutExerciseDto(
    val exercise: ExerciseDto,
    val sets: List<WorkoutSetDto>,
)

data class WorkoutSessionDto(
    val id: String,
    val planId: String,
    val startedAt: Long,
    val completedAt: Long?,
    val loggedExercises: List<WorkoutExerciseDto>,
)
