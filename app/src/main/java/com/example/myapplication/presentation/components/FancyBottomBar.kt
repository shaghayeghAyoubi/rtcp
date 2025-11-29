package com.example.myapplication.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.presentation.navigation.Screen

@Composable
fun FancyBottomBar(
    navController: NavController,
    items: List<Screen>,                         // your screen data class (route, iconRes, title)
    modifier: Modifier = Modifier,
    fabIcon: ImageVector = Icons.Default.Add,    // change to your FAB icon
    onFabClick: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // indicator tracking
    val itemCenters = remember { mutableStateListOf<Float>().apply { repeat(items.size) { add(0f) } } }
    val density = LocalDensity.current

    // indicator position (dp) and animated movement
    val indicatorWidth = 36.dp
    var indicatorXdp by remember { mutableStateOf(0.dp) }
    val animatedIndicatorX by animateDpAsState(targetValue = indicatorXdp, animationSpec = tween(320))

    // bar sizes (tweak these)
    val barHeight = 76.dp
    val fabSize = 64.dp
    val topPaddingForFab = fabSize / 2f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Row of nav items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, screen ->
                    val isSelected = currentRoute == screen.route

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .clickable {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .onGloballyPositioned { coords ->
                                // store center x (px)
                                val centerPx = coords.positionInParent().x + coords.size.width / 2f
                                itemCenters[index] = centerPx
                                // if this item is selected, update indicator target (convert to dp)
                                if (screen.route == currentRoute) {
                                    indicatorXdp = with(density) { (centerPx).toDp() - indicatorWidth / 2f }
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon (use your existing GradientIcon)
                        GradientIcon(
                            resId = screen.iconRes,
                            selected = isSelected,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Label (optional smaller)
                        Text(
                            text = screen.title(),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(if (isSelected) 1f else 0.75f)
                        )
                    }
                }
            }

            // Sliding pill indicator (uses animated X)
            Box(
                modifier = Modifier
                    .offset(x = animatedIndicatorX)
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp)
                    .size(width = indicatorWidth, height = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
            )

            // FloatingActionButton centered and overlapping the bar
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier
                    .size(fabSize)
                    .align(Alignment.TopCenter)
                    .offset(y = -topPaddingForFab),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                // You can draw a gradient inside your FAB by using a Box if you'd like,
                // but default icon usage is fine here:
                Icon(fabIcon, contentDescription = null)
            }
        }
    }
}

private fun Screen.title(): String {
    return TODO("Provide the return value")
}

/** Example minimal GradientIcon placeholder — swap in your actual implementation */
@Composable
fun GradientIcon(@DrawableRes resId: Int, selected: Boolean, modifier: Modifier = Modifier) {
    // Replace with your actual gradient/bitmap painting
    Icon(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = modifier,
        tint = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current.copy(alpha = 0.7f)
    )
}
