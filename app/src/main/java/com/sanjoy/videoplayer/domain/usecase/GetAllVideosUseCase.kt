package com.sanjoy.videoplayer.domain.usecase

import com.sanjoy.videoplayer.domain.model.VideoFile
import com.sanjoy.videoplayer.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for loading all videos.
 *
 * Use cases keep business rules separate from ViewModels.
 */
class GetAllVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<List<VideoFile>> {
        return videoRepository.getAllVideos()
    }
}