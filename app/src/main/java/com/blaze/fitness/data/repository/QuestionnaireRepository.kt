package com.blaze.fitness.data.repository

import com.blaze.fitness.data.local.dao.QuestionnaireDao
import com.blaze.fitness.data.local.entity.QuestionnaireEntity
import com.blaze.fitness.data.remote.ApiService
import com.blaze.fitness.data.remote.dto.toDomain
import com.blaze.fitness.data.remote.dto.toDto
import com.blaze.fitness.domain.model.Questionnaire
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionnaireRepository(
    private val api: ApiService,
    private val questionnaireDao: QuestionnaireDao,
) {
    fun observe(userId: String): Flow<Questionnaire?> =
        questionnaireDao.observe(userId).map { it?.toDomain() }

    suspend fun submit(questionnaire: Questionnaire): Questionnaire {
        questionnaireDao.upsert(QuestionnaireEntity.fromDomain(questionnaire))
        return runCatching { api.submitQuestionnaire(questionnaire.toDto()).toDomain() }
            .getOrDefault(questionnaire)
    }

    suspend fun get(userId: String): Questionnaire? = questionnaireDao.get(userId)?.toDomain()
}
