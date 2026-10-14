package ru.aloyaloya.memory.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/** Откуда открыт просмотр: туда же снимок и вернется. */
enum class MediaOrigin {
    COVER,
    GRID
}

/** Общий слой перелета снимков между экраном воспоминания и просмотром. */
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

private data class MediaKey(val index: Int)

/**
 * Связывает снимок с его парой в просмотре: при открытии и закрытии он перелетает между ними.
 *
 * @param index Индекс снимка в воспоминании.
 * @param visibilityScope Появление и исчезновение снимка.
 */
@Composable
fun Modifier.sharedMedia(
    index: Int,
    visibilityScope: AnimatedVisibilityScope
): Modifier {
    val transitionScope = LocalSharedTransitionScope.current ?: return this

    return with(transitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(MediaKey(index)),
            animatedVisibilityScope = visibilityScope,
            resizeMode = RemeasureToBounds
        )
    }
}

/**
 * Снимок на экране воспоминания, из которого открывается просмотр.
 *
 * @param index Индекс снимка в воспоминании.
 * @param shared Снимок перелетает в просмотр и обратно.
 * @param hidden Снимок сейчас в просмотре: на его месте пусто.
 * @param modifier [Modifier], задающий место снимка.
 * @param content Сам снимок.
 */
@Composable
fun MediaSource(
    index: Int,
    shared: Boolean,
    hidden: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = !hidden,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = if (shared) {
                Modifier
                    .sharedMedia(index = index, visibilityScope = this)
                    .fillMaxSize()
            } else {
                Modifier.fillMaxSize()
            }
        ) {
            content()
        }
    }
}
