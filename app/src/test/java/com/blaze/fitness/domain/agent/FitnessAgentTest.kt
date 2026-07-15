package com.blaze.fitness.domain.agent

import com.blaze.fitness.domain.model.Equipment
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.ExerciseWarning
import com.blaze.fitness.domain.model.ExperienceLevel
import com.blaze.fitness.domain.model.MuscleGroup
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.RiskFactor
import com.blaze.fitness.domain.model.Sex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FitnessAgentTest {

    private val fitnessAgent = FitnessAgent()

    private fun questionnaire(
        hasLumbarPain: Boolean = false,
        hasHernia: Boolean = false,
        experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    ) = Questionnaire(
        userId = "u1",
        weightKg = 70f,
        age = 30,
        sex = Sex.OTHER,
        experienceLevel = experienceLevel,
        hasLumbarPain = hasLumbarPain,
        hasHernia = hasHernia,
    )

    private fun exercise(riskFactors: Set<RiskFactor> = emptySet()) = Exercise(
        id = "deadlift",
        name = "Peso muerto",
        muscleGroup = MuscleGroup.BACK,
        equipment = Equipment.BARBELL,
        mediaUrl = "https://example.com/deadlift.mp4",
        instructions = "Mantén la espalda neutra.",
        riskFactors = riskFactors,
    )

    @Test
    fun `exercise with axial load risk warns user with hernia at AVOID severity`() {
        val recommendation = fitnessAgent.evaluateExercise(
            exercise(setOf(RiskFactor.SPINAL_AXIAL_LOAD)),
            questionnaire(hasHernia = true),
        )

        assertFalse(recommendation.isSafe)
        assertTrue(recommendation.warnings.any { it.severity == ExerciseWarning.Severity.AVOID })
    }

    @Test
    fun `exercise with no matching risk factors is safe`() {
        val recommendation = fitnessAgent.evaluateExercise(exercise(), questionnaire())

        assertTrue(recommendation.isSafe)
        assertTrue(recommendation.warnings.isEmpty())
    }

    @Test
    fun `filterSafeExercises excludes AVOID severity exercises for hernia profile`() {
        val safeExercise = exercise().copy(id = "plank", riskFactors = emptySet())
        val riskyExercise = exercise(setOf(RiskFactor.SPINAL_AXIAL_LOAD, RiskFactor.LUMBAR_FLEXION))

        val safe = fitnessAgent.filterSafeExercises(
            listOf(safeExercise, riskyExercise),
            questionnaire(hasHernia = true),
        )

        assertEquals(listOf(safeExercise), safe)
    }

    @Test
    fun `buildRoutine sizes sets by experience level`() {
        val exercises = (1..8).map { exercise().copy(id = "ex$it", riskFactors = emptySet()) }

        val beginnerPlan = fitnessAgent.buildRoutine(exercises, questionnaire(experienceLevel = ExperienceLevel.BEGINNER))
        val advancedPlan = fitnessAgent.buildRoutine(exercises, questionnaire(experienceLevel = ExperienceLevel.ADVANCED))

        assertEquals(3, beginnerPlan.exercises.first().sets.size)
        assertEquals(5, advancedPlan.exercises.first().sets.size)
    }
}
