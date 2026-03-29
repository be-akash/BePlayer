package com.beakash.beplayer.ui.screen

import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.beakash.beplayer.player.PlayerManager
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun PlayerScreen(
    videoUri: Uri,
    playerManager: PlayerManager,
    resumePositionMs: Long,
    showResumePrompt: Boolean,
    playbackSpeed: Float,
    onSetPlaybackSpeed: (Float) -> Unit,
    onResumeConfirmed: () -> Unit,
    onStartOver: () -> Unit,
    onSavePlaybackProgress: (positionMs: Long, durationMs: Long) -> Unit,
    onPickAnotherVideo: () -> Unit,
    onPlaybackError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val exoPlayer = remember { playerManager.exoPlayer }
    val latestPlaybackSpeed by rememberUpdatedState(playbackSpeed)

    var isPlayerReady by remember(videoUri) { mutableStateOf(false) }
    var hasAppliedInitialPosition by remember(videoUri) { mutableStateOf(false) }
    var actualPlaybackSpeed by remember(videoUri) { mutableFloatStateOf(1.0f) }
    var showSpeedMenu by remember { mutableStateOf(false) }

    val speedOptions = listOf(0.25f, 0.5f, 1f, 1.5f, 2f, 3f, 5f, 7f, 8f, 10f)

    DisposableEffect(videoUri) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isPlayerReady = playbackState == Player.STATE_READY

                if (playbackState == Player.STATE_READY) {
                    playerManager.setPlaybackSpeed(latestPlaybackSpeed)
                    actualPlaybackSpeed = exoPlayer.playbackParameters.speed
                }
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                actualPlaybackSpeed = playbackParameters.speed
            }
        }

        exoPlayer.addListener(listener)

        playerManager.playVideo(
            uri = videoUri,
            onError = onPlaybackError
        )

        onDispose {
            onSavePlaybackProgress(
                exoPlayer.currentPosition,
                exoPlayer.duration.coerceAtLeast(0L)
            )
            exoPlayer.removeListener(listener)
            playerManager.pause()
        }
    }

    LaunchedEffect(isPlayerReady, playbackSpeed) {
        if (isPlayerReady) {
            playerManager.setPlaybackSpeed(playbackSpeed)
            actualPlaybackSpeed = exoPlayer.playbackParameters.speed
        }
    }

    LaunchedEffect(videoUri, isPlayerReady, showResumePrompt, resumePositionMs) {
        if (!isPlayerReady || showResumePrompt || hasAppliedInitialPosition) return@LaunchedEffect

        if (resumePositionMs > 0L) {
            exoPlayer.seekTo(resumePositionMs)
        } else {
            exoPlayer.seekTo(0L)
        }

        hasAppliedInitialPosition = true
    }

    LaunchedEffect(videoUri, isPlayerReady, showResumePrompt, hasAppliedInitialPosition) {
        if (!isPlayerReady || showResumePrompt || !hasAppliedInitialPosition) return@LaunchedEffect

        while (true) {
            if (exoPlayer.isPlaying) {
                onSavePlaybackProgress(
                    exoPlayer.currentPosition,
                    exoPlayer.duration.coerceAtLeast(0L)
                )
            }

            actualPlaybackSpeed = exoPlayer.playbackParameters.speed
            delay(2000L)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT
                    )
                    player = exoPlayer
                    useController = true
                }
            }
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onPickAnotherVideo) {
                Text("Back")
            }

            Box {
                Button(
                    onClick = { showSpeedMenu = true }
                ) {
                    Text(formatSpeed(actualPlaybackSpeed))
                }

                DropdownMenu(
                    expanded = showSpeedMenu,
                    onDismissRequest = { showSpeedMenu = false }
                ) {
                    speedOptions.forEach { speed ->
                        DropdownMenuItem(
                            text = {
                                Text(formatSpeed(speed) + if (playbackSpeed == speed) " ✓" else "")
                            },
                            onClick = {
                                onSetPlaybackSpeed(speed)
                                playerManager.setPlaybackSpeed(speed)
                                actualPlaybackSpeed = exoPlayer.playbackParameters.speed
                                showSpeedMenu = false
                            }
                        )
                    }
                }
            }
        }

        if (showResumePrompt) {
            AlertDialog(
                onDismissRequest = onStartOver,
                title = {
                    Text("Resume from ${formatPlaybackTime(resumePositionMs)}?")
                },
                text = {
                    Text("Do you want to continue from where you left off?")
                },
                confirmButton = {
                    TextButton(onClick = onResumeConfirmed) {
                        Text("Resume")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onStartOver) {
                        Text("Start Over")
                    }
                }
            )
        }
    }
}

private fun formatPlaybackTime(positionMs: Long): String {
    val totalSeconds = (positionMs / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

private fun formatSpeed(speed: Float): String {
    return if (speed % 1f == 0f) {
        "${speed.toInt()}x"
    } else {
        "${speed}x"
    }
}