package ru.aloyaloya.summary.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.dominantEmotion
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.summary.model.EmotionShare
import ru.aloyaloya.summary.model.MoodPlace
import ru.aloyaloya.summary.model.SummaryPeriod
import ru.aloyaloya.summary.model.SummaryUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/** Воспоминания ближе этого расстояния друг к другу считаются одним местом. */
private const val PLACE_RADIUS_METERS = 150f

/** Сколько мест показывать в итогах. */
private const val PLACE_COUNT = 3

/** Точка места: по ней кешируется адрес. */
private typealias PlacePoint = Pair<Double, Double>

class SummaryViewModel @Inject constructor(
    memoryRepository: MemoryRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val period = MutableStateFlow(SummaryPeriod.MONTH)

    private val addresses = MutableStateFlow<Map<PlacePoint, String?>>(emptyMap())

    val uiState: StateFlow<SummaryUiState> =
        combine(memoryRepository.observeAll(), period, addresses) { memories, period, addresses ->
            val today = LocalDate.now()
            val inPeriod = memories.filter { memory ->
                period.contains(memory.happenedAt.toLocalDate(), today)
            }

            SummaryUiState.Content(
                period = period,
                memoryCount = inPeriod.size,
                dayCount = inPeriod.distinctBy { it.happenedAt.toLocalDate() }.size,
                dominantEmotion = inPeriod.dominantEmotion(),
                emotionShares = inPeriod
                    .groupingBy(Memory::emotion)
                    .eachCount()
                    .map { (emotion, count) -> EmotionShare(emotion, count) }
                    .sortedByDescending(EmotionShare::count),
                places = inPeriod
                    .groupIntoPlaces()
                    .take(PLACE_COUNT)
                    .map { place -> place.toMoodPlace(addresses) }
            )
        }
            .onEach { state -> state.places.forEach(::resolveAddress) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SummaryUiState.Loading
            )

    fun onPeriodSelected(period: SummaryPeriod) {
        this.period.value = period
    }

    private fun resolveAddress(place: MoodPlace) {
        val point = place.latitude to place.longitude
        if (point in addresses.value) return

        addresses.update { it + (point to null) }
        viewModelScope.launch {
            val address = addressRepository.resolve(place.latitude, place.longitude)
            addresses.update { it + (point to address) }
        }
    }
}

private fun List<Memory>.groupIntoPlaces(): List<List<Memory>> {
    val places = mutableListOf<MutableList<Memory>>()

    sortedByDescending(Memory::happenedAt).forEach { memory ->
        val place = places.firstOrNull { place -> place.first().isNear(memory) }
        if (place != null) place += memory else places += mutableListOf(memory)
    }

    return places.sortedWith(
        compareByDescending<List<Memory>> { it.size }
            .thenByDescending { it.first().happenedAt }
    )
}

private fun Memory.isNear(other: Memory): Boolean {
    val distance = FloatArray(1)
    Location.distanceBetween(latitude, longitude, other.latitude, other.longitude, distance)
    return distance.first() <= PLACE_RADIUS_METERS
}

private fun List<Memory>.toMoodPlace(addresses: Map<PlacePoint, String?>): MoodPlace {
    val anchor = first()

    return MoodPlace(
        latitude = anchor.latitude,
        longitude = anchor.longitude,
        memoryCount = size,
        dominantEmotion = checkNotNull(dominantEmotion()),
        address = addresses[anchor.latitude to anchor.longitude]
    )
}

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
