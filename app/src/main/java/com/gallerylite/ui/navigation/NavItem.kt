package com.gallerylite.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Photos : Screen("photos")
    object Albums : Screen("albums")
    object Favorites : Screen("favorites")
    object OnThisDay : Screen("on_this_day")
    object Viewer : Screen("viewer/{mediaId}") {
        fun createRoute(mediaId: Long) = "viewer/$mediaId"
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Photos : BottomNavItem("photos", "Photos", Icons.Rounded.Collections)
    object Albums : BottomNavItem("albums", "Albums", Icons.Rounded.PhotoLibrary)
    object Favorites : BottomNavItem("favorites", "Favorites", Icons.Rounded.Favorite)
    object OnThisDay : BottomNavItem("on_this_day", "On This Day", Icons.Rounded.History)
}

val bottomNavItems = listOf(
    BottomNavItem.Photos,
    BottomNavItem.Albums,
    BottomNavItem.Favorites,
    BottomNavItem.OnThisDay
)
