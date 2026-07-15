package com.blaze.fitness.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.blaze.fitness.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Upsert
    suspend fun upsert(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    fun observeHistory(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE planId = :planId ORDER BY startedAt DESC")
    suspend fun historyForPlan(planId: String): List<WorkoutSessionEntity>
}
