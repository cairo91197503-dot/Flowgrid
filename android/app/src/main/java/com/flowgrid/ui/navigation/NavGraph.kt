package com.flowgrid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.flowgrid.ui.screens.GameScreen
import com.flowgrid.ui.screens.HomeScreen
import com.flowgrid.ui.screens.SettingsScreen
import com.flowgrid.ui.screens.VictoryScreen
import com.flowgrid.ui.screens.PaywallScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        
        composable(
            route = "game/{mode}?seed={seed}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("seed") { 
                    type = NavType.IntType 
                    defaultValue = -1 
                }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "flowgrid://challenge?seed={seed}" })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "free"
            val argSeed = backStackEntry.arguments?.getInt("seed") ?: -1
            val modeOverride = if (argSeed != -1) "challenge" else mode
            val seed = if (argSeed != -1) argSeed else null
            GameScreen(navController, modeOverride, seed)
        }
        
        composable(
            route = "victory/{mode}/{seed}/{moves}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("seed") { type = NavType.IntType },
                navArgument("moves") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "free"
            val seed = backStackEntry.arguments?.getInt("seed") ?: 0
            val moves = backStackEntry.arguments?.getInt("moves") ?: 0
            VictoryScreen(navController, mode, seed, moves)
        }
        
        composable("settings") {
            SettingsScreen(navController)
        }

        composable("paywall") {
            PaywallScreen(navController)
        }
    }
}
