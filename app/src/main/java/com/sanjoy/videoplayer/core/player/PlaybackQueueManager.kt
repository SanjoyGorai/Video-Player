package com.sanjoy.videoplayer.core.player

import com.sanjoy.videoplayer.domain.model.VideoFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the current video playback queue.
 *
 * Why this class is needed:
 *
 * HomeScreen knows which video was clicked.
 * PlayerScreen needs to know which video to play.
 *
 * Since these are different screens, we store the selected queue here.
 */
@Singleton
class PlaybackQueueManager @Inject constructor() {

    private val _queueState = MutableStateFlow(PlaybackQueueState())

    /**
     * Public read-only queue state.
     *
     * Other classes can read the queue,
     * but only this manager can modify it.
     */
    val queueState: StateFlow<PlaybackQueueState> = _queueState.asStateFlow()

    /**
     * Prepares the playback queue when user clicks a video.
     *
     * Example:
     * videos = all videos in folder
     * selectedVideo = video user clicked
     */
    fun prepareQueue(
        videos: List<VideoFile>,
        selectedVideo: VideoFile
    ) {
        val selectedIndex = videos.indexOfFirst { video ->
            video.id == selectedVideo.id
        }.coerceAtLeast(0)

        _queueState.value = PlaybackQueueState(
            queue = videos,
            selectedIndex = selectedIndex
        )
    }

    /**
     * Returns current queue immediately.
     *
     * Useful for PlayerViewModel when starting playback.
     */
    fun getCurrentQueueState(): PlaybackQueueState {
        return _queueState.value
    }

    /**
     * Clears the queue.
     *
     * We may use this later when closing the player fully.
     */
    fun clearQueue() {
        _queueState.value = PlaybackQueueState()
    }
}