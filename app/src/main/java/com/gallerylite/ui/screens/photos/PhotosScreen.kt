package com.gallerylite.ui.screens.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gallerylite.data.model.MediaItem
import com.gallerylite.ui.components.MasonryFeed
import com.gallerylite.ui.components.TopGlassBar
import com.gallerylite.ui.theme.DarkBackground
import com.gallerylite.ui.theme.LavenderPrimary
import com.gallerylite.ui.theme.TextPrimary
import com.gallerylite.ui.theme.TextSecondary

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel,
    onMediaClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopGlassBar(
                title = "Gallery Lite",
                onSearchClick = {},
                onVaultClick = {}
            )

            when (val state = uiState) {
                is PhotosUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LavenderPrimary)
                    }
                }
                is PhotosUiState.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Your Gallery is Empty",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Take some photos or download images to see your dynamic masonry feed.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                is PhotosUiState.Success -> {
                    MasonryFeed(
                        dateGroups = state.dateGroups,
                        onItemClick = onMediaClick,
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 96.dp)
                    )
                }
                is PhotosUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.errorMsg,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
