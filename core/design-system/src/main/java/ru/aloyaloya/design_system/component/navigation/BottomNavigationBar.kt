package ru.aloyaloya.design_system.component.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A custom bottom navigation bar component for the Gametracker application.
 *
 * This composable provides a styled bottom navigation bar using Material Design 3's [NavigationBar]
 * with custom appearance settings tailored for the Gametracker app. It serves as a wrapper
 * around the standard NavigationBar with pre-configured styling.
 *
 * The navigation bar uses the surface color from the current theme and removes tonal elevation
 * to create a flat, modern design that integrates seamlessly with the app's visual language.
 *
 * @param modifier The [Modifier] to be applied to the underlying NavigationBar component.
 * @param content The content to be displayed inside the navigation bar.
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        content = content
    )
}