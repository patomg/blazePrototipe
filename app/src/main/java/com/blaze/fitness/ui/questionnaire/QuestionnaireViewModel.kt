package com.blaze.fitness.ui.questionnaire

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaze.fitness.data.repository.QuestionnaireRepository
import com.blaze.fitness.domain.model.ExperienceLevel
import com.blaze.fitness.domain.model.Goal
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.Sex
import kotlinx.coroutines.launch

data class QuestionnaireForm(
    val weightKg: String = "",
    val age: String = "",
    val sex: Sex = Sex.OTHER,
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    val hasLumbarPain: Boolean = false,
    val hasHernia: Boolean = false,
    val disabilities: String = "",
    val goal: Goal = Goal.GENERAL_FITNESS,
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
)

class QuestionnaireViewModel(
    private val userId: String,
    private val repository: QuestionnaireRepository,
) : ViewModel() {

    var form by mutableStateOf(QuestionnaireForm())
        private set

    fun update(transform: (QuestionnaireForm) -> QuestionnaireForm) {
        form = transform(form).copy(error = null)
    }

    fun submit() {
        val weight = form.weightKg.toFloatOrNull()
        val age = form.age.toIntOrNull()
        if (weight == null || weight <= 0f || age == null || age <= 0) {
            form = form.copy(error = "Ingresa un peso y edad válidos")
            return
        }

        form = form.copy(isSubmitting = true)
        viewModelScope.launch {
            val questionnaire = Questionnaire(
                userId = userId,
                weightKg = weight,
                age = age,
                sex = form.sex,
                experienceLevel = form.experienceLevel,
                hasLumbarPain = form.hasLumbarPain,
                hasHernia = form.hasHernia,
                disabilities = form.disabilities.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                goal = form.goal,
            )
            repository.submit(questionnaire)
            form = form.copy(isSubmitting = false, isSubmitted = true)
        }
    }
}
