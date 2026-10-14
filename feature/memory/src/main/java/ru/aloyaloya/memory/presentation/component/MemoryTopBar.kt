package ru.aloyaloya.memory.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.theme.DarkBackground
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.R
import ru.aloyaloya.design_system.R as DesignSystemR

private const val PHOTO_PLATE_ALPHA = 0.32f
private const val MAP_PLATE_ALPHA = 0.88f

/**
 * Шапка воспоминания поверх обложки: пока обложка видна, кнопки стоят на кружках,
 * а когда она уехала под шапку — шапка заливается фоном и показывает заголовок.
 *
 * @param title Заголовок воспоминания.
 * @param collapsed Обложка уехала под шапку.
 * @param overPhotos Под шапкой снимки, а не карта.
 * @param onBackClick Колбэк возврата назад.
 * @param onEditClick Колбэк пункта «Изменить».
 * @param onDeleteClick Колбэк пункта «Удалить».
 * @param modifier [Modifier], применяемый к шапке.
 */
@Composable
fun MemoryTopBar(
    title: String,
    collapsed: Boolean,
    overPhotos: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    var menuOpen by rememberSaveable { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (collapsed) 1f else 0f,
        animationSpec = tween(HereMotion.Duration.short),
        label = "memory-top-bar"
    )

    val coverTint = if (overPhotos) Color.White else colors.textPrimary
    val coverPlate = if (overPhotos) {
        DarkBackground.copy(alpha = PHOTO_PLATE_ALPHA)
    } else {
        colors.background.copy(alpha = MAP_PLATE_ALPHA)
    }
    val tint = lerp(coverTint, colors.textPrimary, progress)
    val plate = coverPlate.copy(alpha = coverPlate.alpha * (1f - progress))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .drawBehind { drawRect(colors.background.copy(alpha = progress)) }
            .statusBarsPadding()
            .height(HereSize.TopAppBar.height)
            .padding(horizontal = HereSize.TopAppBar.contentPadding)
    ) {
        BarButton(
            icon = DesignSystemR.drawable.ic_arrow_back,
            iconSize = HereSize.TopAppBar.actionIconSize,
            contentDescription = stringResource(R.string.memory_back),
            tint = tint,
            plate = plate,
            onClick = onBackClick
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = HereSize.TopAppBar.contentPadding)
                .graphicsLayer { alpha = progress }
                .then(if (collapsed) Modifier else Modifier.clearAndSetSemantics {})
        )

        Box {
            BarButton(
                icon = DesignSystemR.drawable.ic_more_vert,
                iconSize = HereSize.Memory.moreIconSize,
                contentDescription = stringResource(R.string.memory_action_more),
                tint = tint,
                plate = plate,
                onClick = { menuOpen = true }
            )

            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
                containerColor = colors.surface
            ) {
                DropdownMenuItem(
                    text = { MenuText(stringResource(R.string.memory_action_edit)) },
                    onClick = {
                        menuOpen = false
                        onEditClick()
                    }
                )
                DropdownMenuItem(
                    text = { MenuText(stringResource(R.string.memory_action_delete)) },
                    onClick = {
                        menuOpen = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun BarButton(
    @DrawableRes icon: Int,
    iconSize: Dp,
    contentDescription: String,
    tint: Color,
    plate: Color,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(HereSize.TopAppBar.actionSize)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(HereSize.Memory.barPlateSize)
                .background(color = plate, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun MenuText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = HereTheme.colors.textPrimary
    )
}
