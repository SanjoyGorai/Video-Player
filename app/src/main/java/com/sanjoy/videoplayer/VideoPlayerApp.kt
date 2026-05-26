package com.sanjoy.videoplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class required by Hilt.
 *
 * Hilt uses this class to generate and attach the app-level dependency graph.
 */
@HiltAndroidApp
class VideoPlayerApp : Application()