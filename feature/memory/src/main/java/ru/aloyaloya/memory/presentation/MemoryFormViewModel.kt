package ru.aloyaloya.memory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.aloyaloya.domain.model.Emotion
import ru.aloyaloya.domain.model.Memory
import ru.aloyaloya.domain.model.MemoryMedia
import ru.aloyaloya.domain.repository.AddressRepository
import ru.aloyaloya.domain.repository.MemoryRepository
import ru.aloyaloya.mapkit.model.MapPoint
import ru.aloyaloya.memory.model.MemoryFormArgs
import ru.aloyaloya.memory.model.MemoryFormSheet
import ru.aloyaloya.memory.model.MemoryFormUiState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class MemoryFormViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryFormUiState(happenedAt = LocalDateTime.now()))
    val uiState: StateFlow<MemoryFormUiState> = _uiState.asStateFlow()

    private var args: MemoryFormArgs? = null

    private var edited: Memory? = null

    init {
        observeEmotionByDate()
    }

    fun setArgs(args: MemoryFormArgs) {
        if (this.args != null) return
        this.args = args

        when (args) {
            is MemoryFormArgs.New -> startNew(args)
            is MemoryFormArgs.Edit -> startEdit(args)
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

    fun onMediaPicked(picked: List<MemoryMedia>) {
        if (picked.isEmpty()) return

        _uiState.update { it.copy(media = it.media + picked) }
    }

    fun onMediaRemove(index: Int) {
        _uiState.update { state ->
            if (index !in state.media.indices) state
            else state.copy(media = state.media.filterIndexed { i, _ -> i != index })
        }
    }

    fun onDateFieldClick() {
        _uiState.update { it.copy(activeSheet = MemoryFormSheet.DATE) }
    }

    fun onTimeFieldClick() {
        _uiState.update { it.copy(activeSheet = MemoryFormSheet.TIME) }
    }

    fun onSheetDismiss() {
        _uiState.update { it.copy(activeSheet = null) }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(happenedAt = it.happenedAt.with(date), activeSheet = null) }
    }

    fun onTimeSelected(time: LocalTime) {
        _uiState.update { it.copy(happenedAt = it.happenedAt.with(time), activeSheet = null) }
    }

    fun onSave() {
        val state = _uiState.value
        val emotion = state.emotion
        val point = state.point
        if (!state.saveEnabled || emotion == null || point == null) return

        _uiState.update { it.copy(saving = true) }

        viewModelScope.launch {
            val edited = edited

            val memory = edited?.copy(
                title = state.title.trim(),
                description = state.description.trim(),
                emotion = emotion,
                happenedAt = state.happenedAt.toEpochMilli(),
                media = state.media
            ) ?: Memory(
                title = state.title.trim(),
                description = state.description.trim(),
                latitude = point.latitude,
                longitude = point.longitude,
                emotion = emotion,
                createdAt = System.currentTimeMillis(),
                happenedAt = state.happenedAt.toEpochMilli(),
                media = state.media
            )

            if (edited == null) memoryRepository.create(memory) else memoryRepository.update(memory)

            _uiState.update { it.copy(saving = false, saved = true) }
        }
    }

    private fun observeEmotionByDate() {
        viewModelScope.launch {
            memoryRepository.observeAll().collect { memories ->
                val emotionByDate = memories
                    .groupBy { memory -> memory.happenedAt.toLocalDateTime().toLocalDate() }
                    .mapValues { (_, dayMemories) ->
                        dayMemories.maxBy(Memory::happenedAt).emotion
                    }

                _uiState.update { it.copy(emotionByDate = emotionByDate) }
            }
        }
    }

    private fun startNew(args: MemoryFormArgs.New) {
        val point = MapPoint(args.latitude, args.longitude)
        _uiState.update { it.copy(emotion = args.emotion, point = point) }
        resolveAddress(point)
    }

    private fun startEdit(args: MemoryFormArgs.Edit) {
        _uiState.update { it.copy(editing = true) }

        viewModelScope.launch {
            val memory = memoryRepository.observeById(args.memoryId).first() ?: return@launch
            edited = memory

            val point = MapPoint(memory.latitude, memory.longitude)

            _uiState.update {
                it.copy(
                    happenedAt = memory.happenedAt.toLocalDateTime(),
                    emotion = memory.emotion,
                    title = memory.title,
                    description = memory.description,
                    point = point,
                    media = memory.media
                )
            }

            resolveAddress(point)
        }
    }

    private fun resolveAddress(point: MapPoint) {
        viewModelScope.launch {
            val address = addressRepository.resolve(
                latitude = point.latitude,
                longitude = point.longitude
            )
            _uiState.update { it.copy(address = address) }
        }
    }
}

private fun LocalDateTime.toEpochMilli(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

private fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
