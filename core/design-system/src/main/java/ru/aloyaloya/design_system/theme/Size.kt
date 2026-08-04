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
        val height = 74.dp
        val horizontalMargin = 18.dp
        val topMargin = 4.dp
        val contentPadding = 11.dp
        val titlePadding = 24.dp
        val actionSize = 52.dp
        val actionIconSize = 24.dp
    }

    /** Нижняя панель навигации. */
    object NavBar {
        val height = 82.dp
        val horizontalMargin = 22.dp
        val bottomMargin = 20.dp
        val itemIconSize = 28.dp
        val itemIconLabelSpacing = 5.dp
    }

    /** FAB-кнопка. */
    object Fab {
        val size = 72.dp
        val iconSize = 28.dp
        val endMargin = 18.dp
        val bottomMargin = 126.dp
    }

    /** Нижний лист. */
    object Sheet {
        val horizontalPadding = 27.dp
        val bottomPadding = 36.dp
        val contentSpacing = 22.dp
        val handleWidth = 48.dp
        val handleHeight = 5.dp
        val handleVerticalPadding = 17.dp
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
        val height = 169.dp
        val addressMargin = 14.dp
        val addressVerticalPadding = 8.dp
        val addressHorizontalPadding = 14.dp
    }

    /** Панель модального экрана. */
    object ModalTopBar {
        val horizontalPadding = 27.dp
        val topPadding = 19.dp
        val bottomPadding = 12.dp
    }

    /** Поле ввода. */
    object TextField {
        val verticalPadding = 18.dp
        val horizontalPadding = 20.dp
        val multilineMinHeight = 111.dp
    }

    /** Плашка даты или времени события. */
    object DateTimeField {
        val border = 1.5.dp
        val verticalPadding = 16.dp
        val horizontalPadding = 14.dp
        val labelSpacing = 1.dp
        val iconSize = 24.dp
        val spacing = 8.dp
        val labelSize = 12.sp
        val labelLetterSpacing = 0.8.sp
    }

    /** Плитка медиа: превью снимка и кнопка добавления. */
    object MediaTile {
        val size = 87.dp
        val spacing = 12.dp
        val addBorder = 2.dp
        val addDash = 6.dp
        val addGap = 5.dp
    }

    /** Основная кнопка. */
    object PrimaryButton {
        val height = 65.dp
    }
}
