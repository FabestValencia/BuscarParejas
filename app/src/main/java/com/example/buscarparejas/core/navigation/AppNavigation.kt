package com.example.buscarparejas.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.buscarparejas.features.home.HomeScreen
import com.example.buscarparejas.features.game.GameScreen
import com.example.buscarparejas.features.results.ResultsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home.route
    ) {
        composable(MainRoutes.Home.route) {
            HomeScreen(onNavigateToGame = { name ->
                navController.navigate(MainRoutes.Game.createRoute(name))
            })
        }
        composable(
            route = MainRoutes.Game.route,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            GameScreen(
                name = name,
                onExit = {
                    navController.popBackStack()
                },
                onNavigateToResults = { finalName, score ->
                    navController.navigate(MainRoutes.Results.createRoute(finalName, score))
                }
            )
        }
        composable(
            route = MainRoutes.Results.route,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            ResultsScreen(
                name = name,
                score = score,
                onRestart = {
                    navController.navigate(MainRoutes.Home.route) {
                        popUpTo(MainRoutes.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
