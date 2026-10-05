package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.component.character.CharacterEmotion
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R

/** Взгляд персонажа влево. */
const val LOOK_LEFT = -1f

/** Взгляд персонажа вправо. */
const val LOOK_RIGHT = 1f

/** Шаг между падениями персонажей. */
const val DROP_STEP_MILLIS = 120

/** Переход персонажа в новую точку. */
private const val MOVE_MILLIS = 300

/**
 * Персонаж на сцене иллюстрации.
 *
 * @property emotion Эмоция персонажа.
 * @property x Левый край от левого края сцены.
 * @property y Верхний край от верха экрана.
 * @property size Ширина персонажа в макете.
 * @property look Взгляд: −1 влево, 0 прямо, 1 вправо.
 */
@Immutable
data class CharacterSpot(
    val emotion: CharacterEmotion,
    val x: Dp,
    val y: Dp,
    val size: Dp,
    val look: Float = 0f
) {
    /** Центр персонажа по X. */
    val centerX: Dp get() = x + size / 2

    /** Центр персонажа по Y. */
    val centerY: Dp get() = y + size * HereSize.EmotionCharacter.heightRatio / 2

    /** Правый край персонажа. */
    val right: Dp get() = x + size

    /** Нижний край персонажа. */
    val bottom: Dp get() = y + size * HereSize.EmotionCharacter.heightRatio

    /** Тот же персонаж, увеличенный до размера на экране вокруг своего центра. */
    fun scaled(): CharacterSpot {
        val grown = size * HereSize.OnboardingIllustration.characterScale
        return copy(
            x = centerX - grown / 2,
            y = centerY - grown * HereSize.EmotionCharacter.heightRatio / 2,
            size = grown
        )
    }
}

/**
 * Координаты сцены, растянутой из кадра макета на доступное место.
 *
 * @param scaleX Растяжение по ширине.
 * @param scaleY Растяжение по высоте.
 */
class SceneScope(
    boxScope: BoxScope,
    private val scaleX: Float,
    private val scaleY: Float
) : BoxScope by boxScope {

    /** Сдвиг по X точки [anchor] кадра макета при растяжении. */
    fun shiftX(anchor: Dp): Dp = anchor * (scaleX - 1)

    /** Сдвиг по Y точки [anchor] кадра макета при растяжении. */
    fun shiftY(anchor: Dp): Dp = anchor * (scaleY - 1)

    /** Смещение элемента, привязанного к точке ([anchorX], [anchorY]) кадра макета. */
    fun Modifier.sceneOffset(x: Dp, y: Dp, anchorX: Dp = x, anchorY: Dp = y): Modifier =
        offset(x = x + shiftX(anchorX), y = y + shiftY(anchorY))
}

/**
 * Сцена иллюстрации: кадр макета, растянутый на доступное место без масштаба элементов.
 *
 * @param frameHeight Высота кадра макета от верха экрана до низа иллюстрации.
 * @param backdrop Фон во всю сцену.
 * @param content Содержимое в координатах кадра макета.
 */
@Composable
fun IllustrationScene(
    frameHeight: Dp,
    modifier: Modifier = Modifier,
    backdrop: @Composable BoxScope.() -> Unit = {},
    content: @Composable SceneScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        backdrop()

        val scope = SceneScope(
            boxScope = this,
            scaleX = maxWidth / HereSize.OnboardingIllustration.sceneWidth,
            scaleY = maxHeight / frameHeight
        )
        scope.content()
    }
}

/** Фон-карта онбординга во всю сцену. */
@Composable
fun MapBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.onboarding_map),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    )
}

/**
 * Персонаж в своей точке сцены: падает при показе страницы, плавно переходит в новую точку.
 *
 * @param spot Эмоция, место и взгляд персонажа.
 * @param active Текущая ли страница.
 * @param delayMillis Задержка падения.
 */
@Composable
fun SceneScope.CharacterAt(
    spot: CharacterSpot,
    active: Boolean,
    delayMillis: Int
) {
    val target = spot.scaled()
    val x by animateDpAsState(target.x, tween(MOVE_MILLIS), label = "x")
    val y by animateDpAsState(target.y, tween(MOVE_MILLIS), label = "y")
    val size by animateDpAsState(target.size, tween(MOVE_MILLIS), label = "size")
    val moving = spot.copy(x = x, y = y, size = size)

    EmotionCharacter(
        emotion = spot.emotion,
        size = size,
        look = spot.look,
        modifier = Modifier
            .sceneOffset(
                x = x,
                y = y,
                anchorX = moving.centerX,
                anchorY = moving.centerY
            )
            .entrance(active = active, kind = Entrance.DROP, delayMillis = delayMillis)
    )
}

/**
 * Группа поверх иллюстрации по центру места под статус-баром.
 *
 * @param content Содержимое группы.
 */
@Composable
fun IllustrationGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val margin = HereSize.OnboardingIllustration.cardMargin

    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.s, Alignment.CenterVertically),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = margin,
                end = margin,
                bottom = HereSize.PermissionSheet.illustrationOverlap
            ),
        content = content
    )
}

/** Карточка поверх иллюстрации: поверхность, тень, в темной теме обводка. */
@Composable
fun Modifier.illustrationCard(): Modifier {
    val colors = HereTheme.colors

    return this
        .overlayShadow(HereShape.tile)
        .background(color = colors.surface, shape = HereShape.tile)
        .then(
            if (colors.isDark) {
                Modifier.border(HereSize.PermissionSheet.darkBorder, colors.outline, HereShape.tile)
            } else {
                Modifier
            }
        )
}
