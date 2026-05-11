package com.memeforge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.memeforge.ui.editor.EditorScreen
import com.memeforge.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Editor : Screen("editor/{templateId}") {
        fun createRoute(templateId: String) = "editor/$templateId"
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onTemplateClick = { templateId ->
                    navController.navigate(Screen.Editor.createRoute(templateId))
                }
            )
        }
        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("templateId") { type = NavType.StringType })
        ) { backStackEntry ->
            val templateId = backStackEntry.arguments?.getString("templateId") ?: return@composable
            EditorScreen(
                templateId = templateId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
