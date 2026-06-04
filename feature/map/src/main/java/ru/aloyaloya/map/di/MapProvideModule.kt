package ru.aloyaloya.map.di

import dagger.Module
import dagger.Provides
import ru.aloyaloya.mapkit.model.YandexMapConfig

/** Провайдер [YandexMapConfig] для карты. */
@Module
object MapProvideModule {

    @Provides
    fun provideYandexMapConfig(): YandexMapConfig = YandexMapConfig.Default
}
