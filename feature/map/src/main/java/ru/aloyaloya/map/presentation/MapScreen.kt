package ru.aloyaloya.map.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import ru.aloyaloya.mapkit.ui.YandexMap
import ru.aloyaloya.ui.theme.LocalAppDarkTheme

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

private fun Context.hasLocationPermission(): Boolean =
    locationPermissions.any { perm ->
        ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
    }

@Composable
fun MapScreen(uiState: MapUiState) {
    val isDarkTheme = LocalAppDarkTheme.current
    val context = LocalContext.current
    var locationGranted by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        locationGranted = result.values.any { it }
    }

    LaunchedEffect(Unit) {
        if (!locationGranted) {
            launcher.launch(locationPermissions)
        }
    }

    YandexMap(
        modifier = Modifier.fillMaxSize(),
        config = uiState.mapConfig,
        locationEnabled = locationGranted,
        isDarkTheme = isDarkTheme
    )
}
