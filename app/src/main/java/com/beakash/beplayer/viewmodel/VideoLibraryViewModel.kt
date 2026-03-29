package com.beakash.beplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beakash.beplayer.data.media.MediaStoreVideoLoader
import com.beakash.beplayer.domain.model.DeviceVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoLibraryViewModel(
    private val mediaStoreVideoLoader: MediaStoreVideoLoader
) : ViewModel() {

    private val _videos = MutableStateFlow<List<DeviceVideo>>(emptyList())
    val videos: StateFlow<List<DeviceVideo>> = _videos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _error.value = null
                _videos.value = mediaStoreVideoLoader.loadVideos()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load videos"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class VideoLibraryViewModelFactory(
    private val mediaStoreVideoLoader: MediaStoreVideoLoader
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoLibraryViewModel::class.java)) {
            return VideoLibraryViewModel(mediaStoreVideoLoader) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}