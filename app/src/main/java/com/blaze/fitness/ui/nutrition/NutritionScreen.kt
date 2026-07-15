package com.blaze.fitness.ui.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaze.fitness.ui.theme.BlazeTheme

private val nutritionTips = listOf(
    "Distribuye tu proteína a lo largo del día (~0.3 g/kg por comida) para maximizar la síntesis muscular.",
    "Los carbohidratos antes de entrenar sostienen la intensidad; no los elimines por completo.",
    "Las grasas saludables (aceite de oliva, frutos secos, palta) apoyan la producción hormonal.",
    "El progreso no es lineal: una semana sin resultados visibles no invalida el esfuerzo.",
    "Registrar tus entrenamientos te ayuda a ver el avance real, más allá del ánimo del día.",
    "Duerme 7-9 horas: es cuando ocurre la mayor parte de la recuperación muscular.",
)

@Composable
fun NutritionScreen() {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(nutritionTips) { tip ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(tip, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NutritionScreenPreview() {
    BlazeTheme {
        NutritionScreen()
    }
}
