package ru.aloyaloya.onboarding.presentation

/** Шаг страницы геолокации. */
enum class LocationStep {
    /** Первый запрос разрешения. */
    REQUEST,

    /** Отказ, система еще покажет запрос. */
    DENIED,

    /** Отказ насовсем: включить можно только в настройках. */
    BLOCKED
}
