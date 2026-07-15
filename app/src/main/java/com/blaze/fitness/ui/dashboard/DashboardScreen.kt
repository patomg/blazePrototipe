package com.blaze.fitness.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaze.fitness.ui.theme.BlazeTheme

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenExercises: () -> Unit,
    onOpenNutrition: () -> Unit,
    onStartWorkout: () -> Unit,
    onLogout: () -> Unit,
) {
    val state = viewModel.uiState
    DashboardContent(state, onOpenExercises, onOpenNutrition, onStartWorkout, onLogout)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    state: DashboardUiState,
    onOpenExercises: () -> Unit,
    onOpenNutrition: () -> Unit,
    onStartWorkout: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blaze") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Tu progreso", style = MaterialTheme.typography.headlineMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Esta semana", "${state.workoutsThisWeek}", "entrenos", Modifier.weight(1f))
                StatCard("Racha", "${state.currentStreakDays}", "días", Modifier.weight(1f))
                StatCard("Total", "${state.totalSessions}", "sesiones", Modifier.weight(1f))
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Consejo de nutrición", style = MaterialTheme.typography.titleMedium)
                    Text(state.nutritionTip, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onStartWorkout)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Comenzar entrenamiento", style = MaterialTheme.typography.titleMedium)
                    Text("Rutina generada según tu perfil y progreso", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenExercises)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Catálogo de ejercicios", style = MaterialTheme.typography.titleMedium)
                    Text("Videos y GIFs con advertencias personalizadas", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenNutrition)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nutrición y superación", style = MaterialTheme.typography.titleMedium)
                    Text("Consejos para acompañar tu entrenamiento", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text(unit, style = MaterialTheme.typography.labelMedium)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    BlazeTheme {
        DashboardContent(
            state = DashboardUiState(
                workoutsThisWeek = 3,
                currentStreakDays = 5,
                totalSessions = 42,
                nutritionTip = "Hidrátate antes, durante y después de entrenar.",
            ),
            onOpenExercises = {},
            onOpenNutrition = {},
            onStartWorkout = {},
            onLogout = {},
        )
    }
}
