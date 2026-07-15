package com.blaze.fitness.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaze.fitness.domain.model.Equipment
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.Goal
import com.blaze.fitness.domain.model.MuscleGroup
import com.blaze.fitness.domain.model.WorkoutExercise
import com.blaze.fitness.domain.model.WorkoutPlan
import com.blaze.fitness.domain.model.WorkoutSet
import com.blaze.fitness.ui.common.BlazeTextField
import com.blaze.fitness.ui.common.PrimaryButton
import com.blaze.fitness.ui.exercises.WarningBadge
import com.blaze.fitness.ui.theme.BlazeTheme

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    onFinished: () -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    WorkoutContent(
        state = state,
        onSetChange = viewModel::updateSet,
        onFinishSession = viewModel::finishSession,
    )
}

@Composable
private fun WorkoutContent(
    state: WorkoutUiState,
    onSetChange: (exerciseId: String, setNumber: Int, reps: Int, weightKg: Float, completed: Boolean) -> Unit,
    onFinishSession: () -> Unit,
) {
    Scaffold { padding ->
        if (state.isLoading) {
            Column(modifier = Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            }
            return@Scaffold
        }

        val plan = state.plan
        if (plan == null) {
            Text(
                "Completa el cuestionario para generar tu rutina.",
                modifier = Modifier.padding(padding).padding(24.dp),
            )
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(plan.title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(plan.exercises, key = { it.exercise.id }) { workoutExercise ->
                    WorkoutExerciseCard(workoutExercise, onSetChange = onSetChange)
                }
                item {
                    PrimaryButton(
                        text = "Finalizar entrenamiento",
                        onClick = onFinishSession,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutExerciseCard(
    workoutExercise: WorkoutExercise,
    onSetChange: (exerciseId: String, setNumber: Int, reps: Int, weightKg: Float, completed: Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(workoutExercise.exercise.name, style = MaterialTheme.typography.titleMedium)
            workoutExercise.warnings.forEach { WarningBadge(it) }

            workoutExercise.sets.forEach { set ->
                SetRow(
                    exerciseId = workoutExercise.exercise.id,
                    set = set,
                    onChange = onSetChange,
                )
            }
        }
    }
}

@Composable
private fun SetRow(
    exerciseId: String,
    set: WorkoutSet,
    onChange: (exerciseId: String, setNumber: Int, reps: Int, weightKg: Float, completed: Boolean) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("Serie ${set.setNumber}", modifier = Modifier.padding(top = 14.dp).width(72.dp))

        BlazeTextField(
            value = if (set.reps == 0) "" else set.reps.toString(),
            onValueChange = { value ->
                onChange(exerciseId, set.setNumber, value.toIntOrNull() ?: 0, set.weightKg, set.completed)
            },
            label = "Reps",
            modifier = Modifier.width(90.dp),
        )

        BlazeTextField(
            value = if (set.weightKg == 0f) "" else set.weightKg.toString(),
            onValueChange = { value ->
                onChange(exerciseId, set.setNumber, set.reps, value.toFloatOrNull() ?: 0f, set.completed)
            },
            label = "Peso (kg)",
            modifier = Modifier.width(100.dp),
        )

        Checkbox(
            checked = set.completed,
            onCheckedChange = { checked -> onChange(exerciseId, set.setNumber, set.reps, set.weightKg, checked) },
        )
    }
}

private val previewPlan = WorkoutPlan(
    id = "1",
    title = "Rutina de hoy: Tren superior",
    goal = Goal.GAIN_MUSCLE,
    exercises = listOf(
        WorkoutExercise(
            exercise = Exercise(
                id = "1",
                name = "Press de banca",
                muscleGroup = MuscleGroup.CHEST,
                equipment = Equipment.BARBELL,
                mediaUrl = "",
                instructions = "",
            ),
            sets = listOf(
                WorkoutSet(setNumber = 1, reps = 10, weightKg = 40f, completed = true),
                WorkoutSet(setNumber = 2, reps = 8, weightKg = 45f),
            ),
        ),
    ),
)

@Preview(showBackground = true)
@Composable
private fun WorkoutScreenPreview() {
    BlazeTheme {
        WorkoutContent(
            state = WorkoutUiState(plan = previewPlan, isLoading = false),
            onSetChange = { _, _, _, _, _ -> },
            onFinishSession = {},
        )
    }
}

@Preview(showBackground = true, name = "Cargando")
@Composable
private fun WorkoutScreenLoadingPreview() {
    BlazeTheme {
        WorkoutContent(
            state = WorkoutUiState(isLoading = true),
            onSetChange = { _, _, _, _, _ -> },
            onFinishSession = {},
        )
    }
}
