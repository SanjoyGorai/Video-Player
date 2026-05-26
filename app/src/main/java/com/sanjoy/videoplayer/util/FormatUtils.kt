package com.sanjoy.videoplayer.util

import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

/**
 * Converts video duration from milliseconds to readable format.
 *
 * Example:
 * 65000 ms -> 01:05
 * 3670000 ms -> 01:01:10
 */
fun formatVideoDuration(durationMs: Long): String {
    if (durationMs <= 0L) return "00:00"

    val totalSeconds = durationMs / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        )
    } else {
        String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
    }
}

/**
 * Converts bytes to readable file size.
 *
 * Example:
 * 1048576 -> 1.0 MB
 */
fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "0 B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")

    val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()

    return String.format(
        Locale.getDefault(),
        "%.1f %s",
        bytes / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}