package ru.aloyaloya.memory.presentation.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import ru.aloyaloya.design_system.component.media.MediaPhoto

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 4f
private const val TAP_SCALE = 2.5f

/**
 * Снимок, который можно приблизить щипком или двойным касанием.
 *
 * @param uri Путь к файлу во внутреннем хранилище.
 * @param active Страница открыта.
 * @param onZoomChange Колбэк приближения: пока снимок увеличен, листать нельзя.
 * @param modifier [Modifier], применяемый к снимку.
 */
@Composable
fun ZoomablePhoto(
    uri: String,
    active: Boolean,
    onZoomChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(MIN_SCALE) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    fun limit(value: Offset, scale: Float): Offset {
        val maxX = size.width * (scale - MIN_SCALE) / 2
        val maxY = size.height * (scale - MIN_SCALE) / 2

        return Offset(value.x.coerceIn(-maxX, maxX), value.y.coerceIn(-maxY, maxY))
    }

    val transformState = rememberTransformableState { zoom, pan, _ ->
        scale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
        offset = limit(offset + pan, scale)
    }

    LaunchedEffect(active) {
        if (!active) {
            scale = MIN_SCALE
            offset = Offset.Zero
        }
    }

    LaunchedEffect(scale) { onZoomChange(scale > MIN_SCALE) }

    MediaPhoto(
        uri = uri,
        shape = RectangleShape,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > MIN_SCALE) MIN_SCALE else TAP_SCALE
                        offset = Offset.Zero
                    }
                )
            }
            .transformable(state = transformState, canPan = { scale > MIN_SCALE })
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
    )
}
