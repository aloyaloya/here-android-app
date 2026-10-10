package ru.aloyaloya.design_system.component.celebration

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import ru.aloyaloya.design_system.extension.rememberMotionEnabled
import ru.aloyaloya.design_system.theme.HereTheme
import java.util.concurrent.TimeUnit

/**
 * Залп конфетти цветами эмоций поверх экрана; без анимаций в системе не показывается.
 *
 * @param onEnded Колбэк: последняя частица исчезла.
 * @param origin Точка залпа в пикселях или `null` — над центром экрана.
 */
@Composable
fun Confetti(
    onEnded: () -> Unit,
    modifier: Modifier = Modifier,
    origin: Offset? = null
) {
    val currentOnEnded by rememberUpdatedState(onEnded)

    if (!rememberMotionEnabled()) {
        LaunchedEffect(Unit) { currentOnEnded() }
        return
    }

    val emotions = HereTheme.colors.emotions
    val party = remember(emotions, origin) {
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(
                emotions.happy,
                emotions.tender,
                emotions.surprised,
                emotions.calm,
                emotions.sad,
                emotions.angry
            ).map { it.solid.toArgb() },
            position = origin
                ?.let { Position.Absolute(it.x, it.y) }
                ?: Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )
    }

    KonfettiView(
        modifier = modifier.fillMaxSize(),
        parties = listOf(party),
        updateListener = object : OnParticleSystemUpdateListener {
            override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                if (activeSystems == 0) currentOnEnded()
            }
        }
    )
}
