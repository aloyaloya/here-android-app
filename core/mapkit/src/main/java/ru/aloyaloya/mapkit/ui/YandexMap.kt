package ru.aloyaloya.mapkit.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.aloyaloya.mapkit.R
import ru.aloyaloya.mapkit.internal.UserLocationBinder
import ru.aloyaloya.mapkit.model.UserLocationStyle
import ru.aloyaloya.mapkit.model.YandexMapConfig

/**
 * [MapView] в Compose: lifecycle, зум, стиль по [isDarkTheme], геослой при [locationEnabled].
 * Пермишны — снаружи.
 *
 * @param state Держатель карты: через него экран спрашивает, куда наведена камера.
 * @param logoTopInset Отступ логотипа Яндекса от верха карты. По умолчанию логотип опущен
 * под верхнюю панель, а экран добавляет к отступу системные insets.
 * @param userLocationStyle Цвета маркера текущего положения: модуль берет их снаружи,
 * чтобы маркер следовал за темой приложения.
 */
@Composable
fun YandexMap(
    userLocationStyle: UserLocationStyle,
    modifier: Modifier = Modifier,
    state: YandexMapState = rememberYandexMapState(),
    config: YandexMapConfig = YandexMapConfig.Default,
    locationEnabled: Boolean = false,
    isDarkTheme: Boolean = false,
    logoTopInset: Dp = HERE_LOGO_TOP_INSET_DP.dp
) {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember(context) { MapView(context) }

    DisposableEffect(state, mapView) {
        state.mapView = mapView
        onDispose { state.mapView = null }
    }

    LaunchedEffect(mapView, logoTopInset) {
        mapView.applyHereLogoPlacement(topInsetDp = logoTopInset.value.toInt())
    }

    val userLocationStyleState = rememberUpdatedState(userLocationStyle)

    val binder = remember(mapView, config.userLocationZoom, appContext) {
        UserLocationBinder(mapView, config.userLocationZoom, appContext) {
            userLocationStyleState.value
        }
    }

    LaunchedEffect(binder, userLocationStyle) {
        binder.applyStyle()
    }

    val styleLightJson = remember(appContext) {
        readRawJson(appContext, R.raw.light_theme_style)
    }
    val styleDarkJson = remember(appContext) {
        readRawJson(appContext, R.raw.dark_theme_style)
    }

    val locationEnabledState = rememberUpdatedState(locationEnabled)
    val isDarkThemeState = rememberUpdatedState(isDarkTheme)

    LaunchedEffect(mapView, binder, config.minZoom, config.maxZoom, config.userLocationZoom) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val mapKit = MapKitFactory.getInstance()
            mapKit.onStart()
            mapView.onStart()
            try {
                applyCameraZoomBounds(mapView, config)
                coroutineScope {
                    val mapStyleJob = launch {
                        snapshotFlow { isDarkThemeState.value }.collectLatest { dark ->
                            val json = if (dark) styleDarkJson else styleLightJson
                            mapView.mapWindow.map.setMapStyle(json)
                        }
                    }
                    val locationJob = launch {
                        snapshotFlow { locationEnabledState.value }.collectLatest { enabled ->
                            if (enabled) binder.attach() else binder.detach()
                        }
                    }
                    try {
                        awaitCancellation()
                    } finally {
                        mapStyleJob.cancel()
                        locationJob.cancel()
                    }
                }
            } finally {
                binder.detach()
                mapView.onStop()
                mapKit.onStop()
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}

private fun applyCameraZoomBounds(mapView: MapView, config: YandexMapConfig) {
    mapView.mapWindow.map.cameraBounds.apply {
        setMinZoomPreference(config.minZoom)
        setMaxZoomPreference(config.maxZoom)
    }
}

private fun readRawJson(context: Context, resId: Int): String =
    context.resources.openRawResource(resId).bufferedReader(Charsets.UTF_8).use { it.readText() }
