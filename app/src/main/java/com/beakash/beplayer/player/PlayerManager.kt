package com.beakash.beplayer.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer

class PlayerManager(context: Context) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private var currentPlaybackSpeed: Float = 1.0f

    fun playVideo(
        uri: Uri,
        onError: (String) -> Unit
    ) {
        try {
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            applyPlaybackSpeed()
        } catch (e: Exception) {
            onError(e.message ?: "Unknown playback error")
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        currentPlaybackSpeed = speed
        applyPlaybackSpeed()
    }

    fun getPlaybackSpeed(): Float {
        return currentPlaybackSpeed
    }

    fun reapplyPlaybackSpeed() {
        applyPlaybackSpeed()
    }

    private fun applyPlaybackSpeed() {
        exoPlayer.playbackParameters = PlaybackParameters(currentPlaybackSpeed, 1.0f)
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun release() {
        exoPlayer.release()
    }


}

