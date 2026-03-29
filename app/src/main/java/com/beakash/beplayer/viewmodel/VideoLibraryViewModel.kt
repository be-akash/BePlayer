package com.beakash.beplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beakash.beplayer.data.media.MediaStoreVideoLoader
import com.beakash.beplayer.domain.model.DeviceVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class VideoLibraryViewModel(
    private val mediaStoreVideoLoader: MediaStoreVideoLoader
) : ViewModel() {

    private val _allVideos = MutableStateFlow<List<DeviceVideo>>(emptyList())
    private val _videos = MutableStateFlow<List<DeviceVideo>>(emptyList())
    val videos: StateFlow<List<DeviceVideo>> = _videos.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DATE_ADDED_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeLibraryVideos()
    }

    private fun observeLibraryVideos() {
        viewModelScope.launch {
            combine(_allVideos, _searchQuery, _sortOption) { allVideos, query, sortOption ->
                val filtered = if (query.isBlank()) {
                    allVideos
                } else {
                    val normalizedQuery = query.trim()
                    allVideos.filter { video ->
                        video.title.contains(normalizedQuery, ignoreCase = true)
                    }
                }

                when (sortOption) {
                    SortOption.DATE_ADDED_DESC -> filtered.sortedByDescending { it.dateAdded }
                    SortOption.TITLE_ASC -> filtered.sortedBy { it.title.lowercase() }
                    SortOption.DURATION_DESC -> filtered.sortedByDescending { it.duration }
                }
            }.collect { result ->
                _videos.value = result
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
    }

    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            runCatching {
                mediaStoreVideoLoader.loadVideos()
            }.onSuccess { loadedVideos ->
                _allVideos.value = loadedVideos
                _error.value = null
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "Failed to load videos"
            }

            _isLoading.value = false
        }
    }
}