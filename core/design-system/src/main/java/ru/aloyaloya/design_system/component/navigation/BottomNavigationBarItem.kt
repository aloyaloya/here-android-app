package ru.aloyaloya.design_system.component.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

/**
 * A custom bottom navigation bar item component designed for the Gametracker application.
 *
 * This extension function on [RowScope] provides a styled navigation bar item with customized
 * colors and appearance that aligns with the Gametracker design system. It wraps the standard
 * [NavigationBarItem] with pre-configured styling for icons and colors.
 *
 * @param painter The [Painter] to use for the icon display. Typically loaded from resource assets.
 * @param label The text label for the navigation item.
 * @param selected Boolean indicating whether this item is currently selected.
 * @param onClick Callback function to be invoked when the item is clicked.
 * @param modifier The [Modifier] to be applied to the underlying NavigationBarItem.
 */
@Composable
fun RowScope.BottomNavigationBarItem(
    painter: Painter,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBarItem(
        onClick = onClick,
        selected = selected,
        icon = {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painter,
                contentDescription = label
            )
        },
        modifier = modifier,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.surfaceVariant,
            unselectedIconColor = MaterialTheme.colorScheme.surfaceTint,
            indicatorColor = Color.Transparent
        )
    )
}