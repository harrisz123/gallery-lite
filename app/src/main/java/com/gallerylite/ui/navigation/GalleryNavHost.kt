package com.gallerylite.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gallerylite.data.model.MediaItem
import com.gallerylite.data.repository.FavoritesRepository
import com.gallerylite.data.repository.MediaRepository
import com.gallerylite.ui.components.FloatingNavBar
import com.gallerylite.ui.screens.albums.AlbumsScreen
import com.gallerylite.ui.screens.albums.AlbumsViewModel
import com.gallerylite.ui.screens.favorites.FavoritesScreen
import com.gallerylite.ui.screens.favorites.FavoritesViewModel
import com.gallerylite.ui.screens.onthisday.OnThisDayScreen
import com.gallerylite.ui.screens.onthisday.OnThisDayViewModel
import com.gallerylite.ui.screens.photos.PhotosScreen
import com.gallerylite.ui.screens.photos.PhotosViewModel
import com.gallerylite.ui.screens.viewer.ViewerScreen

@Composable
fun GalleryNavHost(
    navController: NavHostController,
    photosViewModel: PhotosViewModel,
    albumsViewModel: AlbumsViewModel,
    favoritesViewModel: FavoritesViewModel,
    onThisDayViewModel: OnThisDayViewModel,
    favoritesRepository: FavoritesRepository,
    mediaRepository: MediaRepository,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Photos.route
    var selectedMediaItem: MediaItem? = remember { null }

    val isTopLevelRoute = currentRoute in listOf(
        Screen.Photos.route,
        Screen.Albums.route,
        Screen.Favorites.route,
        Screen.OnThisDay.route
    )

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Photos.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Photos.route) {
                PhotosScreen(
                    viewModel = photosViewModel,
                    onMediaClick = { item ->
                        selectedMediaItem = item
                        navController.navigate(Screen.Viewer.createRoute(item.id))
                    }
                )
            }

            composable(Screen.Albums.route) {
                AlbumsScreen(
                    viewModel = albumsViewModel,
                    onAlbumClick = { /* Navigate to album detail if needed */ }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onMediaClick = { item ->
                        selectedMediaItem = item
                        navController.navigate(Screen.Viewer.createRoute(item.id))
                    }
                )
            }

            composable(Screen.OnThisDay.route) {
                OnThisDayScreen(
                    viewModel = onThisDayViewModel,
                    onMediaClick = { item ->
                        selectedMediaItem = item
                        navController.navigate(Screen.Viewer.createRoute(item.id))
                    }
                )
            }

            composable(Screen.Viewer.route) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId")?.toLongOrNull() ?: 0L
                val item = selectedMediaItem ?: remember(mediaId) {
                    // Fallback to stub or cached lookup
                    null
                }

                if (item != null) {
                    ViewerScreen(
                        mediaItem = item,
                        favoritesRepository = favoritesRepository,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        // Show floating navigation bar only on top-level tabs
        if (isTopLevelRoute) {
            FloatingNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
