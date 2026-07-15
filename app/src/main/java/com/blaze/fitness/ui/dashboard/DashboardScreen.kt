package com.blaze.fitness.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blaze.fitness.ui.theme.BlazeInkBackground
import com.blaze.fitness.ui.theme.BlazeInkSurfaceRaised
import com.blaze.fitness.ui.theme.BlazeInkTextFaint
import com.blaze.fitness.ui.theme.BlazeInkTextMuted
import com.blaze.fitness.ui.theme.BlazeOrange
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
        containerColor = BlazeInkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Blaze", color = Color.White, fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlazeInkBackground),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = BlazeInkTextMuted,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BlazeInkBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Tu progreso",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                letterSpacing = (-0.4).sp,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Esta semana", "${state.workoutsThisWeek}", "entrenos", Modifier.weight(1f))
                StatCard("Racha", "${state.currentStreakDays}", "días", Modifier.weight(1f))
                StatCard("Total kg", formatVolume(state.totalVolumeKg), "movidos", Modifier.weight(1f))
            }

            WeeklyVolumeChart(days = state.dailyVolumes)

            VolumeTrendChart(volumes = state.volumeTrend)

            EmberActionCard(
                title = "Comenzar entrenamiento",
                subtitle = "Rutina generada según tu perfil y progreso",
                onClick = onStartWorkout,
                highlighted = true,
            )

            EmberActionCard(
                title = "Catálogo de ejercicios",
                subtitle = "Videos y GIFs con advertencias personalizadas",
                onClick = onOpenExercises,
            )

            EmberActionCard(
                title = "Nutrición y superación",
                subtitle = "Consejos para acompañar tu entrenamiento",
                onClick = onOpenNutrition,
            )

            Surface(
                color = BlazeInkSurfaceRaised,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Consejo de nutrición", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Box(modifier = Modifier.padding(top = 4.dp)) {
                        Text(state.nutritionTip, color = BlazeInkTextMuted, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }

            Box(modifier = Modifier.padding(bottom = 12.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Surface(
        color = BlazeInkSurfaceRaised,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                value,
                color = BlazeOrange,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
            )
            Text(unit, color = BlazeInkTextFaint, fontSize = 11.sp)
            Box(modifier = Modifier.padding(top = 4.dp))
            Text(label, color = BlazeInkTextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun EmberActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    highlighted: Boolean = false,
) {
    Surface(
        color = if (highlighted) BlazeOrange else BlazeInkSurfaceRaised,
        shape = RoundedCornerShape(18.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                color = if (highlighted) Color(0xFF1A0E08) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Box(modifier = Modifier.padding(top = 4.dp))
            Text(
                subtitle,
                color = if (highlighted) Color(0xFF1A0E08).copy(alpha = 0.75f) else BlazeInkTextMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

private fun formatVolume(volumeKg: Float): String = when {
    volumeKg >= 1000f -> "%.1fk".format(volumeKg / 1000f)
    else -> volumeKg.toInt().toString()
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0D13)
@Composable
private fun DashboardScreenPreview() {
    BlazeTheme(darkTheme = true) {
        DashboardContent(
            state = DashboardUiState(
                workoutsThisWeek = 3,
                currentStreakDays = 5,
                totalSessions = 42,
                totalVolumeKg = 12840f,
                dailyVolumes = listOf(
                    DailyVolume("L", 820f, false),
                    DailyVolume("M", 0f, false),
                    DailyVolume("X", 1240f, false),
                    DailyVolume("J", 640f, false),
                    DailyVolume("V", 1560f, false),
                    DailyVolume("S", 0f, false),
                    DailyVolume("D", 980f, true),
                ),
                volumeTrend = listOf(2100f, 2400f, 2200f, 2650f, 2500f, 2900f, 3100f, 2980f),
                nutritionTip = "Hidrátate antes, durante y después de entrenar.",
            ),
            onOpenExercises = {},
            onOpenNutrition = {},
            onStartWorkout = {},
            onLogout = {},
        )
    }
}
