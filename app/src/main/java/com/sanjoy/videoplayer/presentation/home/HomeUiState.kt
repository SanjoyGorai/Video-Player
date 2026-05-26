package com.sanjoy.videoplayer.presentation.home

import com.sanjoy.videoplayer.domain.model.VideoFile
import com.sanjoy.videoplayer.domain.model.VideoFolder

/**
 * Complete state required by HomeScreen.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val videos: List<VideoFile> = emptyList(),
    val folders: List<VideoFolder> = emptyList(),
    val recentVideos: List<VideoFile> = emptyList(),
    val errorMessage: String? = null
)