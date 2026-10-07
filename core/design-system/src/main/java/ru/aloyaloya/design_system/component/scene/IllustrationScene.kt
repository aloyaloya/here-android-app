package ru.aloyaloya.design_system.component.scene

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.extension.Entrance
import ru.aloyaloya.design_system.extension.entrance
import ru.aloyaloya.design_system.theme.HereSize
import kotlin.math.roundToInt

/** Взгляд персонажа влево. */
const val LOOK_LEFT = -1f

/** Взгляд персонажа вправо. */
const val LOOK_RIGHT = 1f

/** Шаг между падениями персонажей. */
const val DROP_STEP_MILLIS = 120

/**
 * Персонаж на сцене иллюстрации.
 *
 * @property emotion Эмоция персонажа.
 * @property x Центр по X в долях ширины сцены.
 * @property y Центр по Y в долях высоты сцены.
 * @property size Ширина персонажа.
 * @property look Взгляд: −1 влево, 0 прямо, 1 вправо.
 */
@Immutable
data class CharacterSpot(
    val emotion: CharacterEmotion,
    val x: Float,
    val y: Float,
    val size: Dp,
    val look: Float = 0f
)

/**
 * Координаты сцены: доли ее ширины и высоты переводятся в dp.
 *
 * @param width Ширина сцены.
 * @param height Высота сцены.
 */
class SceneScope(
    boxScope: BoxScope,
    val width: Dp,
    val height: Dp
) : BoxScope by boxScope {

    /** Центр персонажа по X. */
    val CharacterSpot.centerX: Dp get() = width * x

    /** Центр персонажа по Y. */
    val CharacterSpot.centerY: Dp get() = height * y

    /** Правый край персонажа. */
    val CharacterSpot.right: Dp get() = centerX + size / 2

    /** Нижний край персонажа. */
    val CharacterSpot.bottom: Dp get() = centerY + size * HereSize.EmotionCharacter.heightRatio / 2

    /** Ставит элемент так, что его точка [pivot] попадает в ([x], [y]) сцены. */
    fun Modifier.pinTo(x: Dp, y: Dp, pivot: TransformOrigin = TransformOrigin.Center): Modifier =
        layout { measurable, constraints ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
            layout(placeable.width, placeable.height) {
                placeable.place(
                    x = x.roundToPx() - (placeable.width * pivot.pivotFractionX).roundToInt(),
                    y = y.roundToPx() - (placeable.height * pivot.pivotFractionY).roundToInt()
                )
            }
        }
}

/**
 * Сцена иллюстрации во все доступное место.
 *
 * @param backdrop Фон во всю сцену.
 * @param content Содержимое в координатах сцены.
 */
@Composable
fun IllustrationScene(
    modifier: Modifier = Modifier,
    backdrop: @Composable BoxScope.() -> Unit = {},
    content: @Composable SceneScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        backdrop()
        SceneScope(boxScope = this, width = maxWidth, height = maxHeight).content()
    }
}

/**
 * Персонаж в своей точке сцены: падает при показе экрана.
 *
 * @param spot Эмоция, место и взгляд персонажа.
 * @param delayMillis Задержка падения.
 */
@Composable
fun SceneScope.CharacterAt(
    spot: CharacterSpot,
    delayMillis: Int
) {
    EmotionCharacter(
        emotion = spot.emotion,
        size = spot.size,
        look = spot.look,
        modifier = Modifier
            .pinTo(x = spot.centerX, y = spot.centerY)
            .entrance(kind = Entrance.DROP, delayMillis = delayMillis)
    )
}
