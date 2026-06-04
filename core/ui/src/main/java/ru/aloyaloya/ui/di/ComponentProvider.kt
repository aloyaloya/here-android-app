package ru.aloyaloya.ui.di

import kotlin.reflect.KClass

interface ComponentProvider {
    /**
     * Возвращает DI-компонент по строковому ключу и ожидаемому типу.
     *
     * @param key Ключ компонента (например, `map`, `calendar`, `analytic`).
     * @param clazz Ожидаемый KClass возвращаемого компонента.
     * @return Экземпляр компонента, приведенный к типу [T].
     * @throws IllegalArgumentException Если ключ не поддерживается реализацией.
     */
    fun <T : Any> provideComponent(key: String, clazz: KClass<T>): T
}