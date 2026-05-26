package com.sanjoy.videoplayer.core.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

/**
 * Handles runtime video permission.
 *
 * Android 13+ uses READ_MEDIA_VIDEO.
 * Android 12 and below use READ_EXTERNAL_STORAGE.
 */
@Composable
fun VideoPermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(hasVideoPermission(context))
    }

    val permission = getRequiredVideoPermission()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            onPermissionGranted()
        }
    }

    if (!hasPermission) {
        VideoPermissionScreen(
            onAllowClick = {
                if (permission != null) {
                    permissionLauncher.launch(permission)
                } else {
                    hasPermission = true
                }
            },
            onOpenSettingsClick = {
                openAppSettings(context)
            }
        )
    }
}

/**
 * Returns the required permission based on Android version.
 */
private fun getRequiredVideoPermission(): String? {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            Manifest.permission.READ_MEDIA_VIDEO
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        else -> null
    }
}

/**
 * Checks whether the app currently has video read permission.
 */
private fun hasVideoPermission(context: Context): Boolean {
    val permission = getRequiredVideoPermission() ?: return true

    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Opens this app's settings page.
 *
 * Useful when the user denies permission multiple times.
 */
private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    context.startActivity(intent)
}

@Composable
private fun VideoPermissionScreen(
    onAllowClick: () -> Unit,
    onOpenSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Storage Permission Required",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Allow video permission so the app can find and show videos from your device.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp)
        )

        Button(
            onClick = onAllowClick,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(text = "Allow Permission")
        }

        OutlinedButton(
            onClick = onOpenSettingsClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Open Settings")
        }
    }
}