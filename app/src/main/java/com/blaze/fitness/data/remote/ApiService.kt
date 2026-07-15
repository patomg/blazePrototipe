package com.blaze.fitness.data.remote

import com.blaze.fitness.data.remote.dto.AuthResponse
import com.blaze.fitness.data.remote.dto.ExerciseDto
import com.blaze.fitness.data.remote.dto.LoginRequest
import com.blaze.fitness.data.remote.dto.QuestionnaireDto
import com.blaze.fitness.data.remote.dto.RegisterRequest
import com.blaze.fitness.data.remote.dto.UserDto
import com.blaze.fitness.data.remote.dto.WorkoutSessionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Endpoints are relative to the versioned base URL (BuildConfig.API_BASE_URL ends in
 * "/api/v1/"), so this file never repeats the version prefix.
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("users/me")
    suspend fun getCurrentUser(): UserDto

    @POST("questionnaire")
    suspend fun submitQuestionnaire(@Body questionnaire: QuestionnaireDto): QuestionnaireDto

    @GET("questionnaire/{userId}")
    suspend fun getQuestionnaire(@Path("userId") userId: String): QuestionnaireDto

    @GET("exercises")
    suspend fun getExercises(): List<ExerciseDto>

    @POST("workouts/sessions")
    suspend fun logWorkoutSession(@Body session: WorkoutSessionDto): WorkoutSessionDto

    @GET("workouts/sessions")
    suspend fun getWorkoutHistory(): List<WorkoutSessionDto>
}
