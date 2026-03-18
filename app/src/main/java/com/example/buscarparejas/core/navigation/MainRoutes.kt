package com.example.buscarparejas.core.navigation

sealed class MainRoutes(val route: String) {
    object Home : MainRoutes("home")
    object Game : MainRoutes("game/{name}") {
        fun createRoute(name: String) = "game/$name"
    }
    object Results : MainRoutes("results/{name}/{score}") {
        fun createRoute(name: String, score: Int) = "results/$name/$score"
    }
}