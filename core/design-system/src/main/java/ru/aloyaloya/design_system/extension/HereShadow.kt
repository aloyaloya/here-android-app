package ru.aloyaloya.design_system.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Тень элемента, лежащего поверх карты или фотографии.
 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.overlayShadow(shape: Shape): Modifier =
    dropShadow(
        shape = shape,
        color = Color.Black.copy(alpha = 0.10f),
        blur = 12.dp,
        offsetY = 2.dp
    )

/**
 * Тень FAB-кнопки.
 *

 *
 * @param shape Форма элемента, к которому применяется тень.
 */
@Composable
fun Modifier.fabShadow(shape: Shape): Modifier =
    dropShadow(
        shape = shape,
        color = Color.Black.copy(alpha = 0.16f),
        blur = 12.dp,
        offsetY = 3.dp
    )

/**
 * Тень нижней панели над иллюстрацией.
 *
 * @param shape Форма панели.
 */
@Composable
fun Modifier.sheetShadow(shape: Shape): Modifier =
    dropShadow(
        shape = shape,
        color = Color.Black.copy(alpha = if (HereTheme.colors.isDark) 0.45f else 0.10f),
        blur = 40.dp,
        offsetY = (-12).dp
    )
