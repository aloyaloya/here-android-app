package ru.aloyaloya.map.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

private const val MAP_STYLE_URL =
    "https://demotiles.maplibre.org/styles/osm-bright-gl-style/style.json"

@Composable
fun MapScreen(
    uiState: MapUiState
) {
    MapContent(uiState = uiState)
}

@Composable
private fun MapContent(
    uiState: MapUiState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // MapLibre.getInstance() вызываем до создания MapView,
    // а getMapAsync() — после добавления View в иерархию (в AndroidView factory).
    val mapView = remember(context) {
        MapLibre.getInstance(context)
        MapView(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { _ ->
            // В этот момент View добавляется в иерархию, поэтому getMapAsync() безопасен;
            // это аналог вызова после setContentView/findViewById в примере с Activity.
            mapView.apply {
                getMapAsync { map ->
                    map.setStyle(MAP_STYLE_URL)
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(40.0, 30.0))
                        .zoom(1.0)
                        .build()
                }
            }
        },
        modifier = modifier.fillMaxSize(),
    )
}
