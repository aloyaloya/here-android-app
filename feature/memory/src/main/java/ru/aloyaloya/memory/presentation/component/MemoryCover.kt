package ru.aloyaloya.memory.presentation.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.component.character.EmotionCharacter
import ru.aloyaloya.design_system.component.media.MediaPhoto
import ru.aloyaloya.design_system.component.media.MediaPlayBadge
import ru.aloyaloya.design_system.extension.Entrance
import ru.aloyaloya.design_system.extension.entrance
import ru.aloyaloya.design_system.theme.DarkBackground
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.mapkit.model.MapLogoPlacement
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.mapkit.ui.YandexMap
import ru.aloyaloya.memory.R
import ru.aloyaloya.ui.emotion.character
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.theme.LocalAppDarkTheme

private const val PARALLAX = 0.5f
private const val PHOTO_COVER_RATIO = 4f / 3f
private const val MAX_COVER_HEIGHT_FRACTION = 0.75f
private const val TOP_SHADE_ALPHA = 0.42f
private const val BOTTOM_SHADE_ALPHA = 0.30f
private const val INACTIVE_DOT_ALPHA = 0.55f
private const val HALO_ALPHA = 0.6f
private const val MAP_ZOOM = 15.5f

/**
 * Обложка воспоминания: снимки листаются, без снимков — карта с персонажем.
 *
 * @param memory Воспоминание.
 * @param pagerState Состояние листания снимков.
 * @param scrollOffset Насколько обложка ушла под верх экрана, в пикселях.
 * @param onMediaClick Колбэк открытия снимка на весь экран.
 * @param modifier [Modifier], применяемый к обложке.
 */
@Composable
fun MemoryCover(
    memory: Memory,
    pagerState: PagerState,
    scrollOffset: () -> Int,
    onMediaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val photos = memory.media.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(coverHeight(photos))
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { translationY = scrollOffset() * PARALLAX }
        ) {
            if (photos) {
                PhotoPager(
                    media = memory.media,
                    pagerState = pagerState,
                    onMediaClick = onMediaClick
                )
            } else {
                MapCover(memory = memory)
            }
        }

        if (photos) {
            PhotoShades(scrollOffset = scrollOffset)

            if (memory.media.size > 1) {
                CoverIndicator(
                    pageCount = memory.media.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = HereSize.Memory.indicatorBottomPadding)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(HereSize.Memory.mapFadeHeight)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, HereTheme.colors.background)
                        )
                    )
            )
        }
    }
}

/** Высота обложки: снимки — в пропорции вертикального кадра, но не выше 3/4 окна; карта — фиксированной высоты. */
@Composable
fun coverHeight(photos: Boolean): Dp {
    val window = LocalWindowInfo.current.containerSize

    return if (photos) {
        with(LocalDensity.current) {
            minOf(
                window.width.toDp() * PHOTO_COVER_RATIO,
                window.height.toDp() * MAX_COVER_HEIGHT_FRACTION
            )
        }
    } else {
        HereSize.Memory.mapCoverHeight
    }
}

@Composable
private fun PhotoPager(
    media: List<MemoryMedia>,
    pagerState: PagerState,
    onMediaClick: (Int) -> Unit
) {
    val openLabel = stringResource(R.string.memory_cover_open)

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val item = media[page]
        val video = item.type == MediaType.VIDEO

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClickLabel = openLabel) { onMediaClick(page) }
        ) {
            MediaPhoto(
                uri = item.uri,
                video = video,
                shape = RectangleShape,
                modifier = Modifier.fillMaxSize()
            )

            if (video) {
                MediaPlayBadge()
            }
        }
    }
}

/** Затенение снимков: сверху под шапкой оно стоит на месте, снизу уезжает вместе с обложкой. */
@Composable
private fun BoxScope.PhotoShades(scrollOffset: () -> Int) {
    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .graphicsLayer { translationY = scrollOffset().toFloat() }
            .fillMaxWidth()
            .height(HereSize.Memory.coverTopShadeHeight)
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackground.copy(alpha = TOP_SHADE_ALPHA), Color.Transparent)
                )
            )
    )

    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(HereSize.Memory.coverBottomShadeHeight)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, DarkBackground.copy(alpha = BOTTOM_SHADE_ALPHA))
                )
            )
    )
}

/**
 * Белый индикатор поверх снимков.
 *
 * @param pageCount Число снимков.
 * @param currentPage Индекс текущего снимка.
 * @param modifier [Modifier], применяемый к индикатору.
 */
@Composable
private fun CoverIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.memory_cover_page, currentPage + 1, pageCount)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HereSize.Memory.indicatorSpacing),
        modifier = modifier.clearAndSetSemantics { contentDescription = description }
    ) {
        repeat(pageCount) { page ->
            val active = page == currentPage
            val width by animateDpAsState(
                targetValue = if (active) {
                    HereSize.Memory.indicatorActiveWidth
                } else {
                    HereSize.Memory.indicatorDotSize
                },
                label = "cover-dot-width"
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(HereSize.Memory.indicatorDotSize)
                    .background(
                        color = if (active) Color.White else Color.White.copy(alpha = INACTIVE_DOT_ALPHA),
                        shape = HereShape.pill
                    )
            )
        }
    }
}

/** Обложка без снимков: место на карте и персонаж эмоции. */
@Composable
private fun MapCover(memory: Memory) {
    Box(modifier = Modifier.fillMaxSize()) {
        YandexMap(
            modifier = Modifier.fillMaxSize(),
            movable = true,
            interactive = false,
            locationEnabled = false,
            isDarkTheme = LocalAppDarkTheme.current,
            startPosition = MapPoint(memory.latitude, memory.longitude),
            startZoom = MAP_ZOOM,
            logoPlacement = MapLogoPlacement.UnderTopBar
        )

        MemoryCharacter(
            emotion = memory.emotion,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = HereSize.Memory.haloTopPadding)
        )
    }
}

/** Персонаж воспоминания с ореолом: падает в него при первом показе экрана. */
@Composable
private fun MemoryCharacter(
    emotion: Emotion,
    modifier: Modifier = Modifier
) {
    var firstShow by rememberSaveable { mutableStateOf(true) }
    val dropping = remember { firstShow }

    SideEffect { firstShow = false }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.Memory.haloSize)
            .background(
                color = emotion.color.solid.copy(alpha = HALO_ALPHA),
                shape = CircleShape
            )
    ) {
        EmotionCharacter(
            emotion = emotion.character,
            size = HereSize.Memory.characterSize,
            modifier = if (dropping) {
                Modifier.entrance(
                    kind = Entrance.DROP,
                    delayMillis = HereMotion.Duration.medium
                )
            } else {
                Modifier
            }
        )
    }
}
