package com.blaze.fitness.ui.questionnaire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaze.fitness.domain.model.ExperienceLevel
import com.blaze.fitness.domain.model.Goal
import com.blaze.fitness.domain.model.Sex
import com.blaze.fitness.ui.common.BlazeTextField
import com.blaze.fitness.ui.common.EnumDropdown
import com.blaze.fitness.ui.common.PrimaryButton
import com.blaze.fitness.ui.theme.BlazeTheme

@Composable
fun QuestionnaireScreen(
    viewModel: QuestionnaireViewModel,
    onCompleted: () -> Unit,
) {
    val form = viewModel.form

    LaunchedEffect(form.isSubmitted) {
        if (form.isSubmitted) onCompleted()
    }

    QuestionnaireContent(
        form = form,
        onUpdate = viewModel::update,
        onSubmit = viewModel::submit,
    )
}

@Composable
private fun QuestionnaireContent(
    form: QuestionnaireForm,
    onUpdate: ((QuestionnaireForm) -> QuestionnaireForm) -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Cuéntanos sobre ti", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Usamos esta información para personalizar tus rutinas y advertirte de ejercicios riesgosos.",
                style = MaterialTheme.typography.bodyMedium,
            )

            BlazeTextField(
                value = form.weightKg,
                onValueChange = { value -> onUpdate { it.copy(weightKg = value) } },
                label = "Peso (kg)",
            )
            BlazeTextField(
                value = form.age,
                onValueChange = { value -> onUpdate { it.copy(age = value) } },
                label = "Edad",
            )

            EnumDropdown(
                label = "Sexo",
                options = Sex.entries,
                selected = form.sex,
                optionLabel = { it.name },
                onSelected = { value -> onUpdate { it.copy(sex = value) } },
            )

            EnumDropdown(
                label = "Experiencia",
                options = ExperienceLevel.entries,
                selected = form.experienceLevel,
                optionLabel = { it.name },
                onSelected = { value -> onUpdate { it.copy(experienceLevel = value) } },
            )

            EnumDropdown(
                label = "Objetivo",
                options = Goal.entries,
                selected = form.goal,
                optionLabel = { it.name },
                onSelected = { value -> onUpdate { it.copy(goal = value) } },
            )

            Row {
                Checkbox(
                    checked = form.hasLumbarPain,
                    onCheckedChange = { value -> onUpdate { it.copy(hasLumbarPain = value) } },
                )
                Text("Tengo dolor lumbar", modifier = Modifier.padding(top = 12.dp))
            }

            Row {
                Checkbox(
                    checked = form.hasHernia,
                    onCheckedChange = { value -> onUpdate { it.copy(hasHernia = value) } },
                )
                Text("Tengo hernia", modifier = Modifier.padding(top = 12.dp))
            }

            BlazeTextField(
                value = form.disabilities,
                onValueChange = { value -> onUpdate { it.copy(disabilities = value) } },
                label = "Otras condiciones (separadas por coma)",
            )

            form.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            PrimaryButton(
                text = "Guardar y continuar",
                onClick = onSubmit,
                isLoading = form.isSubmitting,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionnaireScreenPreview() {
    BlazeTheme {
        QuestionnaireContent(form = QuestionnaireForm(), onUpdate = {}, onSubmit = {})
    }
}
