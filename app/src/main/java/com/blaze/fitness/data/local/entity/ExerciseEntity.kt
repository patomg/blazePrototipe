package com.blaze.fitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blaze.fitness.domain.model.Equipment
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.MuscleGroup
import com.blaze.fitness.domain.model.RiskFactor

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val mediaUrl: String,
    val instructions: String,
    val riskFactors: Set<String>,
    val safeAlternativeId: String?,
) {
    fun toDomain() = Exercise(
        id = id,
        name = name,
        muscleGroup = MuscleGroup.valueOf(muscleGroup),
        equipment = Equipment.valueOf(equipment),
        mediaUrl = mediaUrl,
        instructions = instructions,
        riskFactors = riskFactors.map(RiskFactor::valueOf).toSet(),
        safeAlternativeId = safeAlternativeId,
    )

    companion object {
        fun fromDomain(exercise: Exercise) = ExerciseEntity(
            id = exercise.id,
            name = exercise.name,
            muscleGroup = exercise.muscleGroup.name,
            equipment = exercise.equipment.name,
            mediaUrl = exercise.mediaUrl,
            instructions = exercise.instructions,
            riskFactors = exercise.riskFactors.map { it.name }.toSet(),
            safeAlternativeId = exercise.safeAlternativeId,
        )
    }
}
