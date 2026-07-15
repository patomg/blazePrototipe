package com.blaze.fitness.domain.agent

import com.blaze.fitness.domain.model.Equipment
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.MuscleGroup
import com.blaze.fitness.domain.model.WorkoutExercise
import com.blaze.fitness.domain.model.WorkoutSession
import com.blaze.fitness.domain.model.WorkoutSet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class ProgressAdvisorTest {

    private val exercise = Exercise(
        id = "squat",
        name = "Sentadilla",
        muscleGroup = MuscleGroup.LEGS,
        equipment = Equipment.BARBELL,
        mediaUrl = "https://example.com/squat.mp4",
        instructions = "Baja controlando la rodilla.",
    )

    private fun sessionWith(weightKg: Float, allCompleted: Boolean, daysAgo: Long) = WorkoutSession(
        id = "session-$daysAgo",
        planId = "plan-1",
        startedAt = Instant.now().minusSeconds(daysAgo * 86_400),
        completedAt = Instant.now().minusSeconds(daysAgo * 86_400),
        loggedExercises = listOf(
            WorkoutExercise(
                exercise = exercise,
                sets = listOf(
                    WorkoutSet(1, 10, weightKg, completed = allCompleted),
                    WorkoutSet(2, 10, weightKg, completed = allCompleted),
                ),
            ),
        ),
    )

    @Test
    fun `returns null with fewer than two prior sessions`() {
        val history = listOf(sessionWith(weightKg = 40f, allCompleted = true, daysAgo = 1))

        val suggestion = ProgressAdvisor.suggestNextWeight("squat", history)

        assertNull(suggestion)
    }

    @Test
    fun `suggests weight increase after two consecutive fully completed sessions`() {
        val history = listOf(
            sessionWith(weightKg = 40f, allCompleted = true, daysAgo = 1),
            sessionWith(weightKg = 40f, allCompleted = true, daysAgo = 3),
        )

        val suggestion = ProgressAdvisor.suggestNextWeight("squat", history)

        assertEquals(42.5f, suggestion?.nextWeightKg)
    }

    @Test
    fun `holds weight when the most recent session was not fully completed`() {
        val history = listOf(
            sessionWith(weightKg = 40f, allCompleted = false, daysAgo = 1),
            sessionWith(weightKg = 40f, allCompleted = true, daysAgo = 3),
        )

        val suggestion = ProgressAdvisor.suggestNextWeight("squat", history)

        assertEquals(40f, suggestion?.nextWeightKg)
    }
}
