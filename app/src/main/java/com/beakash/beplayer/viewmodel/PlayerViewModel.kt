package com.beakash.beplayer.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beakash.beplayer.repository.PlaybackProgressRepository
import com.beakash.beplayer.ui.state.PlayerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val repository: PlaybackProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun openVideo(uri: Uri) {
        val mediaKey = uri.toString()
        val currentSpeed = _uiState.value.playbackSpeed

        viewModelScope.launch {
            val saved = repository.getSavedPosition(mediaKey)

            val shouldResume = saved != null &&
                    saved.lastPositionMs > 10_000L &&
                    !isNearCompletion(saved.lastPositionMs, saved.durationMs)

            _uiState.value = PlayerUiState(
                selectedVideoUri = uri,
                mediaKey = mediaKey,
                resumePositionMs = saved?.lastPositionMs ?: 0L,
                showResumePrompt = shouldResume,
                playbackSpeed = currentSpeed,
                errorMessage = null
            )
        }
    }

    fun onResumeConfirmed() {
        _uiState.value = _uiState.value.copy(showResumePrompt = false)
    }

    fun onStartOver() {
        val mediaKey = _uiState.value.mediaKey

        _uiState.value = _uiState.value.copy(
            resumePositionMs = 0L,
            showResumePrompt = false
        )

        if (mediaKey != null) {
            viewModelScope.launch {
                repository.clearPosition(mediaKey)
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(playbackSpeed = speed)
    }

    fun saveProgress(positionMs: Long, durationMs: Long) {
        val mediaKey = _uiState.value.mediaKey ?: return

        viewModelScope.launch {
            if (durationMs > 0 && isNearCompletion(positionMs, durationMs)) {
                repository.clearPosition(mediaKey)
            } else {
                repository.savePosition(mediaKey, positionMs, durationMs)
            }
        }
    }

    private fun isNearCompletion(position: Long, duration: Long): Boolean {
        return duration > 0 && (duration - position <= 15_000L)
    }

    fun clearVideo() {
        _uiState.value = PlayerUiState(
            playbackSpeed = _uiState.value.playbackSpeed
        )
    }
}

class PlayerViewModelFactory(
    private val repository: PlaybackProgressRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}