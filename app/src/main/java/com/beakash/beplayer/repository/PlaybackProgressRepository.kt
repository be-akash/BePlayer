package com.beakash.beplayer.repository

import com.beakash.beplayer.data.local.playback.PlaybackPositionDao
import com.beakash.beplayer.data.local.playback.PlaybackPositionEntity

class PlaybackProgressRepository(
    private val dao: PlaybackPositionDao
) {

    suspend fun getSavedPosition(mediaKey: String): PlaybackPositionEntity? {
        return dao.getPlaybackPosition(mediaKey)
    }

    suspend fun savePosition(
        mediaKey: String,
        positionMs: Long,
        durationMs: Long
    ) {
        dao.upsertPlaybackPosition(
            PlaybackPositionEntity(
                mediaKey = mediaKey,
                lastPositionMs = positionMs,
                durationMs = durationMs,
                updatedAtEpochMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearPosition(mediaKey: String) {
        dao.deletePlaybackPosition(mediaKey)
    }
}