package com.blaze.fitness.data.repository

import com.blaze.fitness.data.local.dao.WorkoutDao
import com.blaze.fitness.data.local.entity.WorkoutSessionEntity
import com.blaze.fitness.data.remote.ApiService
import com.blaze.fitness.data.remote.dto.toDto
import com.blaze.fitness.domain.agent.FitnessAgent
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.WorkoutPlan
import com.blaze.fitness.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WorkoutRepository(
    private val api: ApiService,
    private val workoutDao: WorkoutDao,
    private val fitnessAgent: FitnessAgent,
) {
    fun observeHistory(): Flow<List<WorkoutSession>> =
        workoutDao.observeHistory().map { entities -> entities.map { it.toDomain() } }

    fun buildRoutine(exercises: List<Exercise>, questionnaire: Questionnaire): WorkoutPlan =
        fitnessAgent.buildRoutine(exercises, questionnaire)

    suspend fun logSession(session: WorkoutSession) {
        workoutDao.upsert(WorkoutSessionEntity.fromDomain(session))
        runCatching { api.logWorkoutSession(session.toDto()) }
    }

    suspend fun suggestNextWeight(exerciseId: String) =
        fitnessAgent.suggestNextWeight(exerciseId, observeHistory().first())
}
