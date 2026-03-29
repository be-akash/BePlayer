package com.beakash.beplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beakash.beplayer.data.media.MediaStoreVideoLoader

class VideoLibraryViewModelFactory(
    private val mediaStoreVideoLoader: MediaStoreVideoLoader
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoLibraryViewModel::class.java)) {
            return VideoLibraryViewModel(mediaStoreVideoLoader) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}