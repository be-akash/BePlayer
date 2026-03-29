package com.beakash.beplayer.data.local.playback

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_positions")
data class PlaybackPositionEntity(
    @PrimaryKey
    val mediaKey: String,
    val lastPositionMs: Long,
    val durationMs: Long,
    val updatedAtEpochMs: Long
)