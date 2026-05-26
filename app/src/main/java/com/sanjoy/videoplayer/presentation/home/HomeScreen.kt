package com.sanjoy.videoplayer.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.sanjoy.videoplayer.domain.model.VideoFile
import com.sanjoy.videoplayer.domain.model.VideoFolder
import com.sanjoy.videoplayer.util.formatVideoDuration

private val ScreenBackground = Color.Black
private val BottomBarBackground = Color(0xFF121212)
private val FolderIconColor = Color(0xFF4B5560)
private val PrimaryTextColor = Color(0xFFEDEDED)
private val SecondaryTextColor = Color(0xFFB8B8B8)
private val AccentBlue = Color(0xFF1E9BFF)
private val BadgePink = Color(0xFFFF3D6E)

/**
 * Home screen designed like the reference screenshot.
 *
 * Main sections:
 * 1. Header
 * 2. Recent video thumbnails
 * 3. Folder list
 * 4. Bottom navigation
 */
@Composable
fun HomeScreen(
    onVideoClick: (VideoFile) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            HomeBottomNavigation()
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                HomeLoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.errorMessage != null -> {
                HomeErrorContent(
                    message = uiState.errorMessage ?: "Something went wrong",
                    onRetryClick = viewModel::loadVideos,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScreenBackground)
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 32.dp,
                        bottom = 16.dp
                    )
                ) {
                    item {
                        HomeHeader()
                    }

                    item {
                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    item {
                        RecentVideosRow(
                            videos = uiState.recentVideos,
                            onVideoClick = { video ->
                                viewModel.prepareVideoPlayback(video)
                                onVideoClick(video)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    if (uiState.folders.isEmpty()) {
                        item {
                            EmptyFolderContent()
                        }
                    } else {
                        items(
                            items = uiState.folders,
                            key = { folder ->
                                folder.folderPath ?: folder.folderName
                            }
                        ) { folder ->
                            FolderListItem(
                                folder = folder,
                                onClick = {
                                    viewModel.prepareFolderPlayback(folder)
                                    folder.firstVideo?.let { video ->
                                        onVideoClick(video)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Folders",
            color = PrimaryTextColor,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "💋",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.width(22.dp))

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = PrimaryTextColor,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(22.dp))

        Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = "Change layout",
            tint = PrimaryTextColor,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(22.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFBFD6FF)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF6FA8FF))
            )
        }
    }
}

@Composable
private fun RecentVideosRow(
    videos: List<VideoFile>,
    onVideoClick: (VideoFile) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = videos.take(3),
            key = { video -> video.id }
        ) { video ->
            RecentVideoCard(
                video = video,
                onClick = {
                    onVideoClick(video)
                }
            )
        }
    }
}

@Composable
private fun RecentVideoCard(
    video: VideoFile,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.DarkGray)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = video.uri),
            contentDescription = video.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(
                text = formatVideoDuration(video.durationMs),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun FolderListItem(
    folder: VideoFolder,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        FolderIconBox(
            folder = folder
        )

        Spacer(modifier = Modifier.width(22.dp))

        Column {
            Text(
                text = folder.folderName,
                color = PrimaryTextColor,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (folder.videoCount == 1) {
                    "1 video"
                } else {
                    "${folder.videoCount} videos"
                },
                color = SecondaryTextColor,
                style = MaterialTheme.typography.titleMedium
            )

            /**
             * Small ad-like subtitle only for visual matching.
             * Remove this later for real production UI.
             */
            if (folder.folderName.contains("VID", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(AccentBlue)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Ad",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "New Show",
                        color = SecondaryTextColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun FolderIconBox(
    folder: VideoFolder
) {
    Box(
        modifier = Modifier.size(width = 105.dp, height = 76.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = FolderIconColor,
            modifier = Modifier.fillMaxSize()
        )

        if (folder.folderName.contains("camera", ignoreCase = true)) {
            Icon(
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.16f),
                modifier = Modifier.size(34.dp)
            )
        }

        /**
         * Example badge like screenshot.
         * Later we can replace this with real "new videos count".
         */
        if (folder.videoCount >= 20) {
            Badge(
                containerColor = BadgePink,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 2.dp)
            ) {
                Text(
                    text = folder.videoCount.coerceAtMost(99).toString()
                )
            }
        }
    }
}

@Composable
private fun HomeBottomNavigation() {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val items = listOf(
        BottomNavItem(
            title = "Local",
            icon = Icons.Default.Folder
        ),
        BottomNavItem(
            title = "Video",
            icon = Icons.Default.PlayCircle
        ),
        BottomNavItem(
            title = "Fatafat",
            icon = Icons.Default.VideoLibrary
        ),
        BottomNavItem(
            title = "Games",
            icon = Icons.Default.SportsEsports
        ),
        BottomNavItem(
            title = "Crazy Deals",
            icon = Icons.Default.ShoppingBag
        )
    )

    NavigationBar(
        containerColor = BottomBarBackground,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBlue,
                    selectedTextColor = AccentBlue,
                    unselectedIconColor = Color(0xFF8C8C8C),
                    unselectedTextColor = Color(0xFF8C8C8C),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
private fun HomeLoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(ScreenBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = AccentBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Loading videos...",
            color = PrimaryTextColor
        )
    }
}

@Composable
private fun HomeErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(ScreenBackground)
            .padding(24.dp)
            .clickable { onRetryClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap to retry",
            color = SecondaryTextColor
        )
    }
}

@Composable
private fun EmptyFolderContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No folders found",
            color = PrimaryTextColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Allow permission and add videos to your device.",
            color = SecondaryTextColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}