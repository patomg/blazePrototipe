package com.blaze.fitness.data.remote.dto

data class QuestionnaireDto(
    val userId: String,
    val weightKg: Float,
    val age: Int,
    val sex: String,
    val experienceLevel: String,
    val hasLumbarPain: Boolean,
    val hasHernia: Boolean,
    val disabilities: List<String>,
    val goal: String,
)
