package com.soundcraft.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundcraft.audio.AudioEngine
import com.soundcraft.core.StepPattern
import com.soundcraft.core.Tempo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class StudioUiState(
    val bpm: Float = Tempo.DEFAULT_BPM,
    val pattern: StepPattern = StepPattern.fourOnTheFloor(),
    val playing: Boolean = false,
    /** Pas en cours de lecture (0..15), -1 à l'arrêt. */
    val currentStep: Int = -1,
    val engineError: String? = null,
)

class StudioViewModel : ViewModel() {
    private val _state = MutableStateFlow(StudioUiState())
    val state: StateFlow<StudioUiState> = _state.asStateFlow()

    private var pollJob: Job? = null

    init {
        AudioEngine.setBpm(_state.value.bpm)
        AudioEngine.setPattern(_state.value.pattern.toBitmask())
    }

    fun togglePlay() {
        if (_state.value.playing) stopPlayback() else startPlayback()
    }

    fun setBpm(bpm: Float) {
        val clamped = Tempo.clamp(bpm)
        _state.update { it.copy(bpm = clamped) }
        AudioEngine.setBpm(clamped)
    }

    fun toggleStep(index: Int) {
        val updated = _state.value.pattern.toggled(index)
        _state.update { it.copy(pattern = updated) }
        AudioEngine.setPattern(updated.toBitmask())
    }

    fun onBackgrounded() {
        stopPlayback()
        AudioEngine.stop()
    }

    private fun startPlayback() {
        if (!AudioEngine.start()) {
            _state.update { it.copy(engineError = "Impossible d'ouvrir la sortie audio") }
            return
        }
        AudioEngine.setPlaying(true)
        _state.update { it.copy(playing = true, engineError = null) }
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (isActive) {
                val step = AudioEngine.currentStep()
                _state.update { it.copy(currentStep = step) }
                delay(16)
            }
        }
    }

    private fun stopPlayback() {
        pollJob?.cancel()
        pollJob = null
        AudioEngine.setPlaying(false)
        _state.update { it.copy(playing = false, currentStep = -1) }
    }

    override fun onCleared() {
        AudioEngine.stop()
        super.onCleared()
    }
}
