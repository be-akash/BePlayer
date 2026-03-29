package com.beakash.beplayer.data.local.playback

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaybackPositionDao {

    @Query("SELECT * FROM playback_positions WHERE mediaKey = :mediaKey LIMIT 1")
    suspend fun getPlaybackPosition(mediaKey: String): PlaybackPositionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlaybackPosition(entity: PlaybackPositionEntity)

    @Query("DELETE FROM playback_positions WHERE mediaKey = :mediaKey")
    suspend fun deletePlaybackPosition(mediaKey: String)
}