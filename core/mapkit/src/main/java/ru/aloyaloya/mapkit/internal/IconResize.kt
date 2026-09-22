package ru.aloyaloya.mapkit.internal

import android.animation.ValueAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd

/**
 * Сжимает иконки на карте в точку и раздувает обратно.
 */
internal class IconResize(private val onScale: (Float) -> Unit) {

    val scale get() = value.coerceAtLeast(MIN_SCALE)

    private var value = 1f
    private var animator: ValueAnimator? = null

    fun animateTo(visible: Boolean, onEnd: () -> Unit = {}) {
        val target = if (visible) 1f else 0f
        stop()
        if (value == target) {
            onEnd()
            return
        }

        animator = ValueAnimator.ofFloat(value, target).apply {
            duration = RESIZE_MILLIS
            interpolator = if (visible) OvershootInterpolator() else AccelerateInterpolator()
            addUpdateListener { animator ->
                value = animator.animatedValue as Float
                onScale(scale)
            }
            doOnEnd { onEnd() }
            start()
        }
    }

    fun snapTo(visible: Boolean) {
        stop()
        value = if (visible) 1f else 0f
        onScale(scale)
    }

    private fun stop() {
        animator?.removeAllListeners()
        animator?.cancel()
        animator = null
    }

    private companion object {
        const val RESIZE_MILLIS = 260L
        const val MIN_SCALE = 0.01f
    }
}
