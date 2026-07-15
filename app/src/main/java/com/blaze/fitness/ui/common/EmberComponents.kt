package com.blaze.fitness.ui.common

import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blaze.fitness.ui.theme.BlazeInkBorder
import com.blaze.fitness.ui.theme.BlazeInkSurface
import com.blaze.fitness.ui.theme.BlazeInkTextFaint
import com.blaze.fitness.ui.theme.BlazeInkTextMuted
import com.blaze.fitness.ui.theme.BlazeOrange
import com.blaze.fitness.ui.theme.BlazeOrangeDark
import com.blaze.fitness.ui.theme.BlazeWarning

/**
 * Shared "ember" identity: deep dark background, a single burnt-orange accent, and a
 * pulsing glow signature. Used by the login, register, and dashboard screens so they
 * read as one coherent product rather than three different demos.
 */

@Composable
fun EmberGlow(reducedMotion: Boolean, modifier: Modifier = Modifier, size: Dp = 140.dp) {
    val infinite = rememberInfiniteTransition(label = "emberPulse")
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "emberPulseValue",
    )
    Box(
        modifier = modifier
            .size(size)
            .scale(1f + pulse * 0.14f)
            .alpha(0.28f + pulse * 0.32f)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(BlazeOrange.copy(alpha = 0.85f), BlazeOrangeDark.copy(alpha = 0f)),
                ),
                shape = CircleShape,
            ),
    )
}

@Composable
fun rememberReducedMotionPreferred(): Boolean {
    val context = LocalContext.current
    return remember {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        }.getOrDefault(1f) == 0f
    }
}

@Composable
fun EmberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
    label: String,
    placeholder: String,
    errorMessage: String?,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var wasFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            color = if (errorMessage != null) BlazeWarning else if (isFocused) BlazeOrange else BlazeInkTextFaint,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp,
        )
        Box(modifier = Modifier.size(width = 0.dp, height = 6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = BlazeInkTextFaint) },
            isError = errorMessage != null,
            singleLine = true,
            interactionSource = interactionSource,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(14.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BlazeInkSurface,
                unfocusedContainerColor = BlazeInkSurface,
                disabledContainerColor = BlazeInkSurface,
                errorContainerColor = BlazeInkSurface,
                focusedBorderColor = BlazeOrange,
                unfocusedBorderColor = BlazeInkBorder,
                errorBorderColor = BlazeWarning,
                cursorColor = BlazeOrange,
                errorCursorColor = BlazeOrange,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .onFocusChanged { focusState ->
                    if (wasFocused && !focusState.isFocused) onFocusLost()
                    wasFocused = focusState.isFocused
                },
        )
        AnimatedVisibility(visible = errorMessage != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp, start = 2.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    tint = BlazeWarning,
                    modifier = Modifier.size(14.dp),
                )
                Box(modifier = Modifier.size(width = 4.dp, height = 0.dp))
                Text(text = errorMessage.orEmpty(), color = BlazeWarning, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EmberBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        color = BlazeWarning.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = BlazeWarning,
                modifier = Modifier.size(18.dp),
            )
            Box(modifier = Modifier.size(width = 10.dp, height = 0.dp))
            Text(text = message, color = Color(0xFFFDE8C8), fontSize = 14.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
fun EmberPrimaryButton(
    text: String,
    loadingText: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactive = enabled && !isLoading
    Surface(
        color = if (interactive) BlazeOrange else BlazeOrange.copy(alpha = 0.4f),
        contentColor = Color(0xFF1A0E08),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        enabled = interactive,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF1A0E08),
                    )
                    Box(modifier = Modifier.size(width = 10.dp, height = 0.dp))
                    Text(text = loadingText, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            } else {
                Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 0.3.sp)
            }
        }
    }
}

@Composable
fun EmberTextMuted(text: String, modifier: Modifier = Modifier) {
    Text(text = text, color = BlazeInkTextMuted, fontSize = 16.sp, lineHeight = 23.sp, modifier = modifier)
}
