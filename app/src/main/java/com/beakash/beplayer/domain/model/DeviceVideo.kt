package com.beakash.beplayer.domain.model

import android.net.Uri

data class DeviceVideo(
    val id: Long,
    val title: String,
    val contentUri: Uri,
    val duration: Long,
    val dateAdded: Long
)