package com.blaze.fitness.domain.agent

import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.ExerciseWarning
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.WorkoutPlan
import com.blaze.fitness.domain.model.WorkoutSession

/**
 * A recommended exercise paired with the personalized warnings that apply to it, and the
 * safer alternative to offer when a warning is severe enough to avoid the exercise.
 */
data class ExerciseRecommendation(
    val exercise: Exercise,
    val warnings: List<ExerciseWarning>,
) {
    val isSafe: Boolean get() = warnings.none { it.severity == ExerciseWarning.Severity.AVOID }
}

/**
 * The FitnessAgent is the app's AI engine: it turns the onboarding questionnaire and
 * workout history into personalized, medically-aware exercise recommendations.
 *
 * It has no side effects or I/O — repositories feed it data and persist its output, which
 * keeps the risk/recommendation logic unit-testable in isolation.
 */
class FitnessAgent(
    private val riskRules: RiskRules = RiskRules,
    private val routineBuilder: RoutineBuilder = RoutineBuilder,
    private val progressAdvisor: ProgressAdvisor = ProgressAdvisor,
) {

    fun evaluateExercise(exercise: Exercise, questionnaire: Questionnaire): ExerciseRecommendation {
        return ExerciseRecommendation(exercise, riskRules.evaluate(exercise, questionnaire))
    }

    fun recommendCatalog(exercises: List<Exercise>, questionnaire: Questionnaire): List<ExerciseRecommendation> {
        return exercises.map { evaluateExercise(it, questionnaire) }
    }

    /** Exercises with no AVOID-severity warning, ready to be scheduled into a routine. */
    fun filterSafeExercises(exercises: List<Exercise>, questionnaire: Questionnaire): List<Exercise> {
        return recommendCatalog(exercises, questionnaire)
            .filter { it.isSafe }
            .map { it.exercise }
    }

    fun buildRoutine(exercises: List<Exercise>, questionnaire: Questionnaire): WorkoutPlan {
        val safeExercises = filterSafeExercises(exercises, questionnaire)
        return routineBuilder.build(questionnaire, safeExercises)
    }

    fun suggestNextWeight(exerciseId: String, history: List<WorkoutSession>): ProgressAdvisor.Suggestion? {
        return progressAdvisor.suggestNextWeight(exerciseId, history)
    }
}
