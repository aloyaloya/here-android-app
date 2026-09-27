package ru.aloyaloya.map.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import ru.aloyaloya.design_system.component.button.HereExtendedFab
import ru.aloyaloya.design_system.component.button.HereFab
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.map.R
import ru.aloyaloya.map.model.MapUiState
import ru.aloyaloya.map.presentation.component.PlaceMemoriesSheet
import ru.aloyaloya.map.presentation.component.PlacePin
import ru.aloyaloya.mapkit.model.MapLogoPlacement
import ru.aloyaloya.mapkit.model.MapMarker
import ru.aloyaloya.mapkit.model.MapMarkerIcon
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.mapkit.model.UserLocationStyle
import ru.aloyaloya.mapkit.ui.YandexMap
import ru.aloyaloya.mapkit.ui.YandexMapState
import ru.aloyaloya.mapkit.ui.rememberYandexMapState
import ru.aloyaloya.ui.emotion.color
import ru.aloyaloya.ui.emotion.emoji
import ru.aloyaloya.ui.theme.LocalAppDarkTheme
import ru.aloyaloya.design_system.R as DesignSystemR

/** Прозрачность круга точности вокруг маркера. */
private const val USER_LOCATION_ACCURACY_ALPHA = 0.10f

/** Кнопка внизу сначала сжимается, и только следом вырастает то, что ее сменяет. */
private const val ACTIONS_EXIT_MILLIS = 180
private const val ACTIONS_ENTER_MILLIS = 280

/** Кнопки, плашка и прицел растут и сжимаются так же, как метки на карте. */
private const val RESIZE_MILLIS = 260
private val GrowEasing = Easing(OvershootInterpolator()::getInterpolation)
private val ShrinkEasing = Easing(AccelerateInterpolator()::getInterpolation)
private val BottomOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f)

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

private fun Context.hasLocationPermission(): Boolean =
    locationPermissions.any { perm ->
        ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
    }

/**
 * Экран карты.
 *
 * Воспоминание ставится в два шага: сначала режим выбора места, где карта ездит под
 * неподвижным прицелом, и только потом лист эмоций. Точка берется в момент «Дальше» —
 * это то место, которое пользователь видел под прицелом.
 *
 * @param uiState Состояние экрана.
 * @param picking Включен ли режим выбора места. Режимом владеет приложение: он подменяет
 * панели, поэтому экран о нем сообщает, но не хранит.
 * @param onEmotionConfirmed Колбэк выбора эмоции в листе: отдает наверх эмоцию
 * и выбранную точку.
 * @param onMemoryClick Колбэк нажатия на метку воспоминания: с карты сразу
 * открывается экран воспоминания. Воспоминания в одной точке сперва показываются списком.
 * @param onPickStart Колбэк входа в режим выбора места.
 * @param onPickCancel Колбэк выхода из режима выбора места.
 * @param onPickPointChanged Колбэк остановки камеры в режиме выбора: по точке
 * определяется адрес.
 */
