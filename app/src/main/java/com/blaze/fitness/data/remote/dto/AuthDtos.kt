package com.blaze.fitness.data.remote.dto

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto,
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
)
