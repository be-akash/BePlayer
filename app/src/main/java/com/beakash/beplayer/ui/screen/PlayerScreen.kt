package com.beakash.beplayer.ui.screen
import androidx.compose.foundation.layout.statusBarsPadding
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.beakash.beplayer.player.PlayerManager

@Composable
fun PlayerScreen(
    videoUri: Uri,
    playerManager: PlayerManager,
    onPickAnotherVideo: () -> Unit,
    onPlaybackError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val exoPlayer = remember { playerManager.exoPlayer }

    DisposableEffect(videoUri) {
        playerManager.playVideo(
            uri = videoUri,
            onError = onPlaybackError
        )

        onDispose {
            playerManager.pause()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    player = exoPlayer
                    useController = true
                }
            }
        )

        Button(
            onClick = onPickAnotherVideo,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text("Pick Another Video")
        }
    }
}