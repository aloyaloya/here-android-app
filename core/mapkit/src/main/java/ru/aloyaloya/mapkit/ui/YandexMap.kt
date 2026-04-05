package ru.aloyaloya.mapkit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import ru.aloyaloya.mapkit.internal.UserLocationBinder
import ru.aloyaloya.mapkit.model.YandexMapConfig

/** [MapView] в Compose: lifecycle, зум, геослой при [locationEnabled]. Пермишны — снаружи. */
@Composable
fun YandexMap(
    modifier: Modifier = Modifier,
    config: YandexMapConfig = YandexMapConfig.Default,
    locationEnabled: Boolean = false,
) {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember(context) { MapView(context) }
    val binder = remember(mapView, config.userLocationZoom, appContext) {
        UserLocationBinder(mapView, config.userLocationZoom, appContext)
    }

    val locationEnabledState = rememberUpdatedState(locationEnabled)

    LaunchedEffect(mapView, binder, config.minZoom, config.maxZoom, config.userLocationZoom) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val mapKit = MapKitFactory.getInstance()
            mapKit.onStart()
            mapView.onStart()
            try {
                applyCameraZoomBounds(mapView, config)
                coroutineScope {
                    val locationJob = launch {
                        snapshotFlow { locationEnabledState.value }.collectLatest { enabled ->
                            if (enabled) binder.attach() else binder.detach()
                        }
                    }
                    try {
                        awaitCancellation()
                    } finally {
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
        modifier = modifier,
    )
}

private fun applyCameraZoomBounds(mapView: MapView, config: YandexMapConfig) {
    mapView.mapWindow.map.cameraBounds.apply {
        setMinZoomPreference(config.minZoom)
        setMaxZoomPreference(config.maxZoom)
    }
}
