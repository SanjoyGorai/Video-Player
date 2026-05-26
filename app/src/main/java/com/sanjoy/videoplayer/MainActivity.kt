package com.sanjoy.videoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sanjoy.videoplayer.presentation.navigation.VideoPlayerNavHost
import com.sanjoy.videoplayer.presentation.theme.VideoPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VideoPlayerTheme {
                VideoPlayerNavHost()
            }
        }
    }
}