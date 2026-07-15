package com.blaze.fitness.data.remote.dto

data class ExerciseDto(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val mediaUrl: String,
    val instructions: String,
    val riskFactors: Set<String>,
    val safeAlternativeId: String?,
)
