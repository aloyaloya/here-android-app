package ru.aloyaloya.domain.repository

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
     * @return Короткий адрес вида «улица, дом» или `null`, если определить не вышло:
     * что показать в этом случае, решает экран.
     */
    suspend fun resolve(latitude: Double, longitude: Double): String?
}