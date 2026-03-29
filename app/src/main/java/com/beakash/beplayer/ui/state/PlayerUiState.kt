package com.beakash.beplayer.ui.state

import android.net.Uri

data class PlayerUiState(
    val selectedVideoUri: Uri? = null,
    val mediaKey: String? = null,
    val resumePositionMs: Long = 0L,
    val showResumePrompt: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val errorMessage: String? = null
)