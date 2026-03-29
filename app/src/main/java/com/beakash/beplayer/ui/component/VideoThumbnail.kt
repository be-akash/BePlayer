package com.beakash.beplayer.ui.component

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private sealed interface ThumbnailUiState {
    data object Loading : ThumbnailUiState
    data class Success(val bitmap: Bitmap) : ThumbnailUiState
    data object Error : ThumbnailUiState
}

@Composable
fun VideoThumbnail(
    videoUri: Uri,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var state by remember(videoUri) {
        mutableStateOf<ThumbnailUiState>(ThumbnailUiState.Loading)
    }

    LaunchedEffect(videoUri) {
        state = ThumbnailUiState.Loading

        val bitmap = loadVideoThumbnail(
            contentResolver = context.contentResolver,
            videoUri = videoUri
        )

        state = if (bitmap != null) {
            ThumbnailUiState.Success(bitmap)
        } else {
            ThumbnailUiState.Error
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            ThumbnailUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }

            is ThumbnailUiState.Success -> {
                Image(
                    bitmap = currentState.bitmap.asImageBitmap(),
                    contentDescription = "Video thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            ThumbnailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoFile,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

private suspend fun loadVideoThumbnail(
    contentResolver: ContentResolver,
    videoUri: Uri
): Bitmap? = withContext(Dispatchers.IO) {
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.loadThumbnail(videoUri, Size(320, 180), null)
        } else {
            null
        }
    }.getOrNull()
}