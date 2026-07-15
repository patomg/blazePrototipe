package com.blaze.fitness.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blaze.fitness.domain.model.User
import com.blaze.fitness.ui.common.BlazeTextField
import com.blaze.fitness.ui.common.PrimaryButton
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

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            Text("Blaze", style = MaterialTheme.typography.headlineLarge)
            Text("Inicia sesión para continuar tu progreso", style = MaterialTheme.typography.bodyMedium)

            BlazeTextField(value = email, onValueChange = { email = it }, label = "Correo")
            BlazeTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation(),
            )

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            PrimaryButton(
                text = "Iniciar sesión",
                onClick = { onLogin(email, password) },
                isLoading = state.isLoading,
            )

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BlazeTheme {
        LoginContent(state = AuthUiState(), onLogin = { _, _ -> }, onNavigateToRegister = {})
    }
}

@Preview(showBackground = true, name = "Con error")
@Composable
private fun LoginScreenErrorPreview() {
    BlazeTheme {
        LoginContent(
            state = AuthUiState(error = "Correo o contraseña incorrectos"),
            onLogin = { _, _ -> },
            onNavigateToRegister = {},
        )
    }
}
