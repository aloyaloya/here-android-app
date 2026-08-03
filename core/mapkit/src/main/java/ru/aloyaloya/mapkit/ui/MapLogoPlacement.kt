package ru.aloyaloya.mapkit.ui

import android.content.res.Resources
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.mapview.MapView

/** Отступ верхней панели от края экрана, её высота и небольшой зазор. */
const val HERE_LOGO_TOP_INSET_DP = 86

private const val HERE_LOGO_HORIZONTAL_INSET_DP = 0

/**
 * Переносит логотип Яндекса наверх по центру карты.
 *
 * Скрывать логотип нельзя по условиям использования MapKit, но его можно двигать.
 * Внизу экрана панель навигации и FAB, поэтому логотип уходит под верхнюю панель.
 *
 * Отступы MapKit задаются в пикселях, поэтому dp переводятся вручную.
 *
 * @param topInsetDp Отступ сверху. Экран добавляет к [HERE_LOGO_TOP_INSET_DP]
 * системные insets, если рисует карту edge-to-edge.
 * @param horizontalInsetDp Отступ от краёв экрана.
 */
fun MapView.applyHereLogoPlacement(
    topInsetDp: Int = HERE_LOGO_TOP_INSET_DP,
    horizontalInsetDp: Int = HERE_LOGO_HORIZONTAL_INSET_DP
) {
    val density = Resources.getSystem().displayMetrics.density
    mapWindow.map.logo.apply {
        setAlignment(
            Alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP)
        )
        setPadding(
            Padding(
                (horizontalInsetDp * density).toInt(),
                (topInsetDp * density).toInt()
            )
        )
    }
}
