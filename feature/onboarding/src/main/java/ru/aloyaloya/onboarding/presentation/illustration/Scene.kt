package ru.aloyaloya.onboarding.presentation.illustration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.onboarding.R

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
