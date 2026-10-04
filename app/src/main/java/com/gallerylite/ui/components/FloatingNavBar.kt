package com.gallerylite.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gallerylite.ui.navigation.BottomNavItem
import com.gallerylite.ui.navigation.bottomNavItems
import com.gallerylite.ui.theme.DarkSurfaceGlass
import com.gallerylite.ui.theme.GlassBorder
import com.gallerylite.ui.theme.LavenderPrimary
import com.gallerylite.ui.theme.TextSecondary
import com.gallerylite.ui.theme.TextTertiary

@Composable
fun FloatingNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = LavenderPrimary.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(DarkSurfaceGlass)
                .border(
                    width = 1.dp,
                    color = GlassBorder,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) LavenderPrimary else TextSecondary,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "NavColor"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) {
                                onNavigate(item.route)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = item.title,
                        color = contentColor,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Glowing dot under the selected tab
                    Box(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) LavenderPrimary else DarkSurfaceGlass)
                    )
                }
            }
        }
    }
}
