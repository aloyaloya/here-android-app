package ru.aloyaloya.design_system.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Общие отступы экранов.
 *
 * Размеры конкретных компонентов лежат в [HereSize].
 */
object HereSpacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val xl = 20.dp
    val screenHorizontal = 22.dp
}

/**
 * Размеры компонентов дизайн-системы.
 *
 * Часть значений нужна и снаружи: экран сам отступает от краёв для нижней панели
 * и FAB, поэтому поля с margin тоже здесь, а не спрятаны в компонентах.
 */
object HereSize {

    /** Верхняя панель. */
    object TopAppBar {
        val height = 64.dp
        val contentPadding = 4.dp
        val titlePadding = 16.dp
        val actionSize = 48.dp
        val actionIconSize = 24.dp
    }

    /** Нижняя панель навигации. */
    object NavBar {
        val height = 80.dp
        val itemIconSize = 24.dp
        val itemIconLabelSpacing = 4.dp
        val indicatorWidth = 64.dp
        val indicatorHeight = 32.dp
    }

    /** FAB-кнопка. */
    object Fab {
        val size = 56.dp
        val iconSize = 24.dp
        val endMargin = 18.dp
        val barSpacing = 24.dp
        val extendedPadding = 22.dp
        val stackSpacing = 14.dp
    }

    /** Нижний лист. */
    object Sheet {
        val horizontalPadding = 27.dp
        val bottomPadding = 36.dp
        val contentSpacing = 22.dp
        val handleWidth = 48.dp
        val handleHeight = 5.dp
        val handleVerticalPadding = 17.dp
        val actionSpacing = 10.dp
        val titleSpacing = 3.dp
        val titleValueSize = 28.sp
    }

    /** Календарь в листе выбора даты. */
    object Calendar {
        val gridSpacing = 6.dp
        val navButtonSize = 40.dp
        val navIconSize = 20.dp
        val navButtonSpacing = 8.dp
        val selectedBorder = 1.5.dp
        val pinSize = 33.dp
        val pinBorder = 1.dp
        val pinEmojiSize = 18f.sp
        val monthSize = 18.sp
        val weekdaySize = 10.5f.sp
        val daySize = 17.sp
    }

    /** Барабан выбора времени и чипы частого времени над ним. */
    object TimeWheel {
        val cardHeight = 132.dp
        val itemHeight = 36.dp
        val bandHeight = 44.dp
        val bandHorizontalMargin = 14.dp
        val colonWidth = 22.dp
        val chipSpacing = 6.dp
        val chipVerticalPadding = 10.dp
        val chipHorizontalPadding = 12.dp
        val itemSize = 24.sp
        val colonSize = 24.sp
        val chipSize = 16f.sp
    }

    /** Плитка эмоции. */
    object EmotionTile {
        val spacing = 12.dp
        val verticalPadding = 19.dp
        val horizontalPadding = 12.dp
        val contentSpacing = 10.dp
        val selectedBorder = 2.dp
        val emojiSize = 36.sp
    }

    /** Квадратная плитка эмоции в ряду. */
    object EmotionChip {
        val size = 65.dp
        val spacing = 11.dp
        val selectedBorder = 2.dp
        val emojiSize = 31.sp
    }

    /** Круглый пин эмоции. */
    object EmotionPin {
        val size = 58.dp
        val border = 4.dp
        val emojiSize = 28.sp
    }

    /** Превью выбранного места. */
    object PlacePreview {
        val height = 180.dp
        val addressMargin = 14.dp
        val addressVerticalPadding = 8.dp
        val addressHorizontalPadding = 14.dp
    }

    /** Строка воспоминания и отступ между строками в списке. */
    object MemoryRow {
        val padding = 14.dp
        val spacing = 10.dp
        val badgeSpacing = 14.dp
        val textSpacing = 3.dp
    }

    /** Режим выбора места на карте: прицел по центру. */
    object PlacePicker {
        val pinHeight = 52.dp
        val pinSize = 30.dp
        val pinBorder = 4.dp
        val pinStemWidth = 3.dp
        val pinStemHeight = 14.dp
        val pinAnchor = 8.dp
        val pinLift = 10.dp
    }

    /** Панель модального экрана. */
    object ModalTopBar {
        val horizontalPadding = 8.dp
        val topPadding = 8.dp
        val bottomPadding = 8.dp
        val closeSize = 48.dp
        val closeIconSize = 24.dp
        val titleSpacing = 16.dp
    }

    /** Поле ввода. */
    object TextField {
        val verticalPadding = 12.dp
        val horizontalPadding = 20.dp
        val multilineMinHeight = 111.dp
        val labelSlot = 16.dp
        val labelSize = 12.sp
        val focusBorder = 2.dp
    }

    /** Плашка даты или времени события. */
    object DateTimeField {
        val border = 1.5.dp
        val verticalPadding = 12.dp
        val horizontalPadding = 14.dp
        val labelSpacing = 1.dp
        val iconSize = 24.dp
        val spacing = 8.dp

