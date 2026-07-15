package com.blaze.fitness.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.blaze.fitness.data.local.entity.QuestionnaireEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionnaireDao {
    @Upsert
    suspend fun upsert(questionnaire: QuestionnaireEntity)

    @Query("SELECT * FROM questionnaires WHERE userId = :userId LIMIT 1")
    fun observe(userId: String): Flow<QuestionnaireEntity?>

    @Query("SELECT * FROM questionnaires WHERE userId = :userId LIMIT 1")
    suspend fun get(userId: String): QuestionnaireEntity?
}
