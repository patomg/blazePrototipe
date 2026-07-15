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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(state.loggedInUser) {
        state.loggedInUser?.let(onRegisterSuccess)
    }

    RegisterContent(
        state = state,
        onRegister = viewModel::register,
        onNavigateToLogin = onNavigateToLogin,
    )
}

@Composable
private fun RegisterContent(
    state: AuthUiState,
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
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
            Text("Crea tu cuenta", style = MaterialTheme.typography.headlineLarge)
            Text("Vamos a personalizar tus rutinas", style = MaterialTheme.typography.bodyMedium)

            BlazeTextField(value = name, onValueChange = { name = it }, label = "Nombre")
            BlazeTextField(value = email, onValueChange = { email = it }, label = "Correo")
            BlazeTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation(),
            )

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            PrimaryButton(
                text = "Crear cuenta",
                onClick = { onRegister(name, email, password) },
                isLoading = state.isLoading,
            )

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    BlazeTheme {
        RegisterContent(state = AuthUiState(), onRegister = { _, _, _ -> }, onNavigateToLogin = {})
    }
}
