package com.example.aplicacion1_entrega_def.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion1_entrega_def.ui.auth.LoginScreen
import com.example.aplicacion1_entrega_def.ui.auth.RegisterScreen
import com.example.aplicacion1_entrega_def.ui.screens.*
import com.example.aplicacion1_entrega_def.ui.viewmodel.MealViewModel
import com.example.aplicacion1_entrega_def.ui.viewmodel.MealViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<MealViewModel>(
        factory = MealViewModelFactory.factory
    )

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }}
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = { navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }}
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("meal_detail/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId")
            if (mealId != null) {
                MealDetailScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }

        composable("favorites") {
            FavoritesScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("completed") {
            CompletedScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
} 