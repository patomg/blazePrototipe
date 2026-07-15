package com.blaze.fitness.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blaze.fitness.di.AppContainer
import com.blaze.fitness.ui.auth.AuthViewModel
import com.blaze.fitness.ui.auth.LoginScreen
import com.blaze.fitness.ui.auth.RegisterScreen
import com.blaze.fitness.ui.dashboard.DashboardScreen
import com.blaze.fitness.ui.dashboard.DashboardViewModel
import com.blaze.fitness.ui.exercises.ExerciseCatalogScreen
import com.blaze.fitness.ui.exercises.ExerciseCatalogViewModel
import com.blaze.fitness.ui.exercises.ExerciseDetailScreen
import com.blaze.fitness.ui.nutrition.NutritionScreen
import com.blaze.fitness.ui.questionnaire.QuestionnaireScreen
import com.blaze.fitness.ui.questionnaire.QuestionnaireViewModel
import com.blaze.fitness.ui.workout.WorkoutScreen
import com.blaze.fitness.ui.workout.WorkoutViewModel

@Composable
fun BlazeNavGraph(container: AppContainer) {
    val navController = rememberNavController()
    var currentUserId by rememberSaveable { mutableStateOf<String?>(null) }
    val startDestination = if (container.authRepository.isLoggedIn()) Screen.Dashboard.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            val viewModel = viewModel<AuthViewModel>(
                factory = viewModelFactory { initializer { AuthViewModel(container.authRepository) } },
            )
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { user ->
                    currentUserId = user.id
                    navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
            )
        }

        composable(Screen.Register.route) {
            val viewModel = viewModel<AuthViewModel>(
                factory = viewModelFactory { initializer { AuthViewModel(container.authRepository) } },
            )
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = { user ->
                    currentUserId = user.id
                    navController.navigate(Screen.Questionnaire.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
            )
        }

        composable(Screen.Questionnaire.route) {
            val userId = currentUserId ?: return@composable
            val viewModel = viewModel<QuestionnaireViewModel>(
                factory = viewModelFactory {
                    initializer { QuestionnaireViewModel(userId, container.questionnaireRepository) }
                },
            )
            QuestionnaireScreen(
                viewModel = viewModel,
                onCompleted = {
                    navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Questionnaire.route) { inclusive = true } }
                },
            )
        }

        composable(Screen.Dashboard.route) {
            val viewModel = viewModel<DashboardViewModel>(
                factory = viewModelFactory {
                    initializer { DashboardViewModel(container.workoutRepository, container.exerciseRepository) }
                },
            )
            val authViewModel = viewModel<AuthViewModel>(
                factory = viewModelFactory { initializer { AuthViewModel(container.authRepository) } },
            )
            DashboardScreen(
                viewModel = viewModel,
                onOpenExercises = { navController.navigate(Screen.ExerciseCatalog.route) },
                onOpenNutrition = { navController.navigate(Screen.Nutrition.route) },
                onStartWorkout = { navController.navigate(Screen.Workout.route) },
                onLogout = {
                    authViewModel.logout()
                    currentUserId = null
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                },
            )
        }

        composable(Screen.ExerciseCatalog.route) {
            val userId = currentUserId ?: ""
            val viewModel = viewModel<ExerciseCatalogViewModel>(
                factory = viewModelFactory {
                    initializer {
                        ExerciseCatalogViewModel(
                            userId,
                            container.exerciseRepository,
                            container.questionnaireRepository,
                            container.fitnessAgent,
                        )
                    }
                },
            )
            ExerciseCatalogScreen(
                viewModel = viewModel,
                onExerciseClick = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                },
            )
        }

        composable(Screen.ExerciseDetail.route) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString(Screen.ARG_EXERCISE_ID) ?: return@composable
            val userId = currentUserId ?: ""
            val viewModel = viewModel<ExerciseCatalogViewModel>(
                factory = viewModelFactory {
                    initializer {
                        ExerciseCatalogViewModel(
                            userId,
                            container.exerciseRepository,
                            container.questionnaireRepository,
                            container.fitnessAgent,
                        )
                    }
                },
            )
            ExerciseDetailScreen(viewModel = viewModel, exerciseId = exerciseId)
        }

        composable(Screen.Workout.route) {
            val userId = currentUserId ?: ""
            val viewModel = viewModel<WorkoutViewModel>(
                factory = viewModelFactory {
                    initializer {
                        WorkoutViewModel(
                            userId,
                            container.exerciseRepository,
                            container.questionnaireRepository,
                            container.workoutRepository,
                        )
                    }
                },
            )
            WorkoutScreen(viewModel = viewModel, onFinished = { navController.popBackStack() })
        }

        composable(Screen.Nutrition.route) {
            NutritionScreen()
        }
    }
}
