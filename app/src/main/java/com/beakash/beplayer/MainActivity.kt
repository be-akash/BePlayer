package com.beakash.beplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beakash.beplayer.data.media.MediaStoreVideoLoader
import com.beakash.beplayer.player.PlayerManager
import com.beakash.beplayer.ui.screen.PermissionScreen
import com.beakash.beplayer.ui.screen.PlayerScreen
import com.beakash.beplayer.ui.screen.VideoLibraryScreen
import com.beakash.beplayer.ui.theme.BePlayerTheme
import com.beakash.beplayer.viewmodel.PlayerViewModel
import com.beakash.beplayer.viewmodel.VideoLibraryViewModel
import com.beakash.beplayer.viewmodel.VideoLibraryViewModelFactory

class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = VideoLibraryViewModelFactory(
            MediaStoreVideoLoader(applicationContext)
        )

        setContent {
            BePlayerTheme {
                val playerManager = remember { PlayerManager(this) }
                val selectedVideoUri by playerViewModel.selectedVideoUri.collectAsState()
                val errorMessage by playerViewModel.errorMessage.collectAsState()

                val videoLibraryViewModel: VideoLibraryViewModel = viewModel(factory = factory)
                val videos by videoLibraryViewModel.videos.collectAsState()
                val isLoading by videoLibraryViewModel.isLoading.collectAsState()
                val libraryError by videoLibraryViewModel.error.collectAsState()
                val searchQuery by videoLibraryViewModel.searchQuery.collectAsState()
                val sortOption by videoLibraryViewModel.sortOption.collectAsState()

                var hasVideoPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_MEDIA_VIDEO
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    hasVideoPermission = granted
                    if (granted) {
                        videoLibraryViewModel.loadVideos()
                    }
                }

                val pickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri ->
                    uri?.let {
                        contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        playerViewModel.setSelectedVideo(it)
                    }
                }

                DisposableEffect(Unit) {
                    if (hasVideoPermission) {
                        videoLibraryViewModel.loadVideos()
                    }
                    onDispose {
                        playerManager.release()
                    }
                }

                when {
                    selectedVideoUri != null -> {
                        PlayerScreen(
                            videoUri = selectedVideoUri!!,
                            playerManager = playerManager,
                            onPickAnotherVideo = {
                                playerViewModel.clearSelectedVideo()
                            },
                            onPlaybackError = { message ->
                                playerViewModel.setError(message)
                                playerViewModel.clearSelectedVideo()
                            },
                            modifier = Modifier
                        )
                    }

                    !hasVideoPermission -> {
                        PermissionScreen(
                            onGrantPermission = {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                            },
                            onPickWithDocumentPicker = {
                                pickerLauncher.launch(arrayOf("video/*"))
                            },
                            errorMessage = errorMessage
                        )
                    }

                    else -> {
                        VideoLibraryScreen(
                            videos = videos,
                            isLoading = isLoading,
                            error = libraryError,
                            searchQuery = searchQuery,
                            onSearchQueryChange = videoLibraryViewModel::updateSearchQuery,
                            sortOption = sortOption,
                            onSortOptionChange = videoLibraryViewModel::updateSortOption,
                            onVideoClick = { video ->
                                playerViewModel.setSelectedVideo(video.contentUri)
                            }
                        )
                    }
                }
            }
        }
    }
}