@Composable
fun MapScreen(
    uiState: MapUiState,
    picking: Boolean,
    onEmotionConfirmed: (Emotion, MapPoint) -> Unit,
    onMemoryClick: (Long) -> Unit,
    onPickStart: () -> Unit,
    onPickCancel: () -> Unit,
    onPickPointChanged: (MapPoint) -> Unit
) {
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
            var pickedPoint by remember { mutableStateOf<MapPoint?>(null) }
            var cameraMoving by remember { mutableStateOf(false) }
            var placeMemoryIds by rememberSaveable { mutableStateOf<List<Long>>(emptyList()) }
            val mapState = rememberYandexMapState()

            BackHandler(enabled = picking, onBack = onPickCancel)

            val onLocationClick = {
                if (locationGranted) {
                    mapState.moveToUserLocation()
                } else {
                    launcher.launch(locationPermissions)
                }
            }

            LaunchedEffect(picking) {
                if (picking) mapState.cameraTarget?.let(onPickPointChanged)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                MapContent(
                    uiState = uiState,
                    mapState = mapState,
                    picking = picking,
                    locationEnabled = locationGranted && !picking,
                    isDarkTheme = isDarkTheme,
                    onMarkerClick = onMemoryClick,
                    onClusterClick = { ids -> placeMemoryIds = ids },
                    onCameraMove = { point, settled ->
                        cameraMoving = !settled
                        if (settled && picking) onPickPointChanged(point)
                    },
                    modifier = Modifier.fillMaxSize()
                )

                AnimatedVisibility(
                    visible = picking,
                    enter = scaleIn(
                        tween(RESIZE_MILLIS, easing = GrowEasing),
                        transformOrigin = BottomOrigin
                    ),
                    exit = scaleOut(
                        tween(RESIZE_MILLIS, easing = ShrinkEasing),
                        transformOrigin = BottomOrigin
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = -HereSize.PlacePicker.pinHeight / 2)
                ) {
                    PlacePin(moving = cameraMoving)
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(HereSize.Fab.stackSpacing),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(bottom = HereSize.NavBar.height)
                        .padding(horizontal = HereSize.Fab.endMargin)
                        .padding(bottom = HereSize.Fab.barSpacing)
                ) {
                    AnimatedVisibility(
                        visible = locationGranted && mapState.awayFromUser,
                        enter = scaleIn(tween(RESIZE_MILLIS, easing = GrowEasing)),
                        exit = scaleOut(tween(RESIZE_MILLIS, easing = ShrinkEasing))
                    ) {
                        LocationFab(onClick = onLocationClick)
                    }

                    AnimatedContent(
                        targetState = picking,
                        transitionSpec = {
                            scaleIn(tween(ACTIONS_ENTER_MILLIS, ACTIONS_EXIT_MILLIS, GrowEasing)) togetherWith
                                scaleOut(tween(ACTIONS_EXIT_MILLIS, easing = ShrinkEasing)) using
                                SizeTransform(clip = false)
                        },
                        label = "map-action"
                    ) { isPicking ->
                        if (isPicking) {
                            HereExtendedFab(
                                text = stringResource(R.string.place_picker_next),
                                onClick = {
                                    pickedPoint = mapState.cameraTarget
                                    emotionPickerVisible = true
                                }
                            )
                        } else {
                            HereFab(onClick = onPickStart)
                        }
                    }
                }
            }

            if (placeMemoryIds.isNotEmpty()) {
                PlaceMemoriesSheet(
                    memories = uiState.memories
                        .filter { it.id in placeMemoryIds }
                        .sortedByDescending { it.happenedAt },
                    onMemoryClick = { id ->
                        placeMemoryIds = emptyList()
                        onMemoryClick(id)
                    },
                    onDismissRequest = { placeMemoryIds = emptyList() }
                )
            }

            if (emotionPickerVisible) {
                EmotionPickerSheet(
                    onDismissRequest = { emotionPickerVisible = false },
                    onEmotionConfirmed = { emotion ->
                        emotionPickerVisible = false
                        onPickCancel()
                        pickedPoint?.let { point -> onEmotionConfirmed(emotion, point) }
                    }
                )
            }
        }
    }
}

@Composable
private fun LocationFab(onClick: () -> Unit) {
    HereFab(
        onClick = onClick,
        icon = DesignSystemR.drawable.ic_location,
        contentDescription = stringResource(
            DesignSystemR.string.fab_location_content_description
        ),
        container = HereTheme.colors.onAccent,
        content = HereTheme.colors.accent
    )
}

/**
 * Карта на весь экран.
 *
 * Логотип Яндекса должен оставаться под верхней панелью, а карта рисуется под
 * системными панелями, поэтому к отступу логотипа добавляется высота статус-бара.
 *
 * В режиме выбора места чужие метки прячутся: под прицелом должно быть
 * видно само место, а не соседние воспоминания.
 */
@Composable
private fun MapContent(
    uiState: MapUiState.Content,
    mapState: YandexMapState,
    picking: Boolean,
    locationEnabled: Boolean,
    isDarkTheme: Boolean,
    onMarkerClick: (Long) -> Unit,
    onClusterClick: (List<Long>) -> Unit,
    onCameraMove: (MapPoint, Boolean) -> Unit,
    modifier: Modifier
) {
    val statusBarInset = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()

    val logoPlacement = MapLogoPlacement.UnderTopBar

    val colors = HereTheme.colors
    val markers = uiState.memories.toMarkers()
    val userLocationStyle = remember(colors) {
        UserLocationStyle(
            fill = colors.accent,
            outline = colors.surface,
            accuracy = colors.accent.copy(alpha = USER_LOCATION_ACCURACY_ALPHA)
        )
    }

    YandexMap(
        state = mapState,
        userLocationStyle = userLocationStyle,
        modifier = modifier,
        config = uiState.mapConfig,
        locationEnabled = locationEnabled,
        isDarkTheme = isDarkTheme,
        markers = markers,
        markersVisible = !picking,
        onMarkerClick = onMarkerClick,
        onClusterClick = onClusterClick,
        onCameraMove = onCameraMove,
        logoPlacement = logoPlacement.copy(
            verticalInset = logoPlacement.verticalInset + statusBarInset
        )
    )
}

/**
 * Переводит воспоминания в метки карты.
 *
 * Цвета эмоций живут в теме, поэтому метки собираются здесь, а не во вьюмодели.
 */
@Composable
private fun List<Memory>.toMarkers(): List<MapMarker> {
    val outline = HereTheme.colors.surface

    return map { memory ->
        MapMarker(
            id = memory.id,
            point = MapPoint(memory.latitude, memory.longitude),
            icon = MapMarkerIcon(
                emoji = memory.emotion.emoji,
                fill = memory.emotion.color.solid,
                outline = outline
            )
        )
    }
}