        /** Кегль подписи общий с полем ввода: это одна и та же подпись над значением. */
        val labelSize = 12.sp
    }

    /** Плитка медиа: превью снимка и кнопка добавления. */
    object MediaTile {
        val size = 87.dp
        val spacing = 12.dp
        val addBorder = 2.dp
        val addDash = 6.dp
        val addGap = 5.dp
        val removeSize = 26.dp
        val removeIconSize = 11.dp
        val removeTouchSize = 38.dp
    }

    /** Круглая кнопка с иконкой поверх карты или фотографии. */
    object IconButton {
        val size = 40.dp
        val iconSize = 24.dp
        val backgroundAlpha = 0.94f
    }

    /** Эмоция строкой: эмодзи и название. */
    object EmotionTag {
        val spacing = 10.dp
        val verticalPadding = 7.dp
        val horizontalPadding = 13.dp
        val emojiSize = 18.sp
    }

    /** Квадратная плитка эмоции без выбора: иконка воспоминания. */
    object EmotionBadge {
        val size = 62.dp
        val emojiSize = 30.sp
    }

    /** Значок видео поверх кадра. */
    object MediaBadge {
        val size = 34.dp
        val iconSize = 15.dp
        val largeSize = 72.dp
        val largeIconSize = 32.dp
    }

    /** Полноэкранный просмотр снимков. */
    object MediaViewer {
        val actionsPadding = 16.dp
        val counterMargin = 24.dp
        val dismissDistance = 120.dp
        val counterVerticalPadding = 7.dp
        val counterHorizontalPadding = 14.dp
    }

    /** Экран воспоминания. */
    object Memory {
        val haloSize = 143.dp
        val pinSize = 94.dp
        val pinBorder = 5.dp
        val pinEmojiSize = 44.sp
        val sheetCornerOverlap = 28.dp
        val sheetHorizontalPadding = 27.dp
        val sheetTopPadding = 22.dp
        val sheetBottomPadding = 32.dp
        val sheetSpacing = 17.dp
        val headerSpacing = 6.dp
        val mediaSpacing = 10.dp
        val mediaSize = 107.dp
        val dividerThickness = 1.dp
    }

    /** Пункт меню в листе действий. */
    object SheetAction {
        val height = 60.dp
        val iconSize = 24.dp
        val iconSpacing = 14.dp
    }

    /** Экран итогов. */
    object Summary {
        val emojiSize = 44.sp
        val cardSpacing = 12.dp
        val barHeight = 12.dp
        val barGap = 2.dp
        val legendEmojiSize = 18.sp
        val recallPhotoHeight = 200.dp
    }

    /** Основная кнопка. */
    object PrimaryButton {
        val height = 56.dp
        val disabledContainerAlpha = 0.12f
        val disabledContentAlpha = 0.38f
    }

    /** Индикатор страниц пейджера. */
    object PagerIndicator {
        val dotSize = 8.dp
        val activeWidth = 24.dp
    }

    /** Текстовая кнопка без фона. */
    object TextButton {
        val height = 48.dp
        val horizontalPadding = 12.dp
    }

    /** Плашка с путем в системных настройках. */
    object SettingsPathCard {
        val iconSize = 24.dp
    }

    /** Иллюстрации онбординга. */
    object OnboardingIllustration {
        val sceneWidth = 360.dp
        val characterScale = 1.25f
        val cardMargin = 16.dp
        val previewSize = 72.dp
        val previewBadgeSize = 28.dp
        val previewBadgeIconSize = 14.dp
        val titleGap = 2.dp
        val dayRowGap = 6.dp
        val dayCellSize = 40.dp
        val dayEmojiSize = 22.sp
    }

    /** Иллюстрация страницы геолокации. */
    object LocationIllustration {
        val youSize = 88.dp
        val youHaloSize = 124.dp
        val youHaloAlpha = 0.22f
        val pickerHeadSize = 48.dp
        val pickerBorder = 4.dp
        val pickerStemWidth = 4.dp
        val pickerStemHeight = 22.dp
        val pickerShadowWidth = 20.dp
        val pickerShadowHeight = 7.dp
        val pickerShadowAlpha = 0.14f
        val darkPickerShadowAlpha = 0.35f
    }

    /** Персонаж-эмоция. */
    object EmotionCharacter {
        val heightRatio = 1.2f
        val lookShift = 0.025f
    }

    /** Реплика персонажа. */
    object CharacterBubble {
        val verticalPadding = 6.dp
        val horizontalPadding = 12.dp
        val tailSize = 10.dp
    }

    /** Нижняя панель экрана разрешения. */
    object PermissionSheet {
        val topPadding = 24.dp
        val indicatorSpacing = 20.dp
        val darkBorder = 1.dp
        val illustrationOverlap = 28.dp
    }
}
