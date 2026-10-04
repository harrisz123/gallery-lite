package com.gallerylite.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gallerylite.data.model.DateGroup
import com.gallerylite.data.model.MediaItem

@Composable
fun MasonryFeed(
    dateGroups: List<DateGroup>,
    onItemClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val allItems = dateGroups.flatMap { it.items }
    val heroItem = allItems.firstOrNull()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        verticalItemSpacing = 6.dp,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        // Spotlight Hero Photo at the top
        if (heroItem != null) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Box(modifier = Modifier.padding(bottom = 12.dp)) {
                    HeroPhotoCard(
                        mediaItem = heroItem,
                        onClick = { onItemClick(heroItem) }
                    )
                }
            }
        }

        // Masonry feed grouped by date
        dateGroups.forEachIndexed { groupIndex, group ->
            // Date Pill Header
            item(
                key = "header_${group.title}_$groupIndex",
                span = StaggeredGridItemSpan.FullLine
            ) {
                DatePillHeader(
                    title = group.title,
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                )
            }

            // Exclude the hero item from the first group's sub-list so it doesn't duplicate
            val displayItems = if (groupIndex == 0 && heroItem != null) {
                group.items.filter { it.id != heroItem.id }
            } else {
                group.items
            }

            items(
                items = displayItems,
                key = { item -> item.id }
            ) { item ->
                MediaThumbnail(
                    mediaItem = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}
