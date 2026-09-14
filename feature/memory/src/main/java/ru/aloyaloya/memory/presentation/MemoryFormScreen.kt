package ru.aloyaloya.memory.presentation

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.component.button.HerePrimaryButton
import ru.aloyaloya.design_system.component.emotion.EmotionChip
import ru.aloyaloya.design_system.component.emotion.EmotionPin
import ru.aloyaloya.design_system.component.field.HereDateTimeField
import ru.aloyaloya.design_system.component.field.HereTextField
import ru.aloyaloya.design_system.component.media.MediaAddTile
import ru.aloyaloya.design_system.component.media.MediaTile
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.component.topbar.HereModalTopBar
import ru.aloyaloya.design_system.component.topbar.HereModalTopBarAction
import ru.aloyaloya.design_system.extension.cardShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.MediaType
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.mapkit.model.MapLogoPlacement
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.mapkit.ui.YandexMap
import ru.aloyaloya.memory.R
import ru.aloyaloya.memory.model.MemoryFormSheet
import ru.aloyaloya.memory.model.MemoryFormUiState
import ru.aloyaloya.memory.presentation.component.DateSheet
import ru.aloyaloya.memory.presentation.component.TimeSheet
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import ru.aloyaloya.ui.theme.LocalAppDarkTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import ru.aloyaloya.design_system.R as DesignSystemR

/** Масштаб, с которого плашка адреса вырастает до полного размера. */
private const val ADDRESS_INITIAL_SCALE = 0.8f

/** Дата занимает больше места, чем время: «15 июля 2026» против «19:30». */
private const val DATE_WEIGHT = 1.35f
private const val TIME_WEIGHT = 1f

/** Сколько файлов можно прикрепить к одному воспоминанию. */
private const val MEDIA_LIMIT = 10

private const val VIDEO_MIME_PREFIX = "video/"

private val DateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"))
private val TimeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("ru"))

/**
 * Форма воспоминания: и нового, и уже записанного.
 *
 * Открывается поверх карты после выбора эмоции или с экрана воспоминания, поэтому
 * у нее своя панель сверху и нет нижней навигации. Сохранить можно и действием
 * в панели, и кнопкой внизу: так в макете.
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
            .imePadding()
    ) {
        HereModalTopBar(
            title = stringResource(
                if (uiState.editing) R.string.memory_form_title_edit
                else R.string.memory_form_title_new
            ),
            navigation = {
                HereModalTopBarAction(
                    text = stringResource(R.string.memory_form_cancel),
                    onClick = onCancelClick
                )
            },
            action = {
                HereModalTopBarAction(
                    text = stringResource(R.string.memory_form_done),
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
                point = uiState.point,
                emotion = uiState.emotion,
                address = uiState.address
            )

            EmotionSection(
                selectedEmotion = uiState.emotion,
                onEmotionSelected = onEmotionSelected
            )

            Column(verticalArrangement = Arrangement.spacedBy(HereSpacing.m)) {
                DateTimeSection(
                    happenedAt = uiState.happenedAt,
                    onDateClick = onDateFieldClick,
                    onTimeClick = onTimeFieldClick
                )

                HereTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    placeholder = stringResource(R.string.memory_form_title_placeholder),
                    textStyle = MaterialTheme.typography.titleSmall,
                    singleLine = true
                )

                HereTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChanged,
                    placeholder = stringResource(R.string.memory_form_description_placeholder),
                    minHeight = HereSize.TextField.multilineMinHeight
                )

                MediaSection(
                    media = uiState.media,
                    onMediaPicked = onMediaPicked,
                    onMediaRemove = onMediaRemove
                )
            }
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

/**
 * Ряд выбранных файлов и плитка добавления.
 */
@Composable
private fun MediaSection(
    media: List<MemoryMedia>,
    onMediaPicked: (List<MemoryMedia>) -> Unit,
    onMediaRemove: (Int) -> Unit
) {
    val resolver = LocalContext.current.contentResolver

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(MEDIA_LIMIT)
    ) { uris ->
        onMediaPicked(uris.map { uri ->
            MemoryMedia(
                uri = uri.toString(),
                type = resolver.mediaType(uri)
            )
        })
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(HereSize.MediaTile.spacing),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        media.forEachIndexed { index, item ->
            MediaTile(
                uri = item.uri,
                onRemoveClick = { onMediaRemove(index) },
                video = item.type == MediaType.VIDEO
            )
        }

        if (media.size < MEDIA_LIMIT) {
            MediaAddTile(
                onClick = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                }
            )
        }
    }
}

/** Что именно выбрали в пикере: адрес о типе файла не говорит, а MIME говорит. */
private fun ContentResolver.mediaType(uri: Uri): MediaType =
    if (getType(uri)?.startsWith(VIDEO_MIME_PREFIX) == true) MediaType.VIDEO else MediaType.PHOTO

/**
 * Превью выбранного места: пин эмоции и адрес.

 */
@Composable
private fun PlacePreview(
    point: MapPoint?,
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
        if (point != null) {
            YandexMap(
                modifier = Modifier.fillMaxSize(),
                movable = true,
                interactive = false,
                locationEnabled = false,
                isDarkTheme = LocalAppDarkTheme.current,
                startPosition = point,
                startZoom = 16f,
                logoPlacement = MapLogoPlacement.Card
            )
        }

        if (emotion != null && point != null) {
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

/** Дата и время события: обе плашки открывают свой лист выбора. */
@Composable
private fun DateTimeSection(
    happenedAt: LocalDateTime,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(HereSize.DateTimeField.spacing)) {
        HereDateTimeField(
            label = stringResource(R.string.memory_form_date_label),
            value = DateFormat.format(happenedAt),
            icon = DesignSystemR.drawable.ic_calendar_outline,
            onClick = onDateClick,
            modifier = Modifier.weight(DATE_WEIGHT)
        )

        HereDateTimeField(
            label = stringResource(R.string.memory_form_time_label),
            value = TimeFormat.format(happenedAt),
            icon = DesignSystemR.drawable.ic_clock_outline,
            onClick = onTimeClick,
            modifier = Modifier.weight(TIME_WEIGHT)
        )
    }
}

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
                    emoji = emotion.emoji,
                    color = emotion.color,
                    selected = emotion == selectedEmotion,
                    onClick = { onEmotionSelected(emotion) }
                )
            }
        }
    }
}
