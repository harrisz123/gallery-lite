package com.gallerylite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gallerylite.data.model.MediaItem
import com.gallerylite.ui.theme.DarkSurfaceVariant
import com.gallerylite.ui.theme.WarmRose

@Composable
fun MediaThumbnail(
    mediaItem: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Constrain aspect ratio between 0.75 (tall portrait) and 1.4 (wide landscape) for balanced masonry
    val clampedRatio = mediaItem.aspectRatio.coerceIn(0.75f, 1.4f)

    Box(
        modifier = modifier
            .aspectRatio(clampedRatio)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(mediaItem.uri)
                .crossfade(true)
                .build(),
            contentDescription = mediaItem.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Video Duration Badge
        if (mediaItem.isVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Video",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    if (mediaItem.formattedDuration.isNotEmpty()) {
                        Text(
                            text = mediaItem.formattedDuration,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }

        // Favorite Indicator
        if (mediaItem.isFavorite) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "Favorite",
                    tint = WarmRose,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
