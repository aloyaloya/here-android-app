package ru.aloyaloya.map.model

import ru.aloyaloya.domain.model.Memory

/**
 * Воспоминание, выбранное нажатием на метку карты.
 *
 * @property memory Выбранное воспоминание.
 * @property address Адрес точки или `null`, пока он не определен или определить не вышло.
 */
data class SelectedMemory(
    val memory: Memory,
    val address: String? = null
)
