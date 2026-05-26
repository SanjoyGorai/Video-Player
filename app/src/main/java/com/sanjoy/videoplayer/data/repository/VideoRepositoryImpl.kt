package com.sanjoy.videoplayer.data.repository

import com.sanjoy.videoplayer.data.local.MediaStoreVideoDataSource
import com.sanjoy.videoplayer.domain.model.VideoFile
import com.sanjoy.videoplayer.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Concrete implementation of VideoRepository.
 */
class VideoRepositoryImpl @Inject constructor(
    private val mediaStoreVideoDataSource: MediaStoreVideoDataSource
) : VideoRepository {

    override fun getAllVideos(): Flow<List<VideoFile>> = flow {
        val videos = mediaStoreVideoDataSource.loadVideos()
        emit(videos)
    }
}