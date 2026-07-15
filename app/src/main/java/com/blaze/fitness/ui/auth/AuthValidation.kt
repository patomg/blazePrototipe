package com.blaze.fitness.ui.auth

import android.util.Patterns

internal fun validateName(name: String, shouldValidate: Boolean): String? {
    if (!shouldValidate) return null
    return if (name.isBlank()) "Te falta tu nombre" else null
}

internal fun validateEmail(email: String, shouldValidate: Boolean): String? {
    if (!shouldValidate) return null
    return when {
        email.isBlank() -> "Te falta tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Revisa tu correo"
        else -> null
    }
}

internal fun validatePassword(password: String, shouldValidate: Boolean, minLength: Int = 1): String? {
    if (!shouldValidate) return null
    return when {
        password.isBlank() -> "Te falta la contraseña"
        password.length < minLength -> "Necesita al menos $minLength caracteres"
        else -> null
    }
}
