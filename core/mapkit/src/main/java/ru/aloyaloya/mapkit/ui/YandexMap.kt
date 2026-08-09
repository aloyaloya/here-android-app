package ru.aloyaloya.mapkit.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.aloyaloya.mapkit.R
import ru.aloyaloya.mapkit.internal.UserLocationBinder
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.mapkit.model.UserLocationStyle
import ru.aloyaloya.mapkit.model.YandexMapConfig

/**
 * [MapView] в Compose: lifecycle, зум, стиль по [isDarkTheme], геослой при [locationEnabled].
 * Пермишны — снаружи.
 *
 * @param state Держатель карты: через него экран спрашивает, куда наведена камера.
 * @param startPosition Точка, на которую наводится камера. Наводится заново при каждом
 * появлении карты, а не только на первом кадре. При `null` камера остается как есть.
 * @param startZoom Зум, с которым камера встает на [startPosition].
 * @param logoTopInset Отступ логотипа Яндекса от верха карты. По умолчанию логотип опущен
 * под верхнюю панель, а экран добавляет к отступу системные insets.
 * @param userLocationStyle Цвета маркера текущего положения: модуль берет их снаружи,
 * чтобы маркер следовал за темой приложения. При `null` маркер не показывается.
 * @param movable Карта рисуется во вьюху, которая подчиняется скруглению и другим
 * преобразованиям родителя, а до первого кадра остается прозрачной. Нужна там, где карта
 * лежит в карточке. Стоит дороже обычной, поэтому на весь экран берется обычная.
 */
@Composable
fun YandexMap(
    modifier: Modifier = Modifier,
    state: YandexMapState = rememberYandexMapState(),
    config: YandexMapConfig = YandexMapConfig.Default,
    movable: Boolean = false,
    interactive: Boolean = true,
    startPosition: MapPoint? = null,
    startZoom: Float = 16f,
    userLocationStyle: UserLocationStyle? = null,
    locationEnabled: Boolean = false,
    isDarkTheme: Boolean = false,
    logoTopInset: Dp = HERE_LOGO_TOP_INSET_DP.dp
) {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val lifecycleOwner = LocalLifecycleOwner.current

    val styleLightJson = remember(appContext) {
        readRawJson(appContext, R.raw.light_theme_style)
    }
    val styleDarkJson = remember(appContext) {
        readRawJson(appContext, R.raw.dark_theme_style)
    }

    val mapView = remember(context, movable) {
        createMapView(context, movable).also { view ->
            applyMapTheme(
                mapView = view,
                isDark = isDarkTheme,
                styleJson = if (isDarkTheme) styleDarkJson else styleLightJson
            )
        }
    }

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

    val locationEnabledState = rememberUpdatedState(locationEnabled)
    val isDarkThemeState = rememberUpdatedState(isDarkTheme)

    LaunchedEffect(
        mapView,
        binder,
        config.minZoom,
        config.maxZoom,
        config.userLocationZoom,
        startPosition,
        startZoom
    ) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val mapKit = MapKitFactory.getInstance()
            mapKit.onStart()
            mapView.onStart()
            try {
                applyCameraZoomBounds(mapView, config)
                applyStartPosition(mapView, startPosition, startZoom)
                coroutineScope {
                    val mapStyleJob = launch {
                        snapshotFlow { isDarkThemeState.value }.collectLatest { dark ->
                            applyMapTheme(
                                mapView = mapView,
                                isDark = dark,
                                styleJson = if (dark) styleDarkJson else styleLightJson
                            )
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

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.matchParentSize()
        )

        if (!interactive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) awaitPointerEvent()
                        }
                    }
            )
        }
    }
}

/**
 * Создает карту нужного вида.
 *
 * Вид отрисовки MapKit выбирает в конструкторе по атрибутам разметки и после уже не меняет,
 * поэтому подвижная карта поднимается из [R.layout.view_movable_map], а не вызовом конструктора.
 */
private fun createMapView(context: Context, movable: Boolean): MapView =
    if (movable) {
        LayoutInflater.from(context).inflate(R.layout.view_movable_map, null) as MapView
    } else {
        MapView(context)
    }

/**
 * Приводит карту к теме приложения.
 * Вызывается сразу после создания карты, до первого кадра, и потом на каждую смену темы.
 */
private fun applyMapTheme(mapView: MapView, isDark: Boolean, styleJson: String) {
    mapView.mapWindow.map.apply {
        isNightModeEnabled = isDark
        setMapStyle(styleJson)
    }
}

private fun applyCameraZoomBounds(mapView: MapView, config: YandexMapConfig) {
    mapView.mapWindow.map.cameraBounds.apply {
        setMinZoomPreference(config.minZoom)
        setMaxZoomPreference(config.maxZoom)
    }
}

private fun applyStartPosition(mapView: MapView, startPosition: MapPoint?, startZoom: Float) {
    if (startPosition == null) return

    val target = Point(startPosition.latitude, startPosition.longitude)
    val cameraPosition = CameraPosition(target, startZoom, 0f, 0f)

    mapView.mapWindow.map.move(cameraPosition)
}

private fun readRawJson(context: Context, resId: Int): String =
    context.resources.openRawResource(resId).bufferedReader(Charsets.UTF_8).use { it.readText() }
