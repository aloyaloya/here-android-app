package ru.aloyaloya.memory.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.memory.di.MemoryComponent
import ru.aloyaloya.memory.model.MemoryFormArgs
import ru.aloyaloya.memory.presentation.MemoryFormScreen
import ru.aloyaloya.memory.presentation.MemoryFormViewModel
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
 * Маршрут редактирования воспоминания.
 *
 * Экран за ним тот же, что и за [NewMemoryRoute], но аргументы у них разные,
 * поэтому и маршрута два, без полей на все случаи сразу.
 *
 * @property memoryId Идентификатор изменяемого воспоминания.
 */
@Serializable
data class EditMemoryRoute(val memoryId: Long)

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
 * Выполняет переход на редактирование воспоминания.
 */
fun NavController.navigateToEditMemory(memoryId: Long) =
    navigate(route = EditMemoryRoute(memoryId))

/**
 * Регистрирует форму воспоминания как destination в [NavGraphBuilder].
 *
 * @param onClose Колбэк закрытия экрана: и по отмене, и после того,
 * как воспоминание записано. После редактирования возвращает на экран воспоминания,
 * который перечитает запись сам.
 */
fun NavGraphBuilder.memoryFormScreen(onClose: () -> Unit) {
    composable<NewMemoryRoute> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<NewMemoryRoute>()

        MemoryForm(
            args = MemoryFormArgs.New(
                emotion = route.emotion,
                latitude = route.latitude,
                longitude = route.longitude
            ),
            viewModelStoreOwner = navBackStackEntry,
            onClose = onClose
        )
    }

    composable<EditMemoryRoute> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<EditMemoryRoute>()

        MemoryForm(
            args = MemoryFormArgs.Edit(memoryId = route.memoryId),
            viewModelStoreOwner = navBackStackEntry,
            onClose = onClose
        )
    }
}

/**
 * Собирает форму: поднимает ViewModel на переданном destination и отдает ей аргументы.
 */
@Composable
private fun MemoryForm(
    args: MemoryFormArgs,
    viewModelStoreOwner: ViewModelStoreOwner,
    onClose: () -> Unit
) {
    val context = LocalContext.current.applicationContext

    val factory = (context as ComponentProvider)
        .provideComponent("memory", MemoryComponent::class)
        .viewModelFactory

    val viewModel = viewModel<MemoryFormViewModel>(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = factory
    )

    LaunchedEffect(args) {
        viewModel.setArgs(args)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onClose()
    }

    MemoryFormScreen(
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
