package ru.aloyaloya.design_system.component.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.button.HereTextButton
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.extension.sheetShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Каркас экрана запроса разрешения: контент во весь экран, действие сверху и кнопки внизу.
 *
 * @param primaryText Подпись основной кнопки.
 * @param onPrimaryClick Колбэк основной кнопки.
 * @param secondaryText Подпись вторичной кнопки или `null`, если ее нет.
 * @param onSecondaryClick Колбэк вторичной кнопки.
 * @param topAction Действие в правом верхнем углу, например [PermissionSkipButton].
 * @param content Контент под кнопками, обычно [PermissionPage]; получает отступы верха и кнопок.
 */
@Composable
fun PermissionScreen(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondaryClick: () -> Unit = {},
    topAction: @Composable BoxScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = HereTheme.colors.background,
        topBar = {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(HereSize.TopAppBar.height)
                    .padding(end = HereSpacing.m),
                content = topAction
            )
        },
        bottomBar = {
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
        },
        content = content
    )
}

/**
 * Страница экрана разрешения: иллюстрация во весь экран и нижняя панель с текстом.
 *
 * При крупном шрифте панель прокручивается, иллюстрация не сжимается.
 *
 * @param contentPadding Отступы от [PermissionScreen]: панель не заходит под верх и кнопки.
 * @param illustrationBackground Фон иллюстрации.
 * @param illustration Иллюстрация во весь экран.
 * @param title Заголовок.
 * @param body Пояснение.
 * @param pageIndicator Индикатор страниц над заголовком или `null`.
 * @param settingsPath Плашка с путем в настройках или `null`.
 */
@Composable
fun PermissionPage(
    contentPadding: PaddingValues,
    illustrationBackground: Color,
    illustration: @Composable BoxScope.() -> Unit,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    pageIndicator: (@Composable () -> Unit)? = null,
    settingsPath: (@Composable () -> Unit)? = null
) {
    val colors = HereTheme.colors
    val sizes = HereSize.PermissionSheet

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(illustrationBackground)
    ) {
        Box(modifier = Modifier.fillMaxSize(), content = illustration)

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .sheetShadow(HereShape.sheet)
                    .background(color = colors.background, shape = HereShape.sheet)
                    .then(
                        if (colors.isDark) {
                            Modifier.border(sizes.darkBorder, colors.outline, HereShape.sheet)
                        } else {
                            Modifier
                        }
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = sizes.topPadding,
                        start = HereSpacing.screenHorizontal,
                        end = HereSpacing.screenHorizontal,
                        bottom = contentPadding.calculateBottomPadding() + HereSpacing.l
                    )
            ) {
                pageIndicator?.let { indicator ->
                    indicator()
                    Spacer(modifier = Modifier.height(sizes.indicatorSpacing))
                }

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
}

/**
 * Плавающая пилюля «Пропустить» поверх иллюстрации.
 *
 * @param text Подпись.
 * @param onClick Колбэк нажатия.
 */
@Composable
fun PermissionSkipButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val sizes = HereSize.PermissionSkip

    Surface(
        onClick = onClick,
        shape = HereShape.pill,
        color = colors.surface.copy(alpha = sizes.surfaceAlpha),
        modifier = modifier.overlayShadow(HereShape.pill)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(sizes.height)
                .padding(horizontal = HereSpacing.l)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
        }
    }
}
