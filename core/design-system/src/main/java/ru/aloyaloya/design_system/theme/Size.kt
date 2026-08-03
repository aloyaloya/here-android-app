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
        val topMargin = 10.dp
        val contentPadding = 11.dp
        val titlePadding = 24.dp
        val actionSize = 52.dp
        val actionIconSize = 24.dp
    }

    /** Нижняя панель навигации. */
    object NavBar {
        val height = 82.dp
        val horizontalMargin = 22.dp
        val bottomMargin = 30.dp
        val itemIconSize = 28.dp
        val itemIconLabelSpacing = 5.dp
    }

    /** FAB-кнопка. */
    object Fab {
        val size = 72.dp
        val iconSize = 28.dp
        val endMargin = 18.dp
        val bottomMargin = 136.dp
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

    /** Основная кнопка. */
    object PrimaryButton {
        val height = 65.dp
    }
}
