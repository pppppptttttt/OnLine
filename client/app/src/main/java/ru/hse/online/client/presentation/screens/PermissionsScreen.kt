package ru.hse.online.client.presentation.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.hse.online.client.presentation.NavigationComponent

@Composable
fun LocationPermissionRequest(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    var showRationale by remember { mutableStateOf(false) }

    val locationPermissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        add(Manifest.permission.ACTIVITY_RECOGNITION)
    }.toTypedArray()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.all { it.value }
            val permanentDenial = permissions.any {
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, it.key) && !it.value
            }

            when {
                allGranted -> onPermissionsGranted()
                permanentDenial -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                    onPermissionsDenied()
                }
                else -> onPermissionsDenied()
            }
        }
    )

    LaunchedEffect(Unit) {
        val hasPermissions = locationPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (hasPermissions) {
            onPermissionsGranted()
        } else {
            val shouldShowRationale = locationPermissions.any {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            }

            if (shouldShowRationale) {
                showRationale = true
            } else {
                permissionLauncher.launch(locationPermissions)
            }
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Permission Required") },
            text = {
                Text( "Please let us track your geolocation\n" +
                        "It definitely won't fall into the hands of the government ^_^"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRationale = false
                        permissionLauncher.launch(locationPermissions)
                    }
                ) {
                    Text("Grant Permissions")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                        onPermissionsDenied()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PermissionScreen() {
    var permissionsGranted by remember { mutableStateOf(false) }

    LocationPermissionRequest(
        onPermissionsGranted = { permissionsGranted = true },
        onPermissionsDenied = {  }
    )
    Log.e("TAG", permissionsGranted.toString())
    if (permissionsGranted) {
        NavigationComponent()
    } else {
        PermissionRequestPlaceholder()
    }
}

@Composable
fun PermissionRequestPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Waiting for location permissions...")
        }
    }
}
