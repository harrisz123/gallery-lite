package com.gallerylite.ui.screens.viewer

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gallerylite.data.model.MediaItem
import com.gallerylite.data.repository.FavoritesRepository
import com.gallerylite.ui.theme.DarkSurface
import com.gallerylite.ui.theme.DarkSurfaceGlass
import com.gallerylite.ui.theme.GlassBorder
import com.gallerylite.ui.theme.TextPrimary
import com.gallerylite.ui.theme.TextSecondary
import com.gallerylite.ui.theme.WarmRose
import com.gallerylite.util.DateUtils
import com.gallerylite.util.FileUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    mediaItem: MediaItem,
    favoritesRepository: FavoritesRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var areControlsVisible by remember { mutableStateOf(true) }
    var showInfoSheet by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(mediaItem.isFavorite) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Zoomable media image
        ZoomableImage(
            mediaItem = mediaItem,
            onTap = { areControlsVisible = !areControlsVisible }
        )

        // Top App Bar
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurfaceGlass)
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = mediaItem.displayName,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
            }
        }

        // Bottom Action Bar
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 32.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(DarkSurfaceGlass)
                    .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Share
                IconButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = mediaItem.mimeType
                        putExtra(Intent.EXTRA_STREAM, mediaItem.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Media"))
                }) {
                    Icon(Icons.Rounded.Share, contentDescription = "Share", tint = Color.White)
                }

                // Favorite Toggle
                IconButton(onClick = {
                    isFavorite = !isFavorite
                    scope.launch {
                        favoritesRepository.toggleFavorite(mediaItem.copy(isFavorite = !isFavorite))
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) WarmRose else Color.White
                    )
                }

                // Info / EXIF details
                IconButton(onClick = { showInfoSheet = true }) {
                    Icon(Icons.Rounded.Info, contentDescription = "Info", tint = Color.White)
                }
            }
        }

        // Info Details Modal Sheet
        if (showInfoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showInfoSheet = false },
                containerColor = DarkSurface,
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Media Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    DetailRow(label = "Date Taken", value = DateUtils.formatFullDate(mediaItem.dateTakenMs))
                    DetailRow(label = "File Name", value = mediaItem.displayName)
                    DetailRow(label = "File Size", value = FileUtils.formatFileSize(mediaItem.sizeBytes))
                    if (mediaItem.width > 0 && mediaItem.height > 0) {
                        DetailRow(label = "Dimensions", value = "${mediaItem.width} × ${mediaItem.height}")
                    }
                    DetailRow(label = "MIME Type", value = mediaItem.mimeType)
                    if (mediaItem.path.isNotEmpty()) {
                        DetailRow(label = "Path", value = mediaItem.path)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
