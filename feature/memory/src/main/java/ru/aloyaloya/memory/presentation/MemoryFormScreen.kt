package ru.aloyaloya.memory.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.emotion.EmotionChip
import ru.aloyaloya.design_system.component.field.HereTextField
import ru.aloyaloya.design_system.component.section.HereCardRow
import ru.aloyaloya.design_system.component.section.HereSection
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.component.topbar.HereContextTopAppBar
import ru.aloyaloya.design_system.format.FullDateFormat
import ru.aloyaloya.design_system.format.TimeFormat
import ru.aloyaloya.design_system.format.currentLocale
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.domain.model.PlaceAddress
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.memory.R
import ru.aloyaloya.memory.model.MemoryFormSheet
import ru.aloyaloya.memory.model.MemoryFormUiState
import ru.aloyaloya.memory.presentation.component.DateSheet
import ru.aloyaloya.memory.presentation.component.MEDIA_LIMIT
import ru.aloyaloya.memory.presentation.component.MediaAddButtons
import ru.aloyaloya.memory.presentation.component.MediaCollage
import ru.aloyaloya.memory.presentation.component.MemoryMiniMap
import ru.aloyaloya.memory.presentation.component.TimeSheet
import ru.aloyaloya.memory.presentation.component.dayCaption
import ru.aloyaloya.ui.emotion.character
import ru.aloyaloya.ui.emotion.color
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import ru.aloyaloya.design_system.R as DesignSystemR

/** Половина зазора между строками: вторая половина у соседней строки. */
private val RowVerticalPadding = HereSize.Memory.rowSpacing / 2
private val SectionVerticalPadding = HereSpacing.l - RowVerticalPadding

/**
 * Форма воспоминания: и нового, и уже записанного.
 *
 * Открывается поверх карты после выбора эмоции или с экрана воспоминания, поэтому
 * у нее своя панель сверху и нет нижней навигации. Панель только закрывает форму,
 * сохранение живет в кнопке внизу — одно действие, один орган управления.
 * Секции и строки карточек те же, что на экране воспоминания.
 *
 * Чем форма занята, она узнает по [MemoryFormUiState.editing]: меняются только
 * заголовок панели и подпись кнопки, поля везде одни и те же.
 *
 * @param uiState Состояние формы.
 * @param onEmotionSelected Колбэк смены эмоции.
 * @param onTitleChanged Колбэк ввода заголовка.
 * @param onDescriptionChanged Колбэк ввода описания.
 * @param onDateFieldClick Колбэк открытия листа даты.
 * @param onTimeFieldClick Колбэк открытия листа времени.
 * @param onSheetDismiss Колбэк закрытия листа без выбора.
 * @param onDateSelected Колбэк выбранной даты.
 * @param onTimeSelected Колбэк выбранного времени.
 * @param onMediaPicked Колбэк выбранных в пикере файлов.
 * @param onMediaRemove Колбэк удаления файла по его месту в подборке.
 * @param onSaveClick Колбэк сохранения.
 * @param onCancelClick Колбэк отмены.
 * @param modifier [Modifier], применяемый к экрану.
 */
@Composable
fun MemoryFormScreen(
    uiState: MemoryFormUiState,
    onEmotionSelected: (Emotion) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDateFieldClick: () -> Unit,
    onTimeFieldClick: () -> Unit,
    onSheetDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    onMediaPicked: (List<MemoryMedia>) -> Unit,
    onMediaRemove: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        HereContextTopAppBar(
            title = stringResource(
                if (uiState.editing) R.string.memory_form_title_edit
                else R.string.memory_form_title_new
            ),
            navigationContentDescription = stringResource(DesignSystemR.string.modal_close_content_description),
            onNavigateBack = onCancelClick,
            navigationIcon = DesignSystemR.drawable.ic_close,
            scrolled = scrollState.canScrollBackward
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(HereSpacing.xl),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = HereSpacing.screenHorizontal)
                .padding(top = HereSpacing.s, bottom = HereSpacing.xl)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.m)) {
                HereTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    label = stringResource(R.string.memory_form_title_label),
                    textStyle = MaterialTheme.typography.titleSmall,
                    singleLine = true
                )

                HereTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChanged,
                    label = stringResource(
                        uiState.emotion?.descriptionLabelResId
                            ?: R.string.memory_form_description_label
                    ),
                    minHeight = HereSize.TextField.multilineMinHeight
                )
            }

            EmotionSection(
                selectedEmotion = uiState.emotion,
                onEmotionSelected = onEmotionSelected
            )

            WhenSection(
                happenedAt = uiState.happenedAt,
                onDateClick = onDateFieldClick,
                onTimeClick = onTimeFieldClick
            )

            WhereSection(
                point = uiState.point,
                emotion = uiState.emotion,
                address = uiState.address
            )

            MediaSection(
                media = uiState.media,
                onMediaPicked = onMediaPicked,
                onMediaRemove = onMediaRemove
            )
        }

        HerePrimaryButton(
            text = stringResource(
                if (uiState.editing) R.string.memory_form_save_edit
                else R.string.memory_form_save_new
            ),
            onClick = onSaveClick,
            enabled = uiState.saveEnabled,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = HereSpacing.screenHorizontal)
                .padding(bottom = HereSpacing.xl)
        )
    }

    when (uiState.activeSheet) {
        MemoryFormSheet.DATE -> DateSheet(
            initialDate = uiState.happenedAt.toLocalDate(),
            emotionByDate = uiState.emotionByDate,
            onDismissRequest = onSheetDismiss,
            onDateSelected = onDateSelected
        )

        MemoryFormSheet.TIME -> TimeSheet(
            initialTime = uiState.happenedAt.toLocalTime(),
            onDismissRequest = onSheetDismiss,
            onTimeSelected = onTimeSelected
        )

        null -> Unit
    }
}

