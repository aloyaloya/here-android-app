package ru.aloyaloya.memory.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.memory.di.MemoryComponent
import ru.aloyaloya.memory.presentation.NewMemoryScreen
import ru.aloyaloya.memory.presentation.NewMemoryViewModel
import ru.aloyaloya.ui.di.ComponentProvider

/**
 * Маршрут экрана нового воспоминания.
 *
 * @property emotion Эмоция, выбранная в листе на карте.
 * @property latitude Широта точки, на которой открылся лист.
 * @property longitude Долгота точки, на которой открылся лист.
 */
@Serializable
data class NewMemoryRoute(
    val emotion: Emotion,
    val latitude: Double,
    val longitude: Double
)

/**
 * Выполняет переход на экран нового воспоминания.
 *
 * @param emotion Эмоция, с которой открывается экран.
 * @param latitude Широта будущего воспоминания.
 * @param longitude Долгота будущего воспоминания.
 */
fun NavController.navigateToNewMemory(emotion: Emotion, latitude: Double, longitude: Double) =
    navigate(route = NewMemoryRoute(emotion, latitude, longitude))

/**
 * Регистрирует экран нового воспоминания как destination в [NavGraphBuilder].
 *
 * @param onClose Колбэк закрытия экрана: и по отмене, и после того,
 * как воспоминание записано.
 */
fun NavGraphBuilder.newMemoryScreen(onClose: () -> Unit) {
    composable<NewMemoryRoute> { navBackStackEntry ->

        val route = navBackStackEntry.toRoute<NewMemoryRoute>()

        val context = LocalContext.current.applicationContext

        val factory = (context as ComponentProvider)
            .provideComponent("memory", MemoryComponent::class)
            .viewModelFactory

        val viewModel = viewModel<NewMemoryViewModel>(
            viewModelStoreOwner = navBackStackEntry,
            factory = factory
        )

        LaunchedEffect(route) {
            viewModel.setInitialArgs(
                emotion = route.emotion,
                latitude = route.latitude,
                longitude = route.longitude
            )
        }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.saved) {
            if (uiState.saved) onClose()
        }

        NewMemoryScreen(
            uiState = uiState,
            onEmotionSelected = viewModel::onEmotionSelected,
            onTitleChanged = viewModel::onTitleChanged,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onDateFieldClick = viewModel::onDateFieldClick,
            onTimeFieldClick = viewModel::onTimeFieldClick,
            onSheetDismiss = viewModel::onSheetDismiss,
            onDateSelected = viewModel::onDateSelected,
            onTimeSelected = viewModel::onTimeSelected,
            onAddMediaClick = {},
            onSaveClick = viewModel::onSave,
            onCancelClick = onClose
        )
    }
}
