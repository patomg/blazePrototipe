package com.blaze.fitness.di

import android.content.Context
import androidx.room.Room
import com.blaze.fitness.data.local.AppDatabase
import com.blaze.fitness.data.local.TokenManager
import com.blaze.fitness.data.remote.ApiService
import com.blaze.fitness.data.remote.RetrofitClient
import com.blaze.fitness.data.repository.AuthRepository
import com.blaze.fitness.data.repository.ExerciseRepository
import com.blaze.fitness.data.repository.QuestionnaireRepository
import com.blaze.fitness.data.repository.WorkoutRepository
import com.blaze.fitness.domain.agent.FitnessAgent

/**
 * Hand-rolled dependency container (no DI framework) so the module graph stays explicit
 * and buildable without an annotation processor.
 */
class AppContainer(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME,
    ).build()

    val tokenManager: TokenManager = TokenManager(context.applicationContext)

    private val apiService: ApiService = RetrofitClient.create(tokenManager)

    val fitnessAgent: FitnessAgent = FitnessAgent()

    val authRepository: AuthRepository = AuthRepository(apiService, tokenManager, database.userDao())

    val questionnaireRepository: QuestionnaireRepository =
        QuestionnaireRepository(apiService, database.questionnaireDao())

    val exerciseRepository: ExerciseRepository = ExerciseRepository(apiService, database.exerciseDao())

    val workoutRepository: WorkoutRepository =
        WorkoutRepository(apiService, database.workoutDao(), fitnessAgent)
}
