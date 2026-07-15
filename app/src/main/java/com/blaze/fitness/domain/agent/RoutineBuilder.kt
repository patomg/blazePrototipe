package com.blaze.fitness.domain.agent

import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.ExperienceLevel
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.WorkoutExercise
import com.blaze.fitness.domain.model.WorkoutPlan
import com.blaze.fitness.domain.model.WorkoutSet
import java.util.UUID

/**
 * Builds a starting [WorkoutPlan] from the exercise catalog already filtered for the
 * user's risk profile, sizing sets/reps by experience level.
 */
object RoutineBuilder {

    private const val EXERCISES_PER_ROUTINE = 6

    fun build(questionnaire: Questionnaire, safeExercises: List<Exercise>): WorkoutPlan {
        val (setCount, repRange) = prescriptionFor(questionnaire.experienceLevel)
        val chosen = safeExercises.take(EXERCISES_PER_ROUTINE)

        val exercises = chosen.map { exercise ->
            WorkoutExercise(
                exercise = exercise,
                sets = (1..setCount).map { setNumber ->
                    WorkoutSet(setNumber = setNumber, reps = repRange, weightKg = 0f)
                },
            )
        }

        return WorkoutPlan(
            id = UUID.randomUUID().toString(),
            title = "Rutina para ${questionnaire.goal.name.lowercase().replace('_', ' ')}",
            goal = questionnaire.goal,
            exercises = exercises,
        )
    }

    private fun prescriptionFor(level: ExperienceLevel): Pair<Int, Int> = when (level) {
        ExperienceLevel.BEGINNER -> 3 to 12
        ExperienceLevel.INTERMEDIATE -> 4 to 10
        ExperienceLevel.ADVANCED -> 5 to 8
    }
}
