package ru.aloyaloya.design_system.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object HereMotion {
    fun <T> spatial(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.9f, stiffness = 700f)
    fun <T> effects(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 1600f)

    fun <T> bouncy(): FiniteAnimationSpec<T> =
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)

    fun <T> fadeThroughEnter(): FiniteAnimationSpec<T> =
        tween(durationMillis = Duration.medium, delayMillis = Duration.short)

    fun <T> fadeThroughExit(): FiniteAnimationSpec<T> = tween(durationMillis = Duration.short)

    object Duration {
        const val short = 150
        const val medium = 250
        const val long = 450
    }
}
