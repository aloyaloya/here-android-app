package ru.aloyaloya.memory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.memory.model.NewMemorySheet
import ru.aloyaloya.memory.model.NewMemoryUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class NewMemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewMemoryUiState(happenedAt = LocalDateTime.now()))
    val uiState: StateFlow<NewMemoryUiState> = _uiState.asStateFlow()

    private var latitude: Double? = null
    private var longitude: Double? = null

    /**
     * Принимает то, с чем экран открыли: эмоцию из листа и точку на карте.
     *
     * Вызывается при каждом появлении экрана, поэтому уже принятые аргументы не
     * трогает: иначе поворот экрана вернул бы смененную эмоцию к исходной.
     * Координаты в состоянии не лежат — экран их не показывает и не меняет.
     * По ним же один раз спрашивается адрес: он приходит позже самого экрана.
     */
    fun setInitialArgs(emotion: Emotion, latitude: Double, longitude: Double) {
        if (this.latitude == null) {
            this.latitude = latitude
            this.longitude = longitude

            viewModelScope.launch {
                val address = addressRepository.resolve(
                    latitude = latitude,
                    longitude = longitude
                )
                _uiState.update { it.copy(address = address) }
            }
        }
        _uiState.update { state ->
            if (state.emotion == null) state.copy(emotion = emotion) else state
        }
    }

    fun onEmotionSelected(emotion: Emotion) {
        _uiState.update { it.copy(emotion = emotion) }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDateFieldClick() {
        _uiState.update { it.copy(activeSheet = NewMemorySheet.DATE) }
    }

    fun onTimeFieldClick() {
        _uiState.update { it.copy(activeSheet = NewMemorySheet.TIME) }
    }

    fun onSheetDismiss() {
        _uiState.update { it.copy(activeSheet = null) }
    }

    /** Применяет дату из листа и закрывает его. Время события остается прежним. */
    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(happenedAt = it.happenedAt.with(date), activeSheet = null) }
    }

    /** Применяет время из листа и закрывает его. Дата события остается прежней. */
    fun onTimeSelected(time: LocalTime) {
        _uiState.update { it.copy(happenedAt = it.happenedAt.with(time), activeSheet = null) }
    }

    /**
     * Записывает воспоминание в базу.
     *
     * Пока идет запись, состояние держит [NewMemoryUiState.saving], чтобы повторное
     * нажатие не создало второе воспоминание. После записи экран закрывается.
     */
    fun onSave() {
        val state = _uiState.value
        val emotion = state.emotion
        val latitude = latitude
        val longitude = longitude
        if (!state.saveEnabled || emotion == null || latitude == null || longitude == null) return

        _uiState.update { it.copy(saving = true) }

        viewModelScope.launch {
            memoryRepository.create(
                Memory(
                    title = state.title.trim(),
                    description = state.description.trim(),
                    latitude = latitude,
                    longitude = longitude,
                    emotion = emotion,
                    createdAt = System.currentTimeMillis(),
                    happenedAt = state.happenedAt.toEpochMilli()
                )
            )
            _uiState.update { it.copy(saving = false, saved = true) }
        }
    }
}

/**
 * Переводит время события в миллисекунды для базы.
 *
 * [LocalDateTime] — это показания календаря и часов без привязки к поясу, поэтому
 * момент из них получается только вместе с поясом устройства.
 */
private fun LocalDateTime.toEpochMilli(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
