package ru.aloyaloya.design_system.component.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import ru.aloyaloya.design_system.component.text.HereSectionLabel
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Секция экрана: подпись и карточка с содержимым под ней.
 *
 * @param title Подпись секции.
 * @param modifier [Modifier], применяемый к секции.
 * @param content Содержимое карточки. Отступы внутри задает само содержимое.
 */
@Composable
fun HereSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.s),
        modifier = modifier
    ) {
        HereSectionLabel(
            text = title,
            modifier = Modifier.padding(horizontal = HereSpacing.xs)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(HereShape.tile)
                .background(HereTheme.colors.surface),
            content = content
        )
    }
}
