package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.aloyaloya.design_system.component.emotion.EmotionPin
import ru.aloyaloya.design_system.component.location.UserLocationMarker
import ru.aloyaloya.design_system.component.picker.PlacePin
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.onboarding.R
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji

/**
 * Пин эмоции на иллюстрации карты.
 *
 * @property x Центр пина по горизонтали в координатах холста.
 * @property y Центр пина по вертикали в координатах холста.
 * @property size Диаметр пина.
 * @property emotion Эмоция пина.
 * @property alpha Прозрачность пина.
 */
@Immutable
data class IllustrationPin(
    val x: Dp,
    val y: Dp,
    val size: Dp,
    val emotion: Emotion,
    val alpha: Float = 1f
)

/** Пины карты на первой странице. */
val FeaturePins = listOf(
    IllustrationPin(62.dp, 60.dp, 48.dp, Emotion.HAPPY),
    IllustrationPin(226.dp, 46.dp, 44.dp, Emotion.TENDER),
    IllustrationPin(156.dp, 136.dp, 60.dp, Emotion.CALM),
    IllustrationPin(262.dp, 150.dp, 44.dp, Emotion.SAD),
    IllustrationPin(70.dp, 200.dp, 46.dp, Emotion.SURPRISED),
    IllustrationPin(196.dp, 222.dp, 40.dp, Emotion.ANGRY)
)

/** Пины карты на странице геолокации. */
val LocationPins = listOf(
    IllustrationPin(58.dp, 54.dp, 40.dp, Emotion.HAPPY, alpha = 0.9f),
    IllustrationPin(266.dp, 214.dp, 40.dp, Emotion.SAD, alpha = 0.9f)
)

/** Пины карты рядом с пользователем: к [LocationPins] добавлен пин поблизости. */
val NearbyPins = LocationPins + IllustrationPin(192.dp, 108.dp, 52.dp, Emotion.CALM)

/**
 * Цвета нарисованной карты.
 *
 * @property land Суша.
 * @property road Дороги.
 * @property water Вода.
 * @property green Парки.
 */
private data class MapColors(
    val land: Color,
    val road: Color,
    val water: Color,
    val green: Color
)

private val LightMapColors = MapColors(
    land = Color(0xFFF0EAE4),
    road = Color(0xFFFDFBF9),
    water = Color(0xFFE2EDEF),
    green = Color(0xFFE8EEE3)
)

private val DarkMapColors = MapColors(
    land = Color(0xFF221E19),
    road = Color(0xFF2B261F),
    water = Color(0xFF1F2A2D),
    green = Color(0xFF232C21)
)

/** Центр отметки «Ты здесь» в координатах холста. */
private val UserLocationCenter = Offset(152f, 152f)

/**
 * Кусок карты с пинами эмоций.
 *
 * Рисуется в холсте фиксированного размера по центру панели: на широком экране
 * по краям продолжается суша, на низком холст обрезается сверху и снизу.
 *
 * @param pins Пины эмоций.
 * @param modifier [Modifier], применяемый к иллюстрации.
 * @param overlay Слой поверх пинов в координатах холста.
 */
@Composable
fun MapIllustration(
    pins: List<IllustrationPin>,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    val mapColors = if (HereTheme.colors.isDark) DarkMapColors else LightMapColors
    val sizes = HereSize.OnboardingIllustration

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(mapColors.land)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            translate(
                left = (size.width - sizes.canvasWidth.toPx()) / 2,
                top = (size.height - sizes.canvasHeight.toPx()) / 2
            ) {
                drawMap(mapColors)
            }
        }

        Box(modifier = Modifier.requiredSize(sizes.canvasWidth, sizes.canvasHeight)) {
            pins.forEach { pin ->
                EmotionPin(
                    emoji = pin.emotion.emoji,
                    color = pin.emotion.color.solid,
                    size = pin.size,
                    border = sizes.pinBorder,
                    emojiSize = (pin.size.value * sizes.pinEmojiRatio).sp,
                    modifier = Modifier
                        .offset(x = pin.x - pin.size / 2, y = pin.y - pin.size / 2)
                        .alpha(pin.alpha)
                )
            }

            overlay()
        }
    }
}

/** Отметка «Ты здесь» для слоя [MapIllustration]. */
@Composable
fun BoxScope.UserLocationOverlay() {
    val haloRadius = HereSize.UserLocation.haloSize / 2

    UserLocationMarker(
        label = stringResource(R.string.onboarding_location_here),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(
                x = UserLocationCenter.x.dp - HereSize.OnboardingIllustration.canvasWidth / 2,
                y = UserLocationCenter.y.dp - haloRadius
            )
    )
}

/** Прицел выбора места с подсказкой для слоя [MapIllustration]. */
@Composable
fun BoxScope.PlacePickerOverlay() {
    val colors = HereTheme.colors

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSpacing.s),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = HereSpacing.l)
            .overlayShadow(HereShape.pill)
            .background(color = colors.surface, shape = HereShape.pill)
            .padding(vertical = HereSpacing.s, horizontal = HereSpacing.m)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_target),
            contentDescription = null,
            tint = colors.textPrimary,
            modifier = Modifier.size(HereSize.OnboardingIllustration.hintIconSize)
        )

        Text(
            text = stringResource(R.string.onboarding_pick_hint),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textPrimary
        )
    }

    PlacePin(
        moving = false,
        modifier = Modifier
            .align(Alignment.Center)
            .offset(y = -HereSize.PlacePicker.pinHeight / 2)
    )
}

/**
 * Рисует парк, воду и дороги в координатах холста.
 *
 * @param colors Цвета карты.
 */
private fun DrawScope.drawMap(colors: MapColors) {
    drawBlob(colors.green, left = -30f, top = 140f, width = 150f, height = 110f, radius = 44f)
    drawBlob(colors.water, left = 196f, top = 186f, width = 210f, height = 150f, radius = 90f)

    drawRoad(colors.road, left = -20f, top = 92f, width = 360f, height = 14f, degrees = -8f)
    drawRoad(colors.road, left = -20f, top = 214f, width = 360f, height = 9f, degrees = 5f)
    drawRoad(colors.road, left = 124f, top = -30f, width = 13f, height = 330f, degrees = 14f)
    drawRoad(colors.road, left = 252f, top = -30f, width = 8f, height = 330f, degrees = -6f)
    drawRoad(colors.road, left = 40f, top = -40f, width = 7f, height = 200f, degrees = -24f)
}

/**
 * Скругленное пятно: парк или вода. Размеры в dp.
 *
 * @param color Цвет пятна.
 */
private fun DrawScope.drawBlob(
    color: Color,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    radius: Float
) {
    drawRoundRect(
        color = color,
        topLeft = Offset(left.dp.toPx(), top.dp.toPx()),
        size = Size(width.dp.toPx(), height.dp.toPx()),
        cornerRadius = CornerRadius(radius.dp.toPx())
    )
}

/**
 * Дорога: прямоугольник, повернутый вокруг своего центра. Размеры в dp.
 *
 * @param color Цвет дороги.
 */
private fun DrawScope.drawRoad(
    color: Color,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    degrees: Float
) {
    val topLeft = Offset(left.dp.toPx(), top.dp.toPx())
    val size = Size(width.dp.toPx(), height.dp.toPx())

    rotate(degrees = degrees, pivot = topLeft + Offset(size.width / 2, size.height / 2)) {
        drawRect(color = color, topLeft = topLeft, size = size)
    }
}
