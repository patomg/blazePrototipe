package com.blaze.fitness.domain.agent

import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.ExerciseWarning
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.RiskFactor

/**
 * Maps a user's medical profile to the exercise [RiskFactor]s they should avoid or be
 * warned about. Kept data-driven so new conditions can be added without touching
 * [FitnessAgent]'s orchestration logic.
 */
object RiskRules {

    private val lumbarPainRisks = setOf(RiskFactor.SPINAL_AXIAL_LOAD, RiskFactor.LUMBAR_FLEXION)
    private val herniaRisks = setOf(RiskFactor.SPINAL_AXIAL_LOAD, RiskFactor.LUMBAR_FLEXION, RiskFactor.HEAVY_GRIP)

    fun riskFactorsFor(questionnaire: Questionnaire): Set<RiskFactor> {
        val risks = mutableSetOf<RiskFactor>()
        if (questionnaire.hasLumbarPain) risks += lumbarPainRisks
        if (questionnaire.hasHernia) risks += herniaRisks
        if (questionnaire.age >= 55) risks += RiskFactor.HIGH_IMPACT
        if (questionnaire.disabilities.any { it.contains("rodilla", ignoreCase = true) || it.contains("knee", ignoreCase = true) }) {
            risks += RiskFactor.JOINT_INTENSIVE
        }
        if (questionnaire.disabilities.any { it.contains("hombro", ignoreCase = true) || it.contains("shoulder", ignoreCase = true) }) {
            risks += RiskFactor.OVERHEAD_LOAD
        }
        return risks
    }

    fun evaluate(exercise: Exercise, questionnaire: Questionnaire): List<ExerciseWarning> {
        val userRisks = riskFactorsFor(questionnaire)
        val matched = exercise.riskFactors.intersect(userRisks)
        return matched.map { risk ->
            ExerciseWarning(
                exerciseId = exercise.id,
                riskFactor = risk,
                message = messageFor(risk),
                severity = severityFor(risk, questionnaire),
            )
        }
    }

    private fun messageFor(risk: RiskFactor): String = when (risk) {
        RiskFactor.SPINAL_AXIAL_LOAD ->
            "Este ejercicio carga la columna vertebral. Con tu historial lumbar, considera la variante sugerida o consulta a un especialista."
        RiskFactor.LUMBAR_FLEXION ->
            "Requiere flexión lumbar bajo carga, un movimiento a evitar con dolor lumbar o hernia."
        RiskFactor.HIGH_IMPACT ->
            "Es un ejercicio de alto impacto; introduce progresivamente el volumen."
        RiskFactor.HEAVY_GRIP ->
            "Exige un agarre intenso que puede aumentar la presión intraabdominal; ve con cargas conservadoras."
        RiskFactor.OVERHEAD_LOAD ->
            "Implica carga por encima de la cabeza; puede agravar molestias de hombro."
        RiskFactor.JOINT_INTENSIVE ->
            "Genera alta tensión articular en rodilla; prioriza rango de movimiento controlado."
    }

    private fun severityFor(risk: RiskFactor, questionnaire: Questionnaire): ExerciseWarning.Severity = when {
        risk == RiskFactor.SPINAL_AXIAL_LOAD && questionnaire.hasHernia -> ExerciseWarning.Severity.AVOID
        risk == RiskFactor.LUMBAR_FLEXION && questionnaire.hasHernia -> ExerciseWarning.Severity.AVOID
        else -> ExerciseWarning.Severity.CAUTION
    }
}
