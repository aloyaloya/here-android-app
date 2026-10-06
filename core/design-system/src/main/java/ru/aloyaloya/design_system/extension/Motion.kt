package ru.aloyaloya.design_system.extension

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

/** Включены ли анимации в системе: при нулевом масштабе длительности выключены. */
@Composable
fun rememberMotionEnabled(): Boolean {
    val resolver = LocalContext.current.contentResolver

    return remember(resolver) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }
}

/** Можно ли крутить бесконечные анимации: они включены и экран виден. */
@Composable
fun rememberLoopsEnabled(): Boolean {
    val state by LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    return rememberMotionEnabled() && state.isAtLeast(Lifecycle.State.RESUMED)
}
