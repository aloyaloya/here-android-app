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
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.memory.model.NewMemoryUiState
import javax.inject.Inject

/** Адрес-заглушка: геокодер к экрану еще не подключен. */
private const val MOCK_ADDRESS = "Малая Бронная, 22"

class NewMemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewMemoryUiState(address = MOCK_ADDRESS))
    val uiState: StateFlow<NewMemoryUiState> = _uiState.asStateFlow()

    private var latitude: Double? = null
    private var longitude: Double? = null

    /**
     * Принимает то, с чем экран открыли: эмоцию из листа и точку на карте.
     *
     * Вызывается при каждом появлении экрана, поэтому уже принятые аргументы не
     * трогает: иначе поворот экрана вернул бы смененную эмоцию к исходной.
     * Координаты в состоянии не лежат — экран их не показывает и не меняет.
     */
    fun setInitialArgs(emotion: Emotion, latitude: Double, longitude: Double) {
        if (this.latitude == null) {
            this.latitude = latitude
            this.longitude = longitude
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
                    createdAt = System.currentTimeMillis()
                )
            )
            _uiState.update { it.copy(saving = false, saved = true) }
        }
    }
}
