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
 */
@Serializable
data class NewMemoryRoute(val emotion: Emotion)

/**
 * Выполняет переход на экран нового воспоминания.
 *
 * @param emotion Эмоция, с которой открывается экран.
 */
fun NavController.navigateToNewMemory(emotion: Emotion) =
    navigate(route = NewMemoryRoute(emotion))

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

        LaunchedEffect(route.emotion) {
            viewModel.setInitialEmotion(route.emotion)
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
            onAddMediaClick = {},
            onSaveClick = viewModel::onSave,
            onCancelClick = onClose
        )
    }
}
