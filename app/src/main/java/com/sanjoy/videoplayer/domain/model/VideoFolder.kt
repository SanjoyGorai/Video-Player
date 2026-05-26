package com.sanjoy.videoplayer.domain.model

/**
 * Represents a folder that contains videos.
 *
 * Example:
 * folderName = "Download"
 * videoCount = 20
 * videos = all videos inside Download folder
 */
data class VideoFolder(
    val folderName: String,
    val folderPath: String?,
    val videos: List<VideoFile>
) {
    val videoCount: Int
        get() = videos.size

    val firstVideo: VideoFile?
        get() = videos.firstOrNull()
}