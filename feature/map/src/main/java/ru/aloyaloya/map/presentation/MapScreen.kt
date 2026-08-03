package ru.aloyaloya.map.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.component.button.HereFab
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.mapkit.model.UserLocationStyle
import ru.aloyaloya.mapkit.ui.HERE_LOGO_TOP_INSET_DP
import ru.aloyaloya.mapkit.ui.YandexMap
import ru.aloyaloya.ui.theme.LocalAppDarkTheme

/** Прозрачность круга точности вокруг маркера. */
private const val USER_LOCATION_ACCURACY_ALPHA = 0.10f

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

    when (uiState) {
        MapUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HereTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HereTheme.colors.accent)
            }
        }

        is MapUiState.Content -> {
            var emotionPickerVisible by rememberSaveable { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize()) {
                MapContent(
                    uiState = uiState,
                    locationEnabled = locationGranted,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.fillMaxSize()
                )

                HereFab(
                    onClick = { emotionPickerVisible = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(
                            end = HereSize.Fab.endMargin,
                            bottom = HereSize.Fab.bottomMargin
                        )
                )
            }

            if (emotionPickerVisible) {
                EmotionPickerSheet(
                    onDismissRequest = { emotionPickerVisible = false },
                    onEmotionConfirmed = { emotionPickerVisible = false }
                )
            }
        }
    }
}

/**
 * Карта на весь экран.
 *
 * Логотип Яндекса должен оставаться под верхней панелью, а карта рисуется под
 * системными панелями, поэтому к отступу логотипа добавляется высота статус-бара.
 */
@Composable
private fun MapContent(
    uiState: MapUiState.Content,
    locationEnabled: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier
) {
    val statusBarInset = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()

    val colors = HereTheme.colors
    val userLocationStyle = remember(colors) {
        UserLocationStyle(
            fill = colors.accent,
            outline = colors.surface,
            accuracy = colors.accent.copy(alpha = USER_LOCATION_ACCURACY_ALPHA)
        )
    }

    YandexMap(
        userLocationStyle = userLocationStyle,
        modifier = modifier,
        config = uiState.mapConfig,
        locationEnabled = locationEnabled,
        isDarkTheme = isDarkTheme,
        logoTopInset = HERE_LOGO_TOP_INSET_DP.dp + statusBarInset
    )
}
