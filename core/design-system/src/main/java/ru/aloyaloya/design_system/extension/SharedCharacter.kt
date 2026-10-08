package ru.aloyaloya.design_system.extension

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.theme.HereMotion

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Персонаж воспоминания, который перелетает между экранами.
 *
 * @param memoryId Воспоминание персонажа или `null`, если персонаж не перелетает.
 */
@Composable
fun Modifier.sharedCharacter(memoryId: Long?): Modifier {
    val transitionScope = LocalSharedTransitionScope.current
    val visibilityScope = LocalNavAnimatedVisibilityScope.current
    if (memoryId == null || transitionScope == null || visibilityScope == null) return this

    return with(transitionScope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key = SharedCharacterKey(memoryId)),
            animatedVisibilityScope = visibilityScope,
            boundsTransform = BoundsTransform { _, _ -> HereMotion.spatial() }
        )
    }
}

private data class SharedCharacterKey(val memoryId: Long)
