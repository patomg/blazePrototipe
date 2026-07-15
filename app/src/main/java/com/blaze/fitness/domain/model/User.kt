package com.blaze.fitness.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
)

enum class Sex { MALE, FEMALE, OTHER }

enum class ExperienceLevel { BEGINNER, INTERMEDIATE, ADVANCED }

/**
 * Snapshot of the medical/physical profile collected in the onboarding questionnaire.
 * [FitnessAgent] uses this to filter exercises and build safe routines.
 */
data class Questionnaire(
    val userId: String,
    val weightKg: Float,
    val age: Int,
    val sex: Sex,
    val experienceLevel: ExperienceLevel,
    val hasLumbarPain: Boolean,
    val hasHernia: Boolean,
    val disabilities: List<String> = emptyList(),
    val goal: Goal = Goal.GENERAL_FITNESS,
)

enum class Goal { LOSE_WEIGHT, GAIN_MUSCLE, GENERAL_FITNESS, MOBILITY }
