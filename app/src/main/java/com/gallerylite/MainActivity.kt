package com.gallerylite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.gallerylite.data.local.GalleryDatabase
import com.gallerylite.data.repository.AlbumRepository
import com.gallerylite.data.repository.FavoritesRepository
import com.gallerylite.data.repository.MediaRepository
import com.gallerylite.data.repository.OnThisDayRepository
import com.gallerylite.ui.navigation.GalleryNavHost
import com.gallerylite.ui.screens.albums.AlbumsViewModel
import com.gallerylite.ui.screens.favorites.FavoritesViewModel
import com.gallerylite.ui.screens.onthisday.OnThisDayViewModel
import com.gallerylite.ui.screens.photos.PhotosViewModel
import com.gallerylite.ui.theme.DarkBackground
import com.gallerylite.ui.theme.GalleryLiteTheme
import com.gallerylite.ui.theme.LavenderPrimary
import com.gallerylite.ui.theme.TextPrimary
import com.gallerylite.ui.theme.TextSecondary
import com.gallerylite.util.PermissionUtils

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lightweight DI initialization
        val database = GalleryDatabase.getDatabase(applicationContext)
        val mediaRepository = MediaRepository(applicationContext, database.favoritesDao())
        val albumRepository = AlbumRepository(mediaRepository)
        val onThisDayRepository = OnThisDayRepository(mediaRepository)
        val favoritesRepository = FavoritesRepository(database.favoritesDao(), mediaRepository)

        val photosViewModel = PhotosViewModel(mediaRepository)
        val albumsViewModel = AlbumsViewModel(albumRepository)
        val favoritesViewModel = FavoritesViewModel(favoritesRepository)
        val onThisDayViewModel = OnThisDayViewModel(onThisDayRepository)

        setContent {
            GalleryLiteTheme {
                var hasPermission by remember { mutableStateOf(PermissionUtils.hasPermissions(this)) }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { results ->
                    val allGranted = results.values.all { it }
                    hasPermission = allGranted
                    if (allGranted) {
                        photosViewModel.loadMedia()
                        albumsViewModel.loadAlbums()
                        onThisDayViewModel.loadMemories()
                    }
                }

                LaunchedEffect(Unit) {
                    if (!hasPermission) {
                        permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                    }
                }

                if (hasPermission) {
                    val navController = rememberNavController()
                    GalleryNavHost(
                        navController = navController,
                        photosViewModel = photosViewModel,
                        albumsViewModel = albumsViewModel,
                        favoritesViewModel = favoritesViewModel,
                        onThisDayViewModel = onThisDayViewModel,
                        favoritesRepository = favoritesRepository,
                        mediaRepository = mediaRepository
                    )
                } else {
                    PermissionRequiredScreen(
                        onRequestPermission = {
                            permissionLauncher.launch(PermissionUtils.getRequiredPermissions())
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRequiredScreen(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LavenderPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PhotoLibrary,
                    contentDescription = null,
                    tint = LavenderPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Access Your Photos",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gallery Lite requires permission to display photos and videos stored locally on your device. We never connect to the internet or collect telemetry.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Grant Permission",
                    color = DarkBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
