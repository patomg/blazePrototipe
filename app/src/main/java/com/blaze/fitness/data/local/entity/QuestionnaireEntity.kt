package com.blaze.fitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blaze.fitness.domain.model.ExperienceLevel
import com.blaze.fitness.domain.model.Goal
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.Sex

@Entity(tableName = "questionnaires")
data class QuestionnaireEntity(
    @PrimaryKey val userId: String,
    val weightKg: Float,
    val age: Int,
    val sex: String,
    val experienceLevel: String,
    val hasLumbarPain: Boolean,
    val hasHernia: Boolean,
    val disabilities: List<String>,
    val goal: String,
) {
    fun toDomain() = Questionnaire(
        userId = userId,
        weightKg = weightKg,
        age = age,
        sex = Sex.valueOf(sex),
        experienceLevel = ExperienceLevel.valueOf(experienceLevel),
        hasLumbarPain = hasLumbarPain,
        hasHernia = hasHernia,
        disabilities = disabilities,
        goal = Goal.valueOf(goal),
    )

    companion object {
        fun fromDomain(q: Questionnaire) = QuestionnaireEntity(
            userId = q.userId,
            weightKg = q.weightKg,
            age = q.age,
            sex = q.sex.name,
            experienceLevel = q.experienceLevel.name,
            hasLumbarPain = q.hasLumbarPain,
            hasHernia = q.hasHernia,
            disabilities = q.disabilities,
            goal = q.goal.name,
        )
    }
}
