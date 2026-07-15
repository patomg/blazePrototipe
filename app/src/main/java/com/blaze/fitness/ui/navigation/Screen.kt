package com.blaze.fitness.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Questionnaire : Screen("questionnaire")
    data object Dashboard : Screen("dashboard")
    data object ExerciseCatalog : Screen("exercises")
    data object ExerciseDetail : Screen("exercises/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercises/$exerciseId"
    }
    data object Workout : Screen("workout")
    data object Nutrition : Screen("nutrition")

    companion object {
        const val ARG_EXERCISE_ID = "exerciseId"
    }
}
