package com.blaze.fitness.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaze.fitness.data.repository.ExerciseRepository
import com.blaze.fitness.data.repository.QuestionnaireRepository
import com.blaze.fitness.domain.agent.ExerciseRecommendation
import com.blaze.fitness.domain.agent.FitnessAgent
import com.blaze.fitness.domain.model.ExperienceLevel
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.Sex
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExerciseCatalogViewModel(
    userId: String,
    exerciseRepository: ExerciseRepository,
    questionnaireRepository: QuestionnaireRepository,
    fitnessAgent: FitnessAgent,
) : ViewModel() {

    private val fallbackQuestionnaire = Questionnaire(
        userId = userId,
        weightKg = 0f,
        age = 0,
        sex = Sex.OTHER,
        experienceLevel = ExperienceLevel.BEGINNER,
        hasLumbarPain = false,
        hasHernia = false,
    )

    val recommendations: StateFlow<List<ExerciseRecommendation>> =
        combine(
            exerciseRepository.observeCatalog(),
            questionnaireRepository.observe(userId),
        ) { exercises, questionnaire ->
            fitnessAgent.recommendCatalog(exercises, questionnaire ?: fallbackQuestionnaire)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { exerciseRepository.refresh() }
    }
}
