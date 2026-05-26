package com.sanjoy.videoplayer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sanjoy.videoplayer.core.permission.VideoPermissionHandler
import com.sanjoy.videoplayer.presentation.home.HomeScreen
import com.sanjoy.videoplayer.presentation.splash.SplashScreen

/**
 * Main navigation graph of the app.
 */
@Composable
fun VideoPlayerNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Permission.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Permission.route) {
            VideoPermissionHandler(
                onPermissionGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Permission.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onVideoClick = {
                    // We will implement actual video playback in the next step.
                }
            )
        }
    }
}