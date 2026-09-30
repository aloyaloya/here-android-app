package ru.aloyaloya.domain.model

/**
 * Эмоция, которая встречается в воспоминаниях чаще других, или `null`, если их нет.
 *
 * При равенстве побеждает та, что была позже: она ближе к тому, чем период закончился.
 * Правило общее для календаря и итогов, чтобы экраны не называли разное настроение.
 */
fun List<Memory>.dominantEmotion(): Emotion? =
    groupBy(Memory::emotion)
        .maxWithOrNull(
            compareBy<Map.Entry<Emotion, List<Memory>>> { it.value.size }
                .thenBy { entry -> entry.value.maxOf(Memory::happenedAt) }
        )
        ?.key
