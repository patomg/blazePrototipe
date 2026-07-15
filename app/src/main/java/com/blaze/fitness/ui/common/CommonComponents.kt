package com.blaze.fitness.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaze.fitness.domain.model.ExerciseWarning
import com.blaze.fitness.ui.theme.BlazeTheme

@Composable
fun BlazeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    Button(onClick = onClick, enabled = enabled && !isLoading, modifier = modifier.fillMaxWidth()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
            Text(text)
        }
    }
}

@Composable
fun WarningSeverityColor(severity: ExerciseWarning.Severity) = when (severity) {
    ExerciseWarning.Severity.INFO -> MaterialTheme.colorScheme.secondary
    ExerciseWarning.Severity.CAUTION -> com.blaze.fitness.ui.theme.BlazeWarning
    ExerciseWarning.Severity.AVOID -> com.blaze.fitness.ui.theme.BlazeDanger
}

@Preview(showBackground = true)
@Composable
private fun CommonComponentsPreview() {
    BlazeTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BlazeTextField(value = "correo@ejemplo.com", onValueChange = {}, label = "Correo")
            PrimaryButton(text = "Continuar", onClick = {})
            PrimaryButton(text = "Cargando", onClick = {}, isLoading = true)
        }
    }
}