/** Секция «Фото и видео»: коллаж выбранных файлов, как на экране воспоминания, под ним кнопки галереи и камеры. */
@Composable
private fun MediaSection(
    media: List<MemoryMedia>,
    onMediaPicked: (List<MemoryMedia>) -> Unit,
    onMediaRemove: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        HereSectionLabel(
            text = if (media.isEmpty()) {
                stringResource(R.string.memory_media_title)
            } else {
                stringResource(R.string.memory_media_count_title, media.size)
            },
            modifier = Modifier.padding(horizontal = HereSpacing.xs)
        )

        if (media.isNotEmpty()) {
            MediaCollage(
                media = media,
                onMediaRemove = onMediaRemove
            )
        }

        if (media.size < MEDIA_LIMIT) {
            MediaAddButtons(
                onMediaPicked = onMediaPicked,
                maxItems = MEDIA_LIMIT - media.size
            )
        }
    }
}

/**
 * Секция «Когда»: дата и время открывают свой лист выбора.
 *
 * @param happenedAt Когда событие произошло.
 * @param onDateClick Колбэк нажатия на дату.
 * @param onTimeClick Колбэк нажатия на время.
 */
@Composable
private fun WhenSection(
    happenedAt: LocalDateTime,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    HereSection(title = stringResource(R.string.memory_form_when_title)) {
        Column(modifier = Modifier.padding(vertical = SectionVerticalPadding)) {
            HereCardRow(
                icon = DesignSystemR.drawable.ic_calendar_outline,
                title = FullDateFormat.withLocale(currentLocale()).format(happenedAt),
                description = dayCaption(happenedAt.toLocalDate()),
                modifier = Modifier
                    .clickable(
                        onClickLabel = stringResource(R.string.memory_form_change_date),
                        onClick = onDateClick
                    )
                    .rowPadding()
            )

            HereCardRow(
                icon = DesignSystemR.drawable.ic_clock_outline,
                title = TimeFormat.format(happenedAt),
                description = null,
                modifier = Modifier
                    .clickable(
                        onClickLabel = stringResource(R.string.memory_form_change_time),
                        onClick = onTimeClick
                    )
                    .rowPadding()
            )
        }
    }
}

/**
 * Секция «Где»: адрес и мини-карта с персонажем.
 *
 * @param point Точка воспоминания или `null`, пока она не загружена.
 * @param emotion Выбранная эмоция.
 * @param address Адрес точки или `null`, пока он не найден.
 */
@Composable
private fun WhereSection(
    point: MapPoint?,
    emotion: Emotion?,
    address: PlaceAddress?
) {
    HereSection(title = stringResource(R.string.memory_form_where_title)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(HereSize.Memory.rowSpacing),
            modifier = Modifier.padding(HereSpacing.l)
        ) {
            HereCardRow(
                icon = DesignSystemR.drawable.ic_place,
                title = address?.line ?: stringResource(R.string.memory_place_unknown),
                description = address?.locality
            )

            if (point != null) {
                MemoryMiniMap(point = point, emotion = emotion)
            }
        }
    }
}

/** Отступы строки внутри карточки: нажатие подсвечивает ее во всю ширину. */
private fun Modifier.rowPadding(): Modifier =
    padding(horizontal = HereSpacing.l, vertical = RowVerticalPadding)

/** Ряд эмоций с подписью секции. */
@Composable
private fun EmotionSection(
    selectedEmotion: Emotion?,
    onEmotionSelected: (Emotion) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.m)) {
        HereSectionLabel(text = stringResource(R.string.memory_form_emotion_label))

        Row(
            horizontalArrangement = Arrangement.spacedBy(HereSize.EmotionChip.spacing),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Emotion.entries.forEach { emotion ->
                EmotionChip(
                    character = emotion.character,
                    color = emotion.color,
                    selected = emotion == selectedEmotion,
                    onClick = { onEmotionSelected(emotion) }
                )
            }
        }
    }
}

private val Emotion.descriptionLabelResId: Int
    @StringRes
    get() = when (this) {
        Emotion.HAPPY -> R.string.memory_form_description_label_happy
        Emotion.TENDER -> R.string.memory_form_description_label_tender
        Emotion.SURPRISED -> R.string.memory_form_description_label_surprised
        Emotion.CALM -> R.string.memory_form_description_label_calm
        Emotion.SAD -> R.string.memory_form_description_label_sad
        Emotion.ANGRY -> R.string.memory_form_description_label_angry
    }
