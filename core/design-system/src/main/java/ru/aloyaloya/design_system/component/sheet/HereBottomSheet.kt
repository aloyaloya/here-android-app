package ru.aloyaloya.design_system.component.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import ru.aloyaloya.design_system.extension.sheetShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

private val SheetScrim = Color(0x57241F1C)

/**
 * BottomSheet приложения Here.
 *
 * Обертка над [ModalBottomSheet] с формой, цветами и хэндлом из дизайн-системы.
 * Отступы контента задает вызывающая сторона.
 *
 * Фон, обводку и хэндл лист рисует сам, а [ModalBottomSheet] остается прозрачным:
 * его собственный контейнер сдвигается при перетаскивании отдельно от переданного
 * модификатора, и обводка на нем уезжает от края листа.
 *
 * @param onDismissRequest Колбэк закрытия листа свайпом или тапом по затемнению.
 * @param modifier [Modifier], применяемый к листу.
 * @param content Контент листа.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HereBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(),
        shape = HereShape.sheet,
        containerColor = Color.Transparent,
        scrimColor = SheetScrim,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sheetShadow(HereShape.sheet)
                .clip(HereShape.sheet)
                .background(HereTheme.colors.background)
        ) {
            HereBottomSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            content()
        }
    }
}

@Composable
private fun HereBottomSheetHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = HereSize.Sheet.handleVerticalPadding)
            .size(
                width = HereSize.Sheet.handleWidth,
                height = HereSize.Sheet.handleHeight
            )
            .clip(HereShape.pill)
            .background(HereTheme.colors.surfaceMuted)
    )
}