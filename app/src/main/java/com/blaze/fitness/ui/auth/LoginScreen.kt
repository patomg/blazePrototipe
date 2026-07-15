package com.blaze.fitness.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blaze.fitness.domain.model.User
import com.blaze.fitness.ui.common.EmberBanner
import com.blaze.fitness.ui.common.EmberGlow
import com.blaze.fitness.ui.common.EmberPrimaryButton
import com.blaze.fitness.ui.common.EmberTextField
import com.blaze.fitness.ui.common.EmberTextMuted
import com.blaze.fitness.ui.common.rememberReducedMotionPreferred
import com.blaze.fitness.ui.theme.BlazeInkBackground
import com.blaze.fitness.ui.theme.BlazeInkTextFaint
import com.blaze.fitness.ui.theme.BlazeOrange
import com.blaze.fitness.ui.theme.BlazeTheme

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

    Scaffold(containerColor = BlazeInkBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BlazeInkBackground)
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
                    Box(modifier = Modifier.size(width = 6.dp, height = 0.dp))
                    Text(
                        text = "BLAZE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        letterSpacing = (-0.8).sp,
                    )
                }
            }

            Box(modifier = Modifier.size(width = 0.dp, height = 28.dp))

            Text(
                text = "Volviste.\nEso ya cuenta.",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp,
            )

            Box(modifier = Modifier.size(width = 0.dp, height = 10.dp))

            EmberTextMuted("Retoma tu rutina, un entreno a la vez. Nadie te pide perfección hoy, solo que aparezcas.")

            Box(modifier = Modifier.size(width = 0.dp, height = 32.dp))

            EmberTextField(
                value = email,
                onValueChange = { email = it },
                onFocusLost = { emailTouched = true },
                label = "Correo",
                placeholder = "tu@correo.com",
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )

            Box(modifier = Modifier.size(width = 0.dp, height = 18.dp))

            EmberTextField(
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
                            tint = BlazeInkTextFaint,
                        )
                    }
                },
            )

            AnimatedVisibility(visible = state.error != null) {
                Column {
                    Box(modifier = Modifier.size(width = 0.dp, height = 18.dp))
                    EmberBanner(message = state.error.orEmpty())
                }
            }

            Box(modifier = Modifier.size(width = 0.dp, height = 28.dp))

            val formHasErrors = emailError != null || passwordError != null || email.isBlank() || password.isBlank()

            EmberPrimaryButton(
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

            Box(modifier = Modifier.size(width = 0.dp, height = 20.dp))

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text(text = "¿Primera vez por aquí? ", color = BlazeInkTextFaint, fontSize = 14.sp)
                Text(text = "Crea tu cuenta", color = BlazeOrange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
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
