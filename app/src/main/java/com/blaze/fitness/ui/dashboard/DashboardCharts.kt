package com.blaze.fitness.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blaze.fitness.ui.theme.BlazeInkBorder
import com.blaze.fitness.ui.theme.BlazeInkSurfaceRaised
import com.blaze.fitness.ui.theme.BlazeInkTextFaint
import com.blaze.fitness.ui.theme.BlazeInkTextMuted
import com.blaze.fitness.ui.theme.BlazeOrange

/**
 * Dark, card-based stat charts inspired by Hevy's training log: a minimal weekly
 * volume bar strip and a session-to-session volume trend line, both grounded in the
 * user's real logged sets (reps x weight) rather than decorative sample data.
 */

@Composable
private fun ChartCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Surface(
        color = BlazeInkSurfaceRaised,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = BlazeInkTextMuted, fontSize = 13.sp)
            Box(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun WeeklyVolumeChart(days: List<DailyVolume>, modifier: Modifier = Modifier) {
    ChartCard(title = "Volumen semanal", subtitle = "Kilos totales movidos por día") {
        val maxVolume = (days.maxOfOrNull { it.volumeKg } ?: 0f).coerceAtLeast(1f)

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            val barCount = days.size.coerceAtLeast(1)
            val gap = 10.dp.toPx()
            val barWidth = (size.width - gap * (barCount - 1)) / barCount

            days.forEachIndexed { index, day ->
                val barHeight = (day.volumeKg / maxVolume) * size.height
                val left = index * (barWidth + gap)
                val top = size.height - barHeight
                val color = if (day.isToday) BlazeOrange else BlazeOrange.copy(alpha = 0.35f)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight.coerceAtLeast(3f)),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                )
            }
        }

        Box(modifier = Modifier.height(8.dp))

        DayLabelsRow(days)
    }
}

@Composable
private fun DayLabelsRow(days: List<DailyVolume>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = day.dayLabel,
                    color = if (day.isToday) BlazeOrange else BlazeInkTextFaint,
                    fontSize = 11.sp,
                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun VolumeTrendChart(volumes: List<Float>, modifier: Modifier = Modifier) {
    ChartCard(title = "Progreso de volumen", subtitle = "Tus últimas ${volumes.size} sesiones") {
        if (volumes.size < 2) {
            Box(modifier = modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.CenterStart) {
                Text(
                    "Registra dos o más entrenos y verás tu tendencia acá.",
                    color = BlazeInkTextFaint,
                    fontSize = 13.sp,
                )
            }
            return@ChartCard
        }

        val maxVolume = volumes.max().coerceAtLeast(1f)
        val minVolume = volumes.min().coerceAtMost(maxVolume - 1f)
        val range = (maxVolume - minVolume).coerceAtLeast(1f)

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(110.dp),
        ) {
            val stepX = size.width / (volumes.size - 1)
            val points = volumes.mapIndexed { index, volume ->
                val x = index * stepX
                val y = size.height - ((volume - minVolume) / range) * size.height
                Offset(x, y)
            }

            // Subtle horizontal guides, Hevy-style minimal grid.
            val guideY = listOf(0f, size.height / 2f, size.height)
            guideY.forEach { y ->
                drawLine(
                    color = BlazeInkBorder,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val current = points[i]
                    val midX = (prev.x + current.x) / 2f
                    cubicTo(midX, prev.y, midX, current.y, current.x, current.y)
                }
            }

            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(points.last().x, size.height)
                lineTo(points.first().x, size.height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(BlazeOrange.copy(alpha = 0.28f), BlazeOrange.copy(alpha = 0f)),
                ),
            )
            drawPath(
                path = linePath,
                color = BlazeOrange,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )

            points.forEachIndexed { index, point ->
                val isLast = index == points.lastIndex
                drawCircle(
                    color = if (isLast) BlazeOrange else BlazeOrange.copy(alpha = 0.55f),
                    radius = if (isLast) 5.dp.toPx() else 3.dp.toPx(),
                    center = point,
                )
            }
        }
    }
}
