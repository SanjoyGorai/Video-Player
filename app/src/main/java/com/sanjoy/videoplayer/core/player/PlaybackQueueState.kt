package com.sanjoy.videoplayer.core.player

import com.sanjoy.videoplayer.domain.model.VideoFile

/**
 * Holds the current playback queue state.
 *
 * queue:
 * Full list of videos that can be played one after another.
 *
 * selectedIndex:
 * The index of the video that user clicked.
 */
data class PlaybackQueueState(
    val queue: List<VideoFile> = emptyList(),
    val selectedIndex: Int = 0
)