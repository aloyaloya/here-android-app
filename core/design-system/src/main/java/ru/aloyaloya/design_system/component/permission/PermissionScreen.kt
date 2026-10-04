package ru.aloyaloya.design_system.component.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.button.HereTextButton
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Каркас экрана запроса разрешения: верхний ряд, контент и кнопки, прижатые к низу.
 *
 * @param topBar Верхний ряд: индикатор страниц или стрелка «назад».
 * @param primaryText Подпись основной кнопки.
 * @param onPrimaryClick Колбэк основной кнопки.
 * @param modifier [Modifier], применяемый к экрану.
 * @param secondaryText Подпись вторичной кнопки или `null`, если ее нет.
 * @param onSecondaryClick Колбэк вторичной кнопки.
 * @param content Контент между верхним рядом и кнопками, обычно [PermissionContent].
 */
@Composable
fun PermissionScreen(
    topBar: @Composable () -> Unit,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondaryClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
            .statusBarsPadding()
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(HereSize.TopAppBar.height)
        ) {
            topBar()
        }

        Box(modifier = Modifier.weight(1f)) {
            content()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(HereSpacing.s),
            modifier = Modifier
                .padding(horizontal = HereSpacing.screenHorizontal)
                .navigationBarsPadding()
                .padding(bottom = HereSpacing.l)
        ) {
            HerePrimaryButton(
                text = primaryText,
                onClick = onPrimaryClick
            )

            secondaryText?.let { text ->
                HereTextButton(
                    text = text,
                    onClick = onSecondaryClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Контент экрана запроса разрешения: иллюстрация, заголовок, пояснение и путь в настройках.
 *
 * При крупном системном шрифте иллюстрация сжимается, а текст прокручивается.
 *
 * @param illustration Иллюстрация в скругленной панели.
 * @param title Заголовок.
 * @param body Пояснение.
 * @param modifier [Modifier], применяемый к контенту.
 * @param settingsPath Плашка с путем в настройках или `null`.
 */
@Composable
fun PermissionContent(
    illustration: @Composable BoxScope.() -> Unit,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    settingsPath: (@Composable () -> Unit)? = null
) {
    val colors = HereTheme.colors
    val sizes = HereSize.Permission
    val largeFont = LocalDensity.current.fontScale > sizes.largeFontScale

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = HereSpacing.screenHorizontal)
    ) {
        Spacer(modifier = Modifier.height(HereSpacing.s))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (largeFont) sizes.compactIllustrationHeight else sizes.illustrationHeight)
                .clip(HereShape.dialog)
                .then(
                    if (colors.isDark) {
                        Modifier.border(sizes.illustrationBorder, colors.outline, HereShape.dialog)
                    } else {
                        Modifier
                    }
                ),
            content = illustration
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = sizes.textTopSpacing, bottom = HereSpacing.l)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(HereSpacing.m))

            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textBody
            )

            settingsPath?.let { path ->
                Spacer(modifier = Modifier.height(HereSpacing.l))
                path()
            }
        }
    }
}
