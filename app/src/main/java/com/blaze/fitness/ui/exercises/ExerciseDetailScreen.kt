package com.blaze.fitness.ui.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.blaze.fitness.domain.agent.ExerciseRecommendation
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.ui.theme.BlazeTheme

@Composable
fun ExerciseDetailScreen(
    viewModel: ExerciseCatalogViewModel,
    exerciseId: String,
) {
    val recommendations by viewModel.recommendations.collectAsState()
    val recommendation = recommendations.find { it.exercise.id == exerciseId }
    ExerciseDetailContent(recommendation)
}

@Composable
private fun ExerciseDetailContent(recommendation: ExerciseRecommendation?) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (recommendation == null) {
                Text("Cargando ejercicio…")
                return@Column
            }

            val exercise: Exercise = recommendation.exercise
            AsyncImage(
                model = exercise.mediaUrl,
                contentDescription = exercise.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )

            Text(exercise.name, style = MaterialTheme.typography.headlineMedium)
            Text("${exercise.muscleGroup} · ${exercise.equipment}", style = MaterialTheme.typography.bodyMedium)

            recommendation.warnings.forEach { warning -> WarningBadge(warning) }

            Text("Instrucciones", style = MaterialTheme.typography.titleMedium)
            Text(exercise.instructions, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseDetailScreenPreview() {
    BlazeTheme {
        ExerciseDetailContent(previewRecommendations.first())
    }
}
