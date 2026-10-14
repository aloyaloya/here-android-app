package ru.aloyaloya.domain.repository

import ru.aloyaloya.domain.model.PlaceAddress

/**
 * Репозиторий адресов.
 *
 * Адрес не хранится вместе с воспоминанием: у воспоминания есть точка, а адрес
 * по ней всегда можно получить заново.
 */
interface AddressRepository {

    /**
     * Определяет адрес точки.
     *
     * @return Короткий адрес вида «улица, дом» с населенным пунктом или `null`, если определить не вышло:
     * что показать в этом случае, решает экран.
     */
    suspend fun resolve(latitude: Double, longitude: Double): PlaceAddress?
}