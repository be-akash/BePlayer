package com.beakash.beplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.beakash.beplayer.data.local.playback.PlaybackPositionDao
import com.beakash.beplayer.data.local.playback.PlaybackPositionEntity

@Database(
    entities = [PlaybackPositionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BePlayerDatabase : RoomDatabase() {
    abstract fun playbackPositionDao(): PlaybackPositionDao
}