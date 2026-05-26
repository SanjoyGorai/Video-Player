package com.sanjoy.videoplayer.domain.model

import android.net.Uri

/**
 * Clean video model used by the app.
 *
 * This model is independent from MediaStore cursor logic.
 */
data class VideoFile(
    val id: Long,
    val name: String,
    val uri: Uri,
    val path: String?,
    val folderName: String,
    val folderPath: String?,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAddedSeconds: Long,
    val width: Int?,
    val height: Int?
)