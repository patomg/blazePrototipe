package com.blaze.fitness.data.repository

import com.blaze.fitness.data.local.dao.ExerciseDao
import com.blaze.fitness.data.local.entity.ExerciseEntity
import com.blaze.fitness.data.remote.ApiService
import com.blaze.fitness.data.remote.dto.toDomain
import com.blaze.fitness.domain.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room is the single source of truth for the UI; [refresh] pulls the latest catalog from
 * the API and writes it through so the app still works offline after the first sync.
 */
class ExerciseRepository(
    private val api: ApiService,
    private val exerciseDao: ExerciseDao,
) {
    fun observeCatalog(): Flow<List<Exercise>> =
        exerciseDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    suspend fun refresh() {
        val exercises = api.getExercises().map { it.toDomain() }
        exerciseDao.upsertAll(exercises.map(ExerciseEntity::fromDomain))
    }
}
