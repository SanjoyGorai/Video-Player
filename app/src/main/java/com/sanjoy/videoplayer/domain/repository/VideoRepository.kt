package com.sanjoy.videoplayer.domain.repository

import com.sanjoy.videoplayer.domain.model.VideoFile
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for video data.
 *
 * ViewModels and use cases depend on this interface,
 * not directly on MediaStore.
 */
interface VideoRepository {

    /**
     * Returns all videos from the device.
     */
    fun getAllVideos(): Flow<List<VideoFile>>
}