package com.blaze.fitness.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blaze.fitness.data.repository.AuthRepository
import com.blaze.fitness.domain.model.User
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: User? = null,
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Ingresa tu correo y contraseña")
            return
        }
        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            runCatching { authRepository.login(email, password) }
                .onSuccess { user -> uiState = uiState.copy(isLoading = false, loggedInUser = user) }
                .onFailure { e -> uiState = uiState.copy(isLoading = false, error = e.message ?: "No se pudo iniciar sesión") }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.length < 8) {
            uiState = uiState.copy(error = "Completa tus datos; la contraseña debe tener al menos 8 caracteres")
            return
        }
        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            runCatching { authRepository.register(name, email, password) }
                .onSuccess { user -> uiState = uiState.copy(isLoading = false, loggedInUser = user) }
                .onFailure { e -> uiState = uiState.copy(isLoading = false, error = e.message ?: "No se pudo crear la cuenta") }
        }
    }

    fun consumeError() {
        uiState = uiState.copy(error = null)
    }

    fun logout() {
        authRepository.logout()
        uiState = AuthUiState()
    }
}
