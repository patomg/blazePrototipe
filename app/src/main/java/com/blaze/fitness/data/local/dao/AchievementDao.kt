package com.blaze.fitness.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.blaze.fitness.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Upsert
    suspend fun upsert(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements ORDER BY unlockedAt DESC")
    fun observeAll(): Flow<List<AchievementEntity>>
}
