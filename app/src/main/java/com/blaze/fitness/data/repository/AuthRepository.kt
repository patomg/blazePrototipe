package com.blaze.fitness.data.repository

import com.blaze.fitness.data.local.TokenManager
import com.blaze.fitness.data.local.dao.UserDao
import com.blaze.fitness.data.local.entity.UserEntity
import com.blaze.fitness.data.remote.ApiService
import com.blaze.fitness.data.remote.dto.LoginRequest
import com.blaze.fitness.data.remote.dto.RegisterRequest
import com.blaze.fitness.data.remote.dto.toDomain
import com.blaze.fitness.domain.model.User

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager,
    private val userDao: UserDao,
) {
    suspend fun login(email: String, password: String): User {
        val response = api.login(LoginRequest(email = email, password = password))
        tokenManager.saveTokens(response.accessToken, response.refreshToken)
        userDao.upsert(UserEntity.fromDomain(response.user.toDomain()))
        return response.user.toDomain()
    }

    suspend fun register(name: String, email: String, password: String): User {
        val response = api.register(RegisterRequest(name = name, email = email, password = password))
        tokenManager.saveTokens(response.accessToken, response.refreshToken)
        userDao.upsert(UserEntity.fromDomain(response.user.toDomain()))
        return response.user.toDomain()
    }

    fun isLoggedIn(): Boolean = tokenManager.accessToken() != null

    fun logout() = tokenManager.clear()
}
