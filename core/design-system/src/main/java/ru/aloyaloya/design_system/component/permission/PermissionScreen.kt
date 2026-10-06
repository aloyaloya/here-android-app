package ru.aloyaloya.design_system.component.permission

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.component.button.HereTextButton
import ru.aloyaloya.design_system.component.topbar.TopAppBarAction
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
 * @param canNavigateBack Показывать ли кнопку «Назад».
 * @param onNavigateBack Колбэк кнопки «Назад».
 * @param topAction Действие в правом верхнем углу, например [HereTextButton].
 * @param content Контент под кнопками, обычно [PermissionPage]; получает отступы верха и кнопок.
 */
@Composable
fun PermissionScreen(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondaryClick: () -> Unit = {},
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    topAction: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = HereTheme.colors.background,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(HereSize.TopAppBar.height)
                    .padding(horizontal = HereSize.TopAppBar.contentPadding)
            ) {
                if (canNavigateBack) {
                    TopAppBarAction(
                        icon = R.drawable.ic_arrow_back,
                        contentDescription = stringResource(R.string.permission_back_content_description),
                        onClick = onNavigateBack
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                topAction()
            }
        },
        bottomBar = {
            Column(
                verticalArrangement = Arrangement.spacedBy(HereSpacing.s),
                modifier = Modifier
                    .padding(horizontal = HereSpacing.screenHorizontal)
                    .navigationBarsPadding()
                    .padding(bottom = HereSpacing.l)
            ) {
                Crossfade(targetState = primaryText) { text ->
                    HerePrimaryButton(
                        text = text,
                        onClick = onPrimaryClick
                    )
                }

                AnimatedContent(
                    targetState = secondaryText,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { text ->
                    if (text != null) {
                        HereTextButton(
                            text = text,
                            onClick = onSecondaryClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        content = content
    )
}

/**
 * Страница экрана разрешения: иллюстрация во весь экран и нижняя панель с текстом.
 *
 * При крупном шрифте панель прокручивается, иллюстрация не сжимается и скрыта от TalkBack.
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

    Layout(
        content = {
            Box(modifier = Modifier.clearAndSetSemantics {}, content = illustration)

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
                    .animateContentSize()
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

                AnimatedContent(
                    targetState = title to body,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { (title, body) ->
                    Column {
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
                    }
                }

                AnimatedVisibility(visible = settingsPath != null) {
                    Column {
                        Spacer(modifier = Modifier.height(HereSpacing.l))
                        settingsPath?.invoke()
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(illustrationBackground)
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val sheetMaxHeight = (height - contentPadding.calculateTopPadding().roundToPx()).coerceAtLeast(0)
        val sheet = measurables[1].measure(
            Constraints(minWidth = width, maxWidth = width, maxHeight = sheetMaxHeight)
        )
        val illustrationHeight = (height - sheet.height + sizes.illustrationOverlap.roundToPx())
            .coerceIn(0, height)
        val illustration = measurables[0].measure(Constraints.fixed(width, illustrationHeight))

        layout(width, height) {
            illustration.place(0, 0)
            sheet.place(0, height - sheet.height)
        }
    }
}
