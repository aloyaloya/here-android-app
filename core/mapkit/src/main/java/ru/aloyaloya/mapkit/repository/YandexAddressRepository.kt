package ru.aloyaloya.mapkit.repository

import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ru.aloyaloya.domain.repository.AddressRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/** Уровень детализации ответа: на нем геокодер доходит до дома, а не до района. */
private const val SEARCH_ZOOM = 17

/** Из ответа нужен только первый, самый точный топоним. */
private const val RESULT_PAGE_SIZE = 1

/**
 * Реализация [AddressRepository] на поиске MapKit.
 *
 * Поиск умеет работать и по скачанным офлайн-картам, поэтому менеджер создается
 * в режиме [SearchManagerType.COMBINED]: без сети адрес все равно может найтись.
 *
 * Весь API MapKit вызывается только с главного потока — сеть он крутит внутри себя
 * и возвращает готовый ответ колбэком.
 */
@Singleton
class YandexAddressRepository @Inject constructor() : AddressRepository {

    private val searchManager: SearchManager by lazy {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }

    override suspend fun resolve(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.Main) {
            requestToponym(Point(latitude, longitude))?.address?.toShortAddress()
        }

    /**
     * Спрашивает у поиска топоним в точке.
     *
     * Сессию нужно держать за ссылку до ответа и отменять вместе с корутиной,
     * иначе ответ придет в никуда.
     */
    private suspend fun requestToponym(point: Point): GeoObject? =
        suspendCancellableCoroutine { continuation ->
            val options = SearchOptions()
                .setSearchTypes(SearchType.GEO.value)
                .setResultPageSize(RESULT_PAGE_SIZE)

            val session = searchManager.submit(
                point,
                SEARCH_ZOOM,
                options,
                object : Session.SearchListener {
                    override fun onSearchResponse(response: Response) {
                        continuation.resume(response.collection.children.firstOrNull()?.obj)
                    }

                    override fun onSearchError(error: Error) {
                        continuation.resume(null)
                    }
                }
            )

            continuation.invokeOnCancellation { session.cancel() }
        }
}

/**
 * Адрес топонима или `null`, если найденный объект адреса не имеет.
 *
 * Один объект карты несет сразу несколько наборов метаданных, поэтому нужный
 * запрашивается по типу.
 */
private val GeoObject.address: Address?
    get() = metadataContainer.getItem(ToponymObjectMetadata::class.java)?.address

/**
 * Собирает из ответа строку вида «Малая Бронная, 22».
 *
 * Когда улицы нет — точка на реке или в парке, — берется готовая строка адреса
 * целиком: короче ее все равно не собрать.
 */
private fun Address.toShortAddress(): String? {
    val street = nameOf(Address.Component.Kind.STREET)
    val house = nameOf(Address.Component.Kind.HOUSE)

    return when {
        street != null && house != null -> "$street, $house"
        street != null -> street
        else -> formattedAddress
    }
}

/**
 * Ищет в разобранном адресе часть нужного вида.
 *
 * Виды у части перечислены списком: дом с одним подъездом бывает одновременно
 * и домом, и входом.
 */
private fun Address.nameOf(kind: Address.Component.Kind): String? =
    components.firstOrNull { kind in it.kinds }?.name