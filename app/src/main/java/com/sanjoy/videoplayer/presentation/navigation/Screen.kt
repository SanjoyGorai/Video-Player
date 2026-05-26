package com.sanjoy.videoplayer.presentation.navigation

/**
 * All app routes are stored here.
 *
 * This prevents route spelling mistakes across the project.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Permission : Screen("permission")
    data object Home : Screen("home")
}