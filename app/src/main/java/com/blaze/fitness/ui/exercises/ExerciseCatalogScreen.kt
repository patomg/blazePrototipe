package com.blaze.fitness.ui.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaze.fitness.domain.agent.ExerciseRecommendation
import com.blaze.fitness.domain.model.Equipment
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.ExerciseWarning
import com.blaze.fitness.domain.model.MuscleGroup
import com.blaze.fitness.domain.model.RiskFactor
import com.blaze.fitness.ui.common.WarningSeverityColor
import com.blaze.fitness.ui.theme.BlazeTheme

@Composable
fun ExerciseCatalogScreen(
    viewModel: ExerciseCatalogViewModel,
    onExerciseClick: (String) -> Unit,
) {
    val recommendations by viewModel.recommendations.collectAsState()
    ExerciseCatalogContent(recommendations, onExerciseClick)
}

@Composable
private fun ExerciseCatalogContent(
    recommendations: List<ExerciseRecommendation>,
    onExerciseClick: (String) -> Unit,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(recommendations, key = { it.exercise.id }) { recommendation ->
                ExerciseCard(recommendation, onClick = { onExerciseClick(recommendation.exercise.id) })
            }
        }
    }
}

@Composable
private fun ExerciseCard(recommendation: ExerciseRecommendation, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(recommendation.exercise.name, style = MaterialTheme.typography.titleMedium)
            Text(
                "${recommendation.exercise.muscleGroup} · ${recommendation.exercise.equipment}",
                style = MaterialTheme.typography.bodyMedium,
            )
            recommendation.warnings.forEach { warning ->
                WarningBadge(warning)
            }
        }
    }
}

@Composable
fun WarningBadge(warning: ExerciseWarning) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(WarningSeverityColor(warning.severity)),
        )
        Text(
            warning.message,
            style = MaterialTheme.typography.labelMedium,
            color = WarningSeverityColor(warning.severity),
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

internal val previewExercise = Exercise(
    id = "1",
    name = "Sentadilla con barra",
    muscleGroup = MuscleGroup.LEGS,
    equipment = Equipment.BARBELL,
    mediaUrl = "",
    instructions = "Mantén la espalda recta y baja de forma controlada.",
    riskFactors = setOf(RiskFactor.SPINAL_AXIAL_LOAD),
)

internal val previewRecommendations = listOf(
    ExerciseRecommendation(
        exercise = previewExercise,
        warnings = listOf(
            ExerciseWarning(
                exerciseId = previewExercise.id,
                riskFactor = RiskFactor.SPINAL_AXIAL_LOAD,
                message = "Carga axial alta: evita si tienes dolor lumbar",
                severity = ExerciseWarning.Severity.CAUTION,
            ),
        ),
    ),
    ExerciseRecommendation(
        exercise = previewExercise.copy(id = "2", name = "Press de banca", equipment = Equipment.MACHINE, riskFactors = emptySet()),
        warnings = emptyList(),
    ),
)

@Preview(showBackground = true)
@Composable
private fun ExerciseCatalogScreenPreview() {
    BlazeTheme {
        ExerciseCatalogContent(previewRecommendations, onExerciseClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun WarningBadgePreview() {
    BlazeTheme {
        WarningBadge(previewRecommendations.first().warnings.first())
    }
}
