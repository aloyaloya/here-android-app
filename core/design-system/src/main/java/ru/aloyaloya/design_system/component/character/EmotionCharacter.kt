package ru.aloyaloya.design_system.component.character

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize

/** Персонаж: три слоя одной эмоции или «ты». */
enum class CharacterEmotion(
    @param:DrawableRes val body: Int,
    @param:DrawableRes val eyes: Int,
    @param:DrawableRes val face: Int
) {
    HAPPY(R.drawable.character_body_happy, R.drawable.character_eyes_happy, R.drawable.character_face_happy),
    TENDER(R.drawable.character_body_tender, R.drawable.character_eyes_tender, R.drawable.character_face_tender),
    SURPRISED(R.drawable.character_body_surprised, R.drawable.character_eyes_surprised, R.drawable.character_face_surprised),
    CALM(R.drawable.character_body_calm, R.drawable.character_eyes_calm, R.drawable.character_face_calm),
    SAD(R.drawable.character_body_sad, R.drawable.character_eyes_sad, R.drawable.character_face_sad),
    ANGRY(R.drawable.character_body_angry, R.drawable.character_eyes_angry, R.drawable.character_face_angry),
    YOU(R.drawable.character_body_you, R.drawable.character_eyes_you, R.drawable.character_face_you)
}

/**
 * Персонаж-эмоция из трех слоев: тело, глаза, лицо.
 *
 * @param emotion Эмоция персонажа.
 * @param size Ширина персонажа, высота в [HereSize.EmotionCharacter.heightRatio] раз больше.
 * @param look Взгляд: −1 влево, 0 прямо, 1 вправо.
 */
@Composable
fun EmotionCharacter(
    emotion: CharacterEmotion,
    size: Dp,
    modifier: Modifier = Modifier,
    look: Float = 0f
) {
    // TODO: сделать анимацию дыхания и моргания
    Box(modifier.size(width = size, height = size * HereSize.EmotionCharacter.heightRatio)) {
        Image(
            painter = painterResource(emotion.body),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )
        Image(
            painter = painterResource(emotion.eyes),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { translationX = look * size.toPx() * HereSize.EmotionCharacter.lookShift }
        )
        Image(
            painter = painterResource(emotion.face),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )
    }
}
