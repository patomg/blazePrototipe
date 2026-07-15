package com.blaze.fitness.domain.model

enum class MuscleGroup { CHEST, BACK, LEGS, SHOULDERS, ARMS, CORE, FULL_BODY }

enum class Equipment { NONE, DUMBBELL, BARBELL, MACHINE, RESISTANCE_BAND, KETTLEBELL }

/**
 * Tags that [RiskRules] matches against a user's questionnaire to decide whether an
 * exercise is unsafe, needs a warning, or should be swapped for a variant.
 */
enum class RiskFactor {
    SPINAL_AXIAL_LOAD,
    LUMBAR_FLEXION,
    HIGH_IMPACT,
    HEAVY_GRIP,
    OVERHEAD_LOAD,
    JOINT_INTENSIVE,
}

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val equipment: Equipment,
    val mediaUrl: String,
    val instructions: String,
    val riskFactors: Set<RiskFactor> = emptySet(),
    val safeAlternativeId: String? = null,
)
