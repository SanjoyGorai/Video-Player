package com.sanjoy.videoplayer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjoy.videoplayer.core.player.PlaybackQueueManager
import com.sanjoy.videoplayer.domain.model.VideoFile
import com.sanjoy.videoplayer.domain.model.VideoFolder
import com.sanjoy.videoplayer.domain.usecase.GetAllVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for HomeScreen.
 *
 * It loads all videos, prepares folder groups, recent videos,
 * and creates playback queue when user taps a video/folder.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllVideosUseCase: GetAllVideosUseCase,
    private val playbackQueueManager: PlaybackQueueManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    /**
     * Loads videos from MediaStore and converts them into:
     * 1. Full video list
     * 2. Folder list
     * 3. Recent video preview list
     */
    fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                getAllVideosUseCase().collect { videos ->
                    val folders = videos
                        .groupBy { video ->
                            video.folderPath ?: video.folderName
                        }
                        .map { entry ->
                            val folderVideos = entry.value

                            VideoFolder(
                                folderName = folderVideos.firstOrNull()?.folderName ?: "Unknown",
                                folderPath = folderVideos.firstOrNull()?.folderPath,
                                videos = folderVideos
                            )
                        }
                        .sortedBy { folder ->
                            folder.folderName.lowercase()
                        }

                    val recentVideos = videos
                        .sortedByDescending { video ->
                            video.dateAddedSeconds
                        }
                        .take(10)

                    _uiState.value = HomeUiState(
                        isLoading = false,
                        videos = videos,
                        folders = folders,
                        recentVideos = recentVideos,
                        errorMessage = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to load videos"
                )
            }
        }
    }

    /**
     * Plays all videos from Home screen queue.
     */
    fun prepareVideoPlayback(video: VideoFile) {
        playbackQueueManager.prepareQueue(
            videos = _uiState.value.videos,
            selectedVideo = video
        )
    }

    /**
     * Plays videos from a selected folder.
     */
    fun prepareFolderPlayback(folder: VideoFolder) {
        val firstVideo = folder.firstVideo ?: return

        playbackQueueManager.prepareQueue(
            videos = folder.videos,
            selectedVideo = firstVideo
        )
    }
}