package com.sanjoy.videoplayer.data.local

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.sanjoy.videoplayer.domain.model.VideoFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Reads video files from Android MediaStore.
 *
 * MediaStore is Android's indexed database for media files like videos,
 * images, and audio.
 */
class MediaStoreVideoDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Loads all videos from device storage.
     *
     * This function runs on Dispatchers.IO because MediaStore query is disk I/O.
     */
    suspend fun loadVideos(): List<VideoFile> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<VideoFile>()

        val videoCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            videoCollection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val folderColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
            val dateAddedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
            val widthColumn = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val name = cursor.getString(nameColumn).orEmpty()

                val path = if (pathColumn != -1) {
                    cursor.getString(pathColumn)
                } else {
                    null
                }

                val folderName = if (folderColumn != -1) {
                    cursor.getString(folderColumn).orEmpty()
                } else {
                    getFolderNameFromPath(path)
                }

                val duration = if (durationColumn != -1) {
                    cursor.getLong(durationColumn)
                } else {
                    0L
                }

                val size = if (sizeColumn != -1) {
                    cursor.getLong(sizeColumn)
                } else {
                    0L
                }

                val dateAdded = if (dateAddedColumn != -1) {
                    cursor.getLong(dateAddedColumn)
                } else {
                    0L
                }

                val width = if (widthColumn != -1) {
                    cursor.getInt(widthColumn)
                } else {
                    null
                }

                val height = if (heightColumn != -1) {
                    cursor.getInt(heightColumn)
                } else {
                    null
                }

                val videoUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                videos.add(
                    VideoFile(
                        id = id,
                        name = name,
                        uri = videoUri,
                        path = path,
                        folderName = folderName.ifBlank { "Unknown" },
                        folderPath = getFolderPathFromPath(path),
                        durationMs = duration,
                        sizeBytes = size,
                        dateAddedSeconds = dateAdded,
                        width = width,
                        height = height
                    )
                )
            }
        }

        videos
    }

    /**
     * Extracts folder path from full file path.
     *
     * Example:
     * /storage/emulated/0/Movies/video.mp4
     * becomes:
     * /storage/emulated/0/Movies
     */
    private fun getFolderPathFromPath(path: String?): String? {
        if (path.isNullOrBlank()) return null

        return path.substringBeforeLast(
            delimiter = "/",
            missingDelimiterValue = ""
        )
    }

    /**
     * Extracts folder name from full file path.
     *
     * Example:
     * /storage/emulated/0/Movies/video.mp4
     * becomes:
     * Movies
     */
    private fun getFolderNameFromPath(path: String?): String {
        val folderPath = getFolderPathFromPath(path)
        return folderPath?.substringAfterLast("/") ?: "Unknown"
    }
}