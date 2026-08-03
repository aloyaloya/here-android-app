package ru.aloyaloya.memory.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.memory.model.NewMemoryUiState
import javax.inject.Inject

/** Адрес-заглушка: геокодер к экрану еще не подключен. */
private const val MOCK_ADDRESS = "Малая Бронная, 22"

class NewMemoryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NewMemoryUiState(address = MOCK_ADDRESS))
    val uiState: StateFlow<NewMemoryUiState> = _uiState.asStateFlow()

    /**
     * Ставит эмоцию, выбранную в листе на карте.
     *
     * Вызывается при открытии экрана, поэтому уже выбранную эмоцию не трогает:
     * иначе поворот экрана вернул бы ее к исходной.
     */
    fun setInitialEmotion(emotion: Emotion) {
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
}
