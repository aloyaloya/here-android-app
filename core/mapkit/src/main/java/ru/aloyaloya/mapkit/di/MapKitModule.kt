package ru.aloyaloya.mapkit.di

import dagger.Binds
import dagger.Module
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.mapkit.repository.YandexAddressRepository
import javax.inject.Singleton

/**
 * Dagger-модуль карты.
 *
 * Отдает наружу то, что модуль умеет благодаря MapKit, но что нужно не только карте.
 */
@Module
interface MapKitModule {

    /** Привязывает [YandexAddressRepository] к [AddressRepository]. */
    @Binds
    @Singleton
    fun bindAddressRepository(impl: YandexAddressRepository): AddressRepository
}
