package com.blaze.fitness.ui.auth

import android.provider.Settings
import android.util.Patterns
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blaze.fitness.domain.model.User
import com.blaze.fitness.ui.theme.BlazeOrange
import com.blaze.fitness.ui.theme.BlazeOrangeDark
import com.blaze.fitness.ui.theme.BlazeTheme
import com.blaze.fitness.ui.theme.BlazeWarning

// Deep, near-black background so the ember accent reads as the only warm light in the frame.
private val LoginBackground = Color(0xFF0A0D13)
private val LoginSurface = Color(0xFF151B26)
private val LoginBorderRest = Color(0xFF2A3140)
private val LoginTextMuted = Color(0xFF97A1B5)
private val LoginTextFaint = Color(0xFF5E6779)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(state.loggedInUser) {
        state.loggedInUser?.let(onLoginSuccess)
    }

    LoginContent(
        state = state,
        onLogin = viewModel::login,
        onNavigateToRegister = onNavigateToRegister,
    )
}

@Composable
private fun LoginContent(
    state: AuthUiState,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var submitAttempted by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val emailError = validateEmail(email, emailTouched || submitAttempted)
    val passwordError = validatePassword(password, passwordTouched || submitAttempted)
    val reducedMotion = rememberReducedMotionPreferred()

    Scaffold(containerColor = LoginBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LoginBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                EmberGlow(reducedMotion = reducedMotion)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = BlazeOrange,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(6.dp)
                    Text(
                        text = "BLAZE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        letterSpacing = (-0.8).sp,
                    )
                }
            }

            Spacer(28.dp)

            Text(
                text = "Volviste.\nEso ya cuenta.",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp,
            )

            Spacer(10.dp)

            Text(
                text = "Retoma tu rutina, un entreno a la vez. Nadie te pide perfección hoy, solo que aparezcas.",
                color = LoginTextMuted,
                fontSize = 16.sp,
                lineHeight = 23.sp,
            )

            Spacer(32.dp)

            LoginTextField(
                value = email,
                onValueChange = { email = it },
                onFocusLost = { emailTouched = true },
                label = "Correo",
                placeholder = "tu@correo.com",
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )

            Spacer(18.dp)

            LoginTextField(
                value = password,
                onValueChange = { password = it },
                onFocusLost = { passwordTouched = true },
                label = "Contraseña",
                placeholder = "Tu contraseña",
                errorMessage = passwordError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = LoginTextMuted,
                        )
                    }
                },
            )

            AnimatedVisibility(visible = state.error != null) {
                Column {
                    Spacer(18.dp)
                    AuthErrorBanner(message = state.error.orEmpty())
                }
            }

            Spacer(28.dp)

            val formHasErrors = emailError != null || passwordError != null || email.isBlank() || password.isBlank()

            EmberButton(
                text = "Entrar",
                loadingText = "Entrando…",
                onClick = {
                    submitAttempted = true
                    emailTouched = true
                    passwordTouched = true
                    if (!formHasErrors) onLogin(email, password)
                },
                isLoading = state.isLoading,
                enabled = !state.isLoading,
            )

            Spacer(20.dp)

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text(text = "¿Primera vez por aquí? ", color = LoginTextFaint, fontSize = 14.sp)
                Text(text = "Crea tu cuenta", color = BlazeOrange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun EmberGlow(reducedMotion: Boolean, modifier: Modifier = Modifier) {
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
            .size(140.dp)
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
private fun LoginTextField(
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
            color = if (errorMessage != null) BlazeWarning else if (isFocused) BlazeOrange else LoginTextFaint,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp,
        )
        Spacer(6.dp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = LoginTextFaint) },
            isError = errorMessage != null,
            singleLine = true,
            interactionSource = interactionSource,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(14.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LoginSurface,
                unfocusedContainerColor = LoginSurface,
                disabledContainerColor = LoginSurface,
                errorContainerColor = LoginSurface,
                focusedBorderColor = BlazeOrange,
                unfocusedBorderColor = LoginBorderRest,
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
                Spacer(4.dp)
                Text(text = errorMessage.orEmpty(), color = BlazeWarning, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AuthErrorBanner(message: String) {
    Surface(
        color = BlazeWarning.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
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
            Spacer(10.dp)
            Text(text = message, color = Color(0xFFFDE8C8), fontSize = 14.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun EmberButton(
    text: String,
    loadingText: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
) {
    val interactive = enabled && !isLoading
    Surface(
        color = if (interactive) BlazeOrange else BlazeOrange.copy(alpha = 0.4f),
        contentColor = Color(0xFF1A0E08),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        enabled = interactive,
        modifier = Modifier
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
                    Spacer(10.dp)
                    Text(text = loadingText, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            } else {
                Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 0.3.sp)
            }
        }
    }
}

@Composable
private fun Spacer(height: Dp) {
    Box(modifier = Modifier.size(width = 0.dp, height = height))
}

@Composable
private fun rememberReducedMotionPreferred(): Boolean {
    val context = LocalContext.current
    return remember {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        }.getOrDefault(1f) == 0f
    }
}

private fun validateEmail(email: String, shouldValidate: Boolean): String? {
    if (!shouldValidate) return null
    return when {
        email.isBlank() -> "Te falta tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Revisa tu correo"
        else -> null
    }
}

private fun validatePassword(password: String, shouldValidate: Boolean): String? {
    if (!shouldValidate) return null
    return if (password.isBlank()) "Te falta la contraseña" else null
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0D13)
@Composable
private fun LoginScreenPreview() {
    BlazeTheme(darkTheme = true) {
        LoginContent(state = AuthUiState(), onLogin = { _, _ -> }, onNavigateToRegister = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0D13, name = "Con error")
@Composable
private fun LoginScreenErrorPreview() {
    BlazeTheme(darkTheme = true) {
        LoginContent(
            state = AuthUiState(error = "Correo o contraseña incorrectos. Intenta otra vez."),
            onLogin = { _, _ -> },
            onNavigateToRegister = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0D13, name = "Cargando")
@Composable
private fun LoginScreenLoadingPreview() {
    BlazeTheme(darkTheme = true) {
        LoginContent(state = AuthUiState(isLoading = true), onLogin = { _, _ -> }, onNavigateToRegister = {})
    }
}
