package ru.aloyaloya.design_system.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

private const val FADE_THROUGH_DELAY = 100

private const val AXIS_SHIFT_FRACTION = 0.1f

object HereMotion {
    fun <T> spatial(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.9f, stiffness = 700f)
    fun <T> effects(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 1600f)

    fun <T> bouncy(): FiniteAnimationSpec<T> =
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)

    fun <T> fadeThroughEnter(): FiniteAnimationSpec<T> =
        tween(durationMillis = Duration.medium, delayMillis = FADE_THROUGH_DELAY)

    fun <T> fadeThroughExit(): FiniteAnimationSpec<T> = tween(durationMillis = Duration.short)

    fun axisShift(width: Int, forward: Boolean): Int {
        val shift = (width * AXIS_SHIFT_FRACTION).toInt()
        return if (forward) shift else -shift
    }

    object Duration {
        const val short = 150
        const val medium = 250
        const val long = 450
    }
}
