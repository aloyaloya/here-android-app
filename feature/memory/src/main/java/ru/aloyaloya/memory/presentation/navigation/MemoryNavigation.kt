package ru.aloyaloya.memory.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
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
import ru.aloyaloya.design_system.extension.LocalNavAnimatedVisibilityScope
import ru.aloyaloya.memory.di.MemoryComponent
import ru.aloyaloya.memory.presentation.MemoryScreen
import ru.aloyaloya.memory.presentation.MemoryViewModel
import ru.aloyaloya.ui.di.ComponentProvider

/**
 * Маршрут экрана воспоминания.
 *
 * @property memoryId Идентификатор воспоминания.
 */
@Serializable
data class MemoryRoute(val memoryId: Long)

/**
 * Выполняет переход на экран воспоминания.
 */
fun NavController.navigateToMemory(memoryId: Long) =
    navigate(route = MemoryRoute(memoryId))

/**
 * Регистрирует экран воспоминания как destination в [NavGraphBuilder].
 *
 * @param onBackClick Колбэк возврата назад.
 * @param onEditClick Колбэк перехода на редактирование воспоминания.
 */
fun NavGraphBuilder.memoryScreen(
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    composable<MemoryRoute> { navBackStackEntry ->

        val route = navBackStackEntry.toRoute<MemoryRoute>()

        val context = LocalContext.current.applicationContext

        val factory = (context as ComponentProvider)
            .provideComponent("memory", MemoryComponent::class)
            .viewModelFactory

        val viewModel = viewModel<MemoryViewModel>(
            viewModelStoreOwner = navBackStackEntry,
            factory = factory
        )

        LaunchedEffect(route) {
            viewModel.setMemoryId(route.memoryId)
        }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            MemoryScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onMoreClick = viewModel::onMoreClick,
                onEditClick = {
                    viewModel.onSheetDismiss()
                    onEditClick(route.memoryId)
                },
                onDeleteClick = viewModel::onDeleteClick,
                onMediaClick = viewModel::onMediaClick,
                onViewerDismiss = viewModel::onViewerDismiss,
                onDeleteConfirm = viewModel::onDeleteConfirm,
                onSheetDismiss = viewModel::onSheetDismiss
            )
        }
    }
}
