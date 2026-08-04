package ru.aloyaloya.memory.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.emotion.EmotionChip
import ru.aloyaloya.design_system.component.emotion.EmotionPin
import ru.aloyaloya.design_system.component.field.HereDateTimeField
import ru.aloyaloya.design_system.component.field.HereTextField
import ru.aloyaloya.design_system.component.media.MediaAddTile
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.component.topbar.HereModalTopBar
import ru.aloyaloya.design_system.component.topbar.HereModalTopBarAction
import ru.aloyaloya.design_system.extension.cardShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.memory.R
import ru.aloyaloya.memory.model.NewMemoryUiState
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import ru.aloyaloya.design_system.R as DesignSystemR

/** Масштаб, с которого плашка адреса вырастает до полного размера. */
private const val ADDRESS_INITIAL_SCALE = 0.8f

/** Дата занимает больше места, чем время: «15 июля 2026» против «19:30». */
private const val DATE_WEIGHT = 1.35f
private const val TIME_WEIGHT = 1f

private val DateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
private val TimeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("ru"))

/**
 * Экран нового воспоминания.
 *
 * Открывается поверх карты после выбора эмоции, поэтому у него своя панель сверху
 * и нет нижней навигации. Сохранить можно и действием в панели, и кнопкой внизу:
 * так в макете.
 *
 * @param uiState Состояние формы.
 * @param onEmotionSelected Колбэк смены эмоции.
 * @param onTitleChanged Колбэк ввода заголовка.
 * @param onDescriptionChanged Колбэк ввода описания.
 * @param onAddMediaClick Колбэк добавления фото или видео.
 * @param onSaveClick Колбэк сохранения.
 * @param onCancelClick Колбэк отмены.
 * @param modifier [Modifier], применяемый к экрану.
 */
@Composable
fun NewMemoryScreen(
    uiState: NewMemoryUiState,
    onEmotionSelected: (Emotion) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAddMediaClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
            .imePadding()
    ) {
        HereModalTopBar(
            title = stringResource(R.string.new_memory_title),
            navigation = {
                HereModalTopBarAction(
                    text = stringResource(R.string.new_memory_cancel),
                    onClick = onCancelClick
                )
            },
            action = {
                HereModalTopBarAction(
                    text = stringResource(R.string.new_memory_done),
                    onClick = onSaveClick,
                    accent = true,
                    enabled = uiState.saveEnabled
                )
            }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(HereSpacing.xl),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HereSpacing.screenHorizontal)
                .padding(vertical = HereSpacing.s)
        ) {
            PlacePreview(
                emotion = uiState.emotion,
                address = uiState.address
            )

            EmotionSection(
                selectedEmotion = uiState.emotion,
                onEmotionSelected = onEmotionSelected
            )

            Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.m)) {
                DateTimeSection(happenedAt = uiState.happenedAt)

                HereTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    placeholder = stringResource(R.string.new_memory_title_placeholder),
                    textStyle = MaterialTheme.typography.titleSmall,
                    singleLine = true
                )

                HereTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChanged,
                    placeholder = stringResource(R.string.new_memory_description_placeholder),
                    minHeight = HereSize.TextField.multilineMinHeight
                )

                MediaAddTile(onClick = onAddMediaClick)
            }
        }

        HerePrimaryButton(
            text = stringResource(R.string.new_memory_save),
            onClick = onSaveClick,
            enabled = uiState.saveEnabled,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = HereSpacing.screenHorizontal)
                .padding(bottom = HereSpacing.xl)
        )
    }
}

/**
 * Превью выбранного места: пин эмоции и адрес.
 *
 * Карты в превью пока нет, под пином лежит приглушенная подложка.
 *
 * Адрес приходит от геокодера позже самого экрана, поэтому плашка не появляется
 * рывком, а вырастает из своего угла.
 */
@Composable
private fun PlacePreview(
    emotion: Emotion?,
    address: String?
) {
    val colors = HereTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HereSize.PlacePreview.height)
            .cardShadow(HereShape.card)
            .clip(HereShape.card)
            .background(colors.surfaceMuted)
    ) {
        if (emotion != null) {
            EmotionPin(
                emoji = emotion.emoji,
                color = emotion.color.solid,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        AnimatedVisibility(
            visible = address != null,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialScale = ADDRESS_INITIAL_SCALE,
                transformOrigin = TransformOrigin(0f, 1f)
            ) + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(HereSize.PlacePreview.addressMargin)
        ) {
            Text(
                text = address.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier = Modifier
                    .clip(HereShape.chip)
                    .background(colors.surface)
                    .padding(
                        vertical = HereSize.PlacePreview.addressVerticalPadding,
                        horizontal = HereSize.PlacePreview.addressHorizontalPadding
                    )
            )
        }
    }
}

/**
 * Дата и время события.
 *
 * Обе плашки открывают свой лист выбора, листов пока нет.
 */
@Composable
private fun DateTimeSection(happenedAt: LocalDateTime) {
    Row(horizontalArrangement = Arrangement.spacedBy(HereSize.DateTimeField.spacing)) {
        HereDateTimeField(
            label = stringResource(R.string.new_memory_date_label),
            value = DateFormat.format(happenedAt),
            icon = DesignSystemR.drawable.ic_calendar_outline,
            onClick = {},
            modifier = Modifier.weight(DATE_WEIGHT)
        )

        HereDateTimeField(
            label = stringResource(R.string.new_memory_time_label),
            value = TimeFormat.format(happenedAt),
            icon = DesignSystemR.drawable.ic_clock_outline,
            onClick = {},
            modifier = Modifier.weight(TIME_WEIGHT)
        )
    }
}

/**
 * Ряд эмоций с подписью секции.
 *
 * Все шесть эмоций в ширину экрана не помещаются, поэтому ряд скроллится вбок.
 */
@Composable
private fun EmotionSection(
    selectedEmotion: Emotion?,
    onEmotionSelected: (Emotion) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.m)) {
        HereSectionLabel(text = stringResource(R.string.new_memory_emotion_label))

        Row(
            horizontalArrangement = Arrangement.spacedBy(HereSize.EmotionChip.spacing),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Emotion.entries.forEach { emotion ->
                EmotionChip(
                    emoji = emotion.emoji,
                    color = emotion.color,
                    selected = emotion == selectedEmotion,
                    onClick = { onEmotionSelected(emotion) }
                )
            }
        }
    }
}
