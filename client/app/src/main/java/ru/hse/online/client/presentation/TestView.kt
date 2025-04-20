package ru.hse.online.client.presentation

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.presentation.common.BottomScreenName
import ru.hse.online.client.presentation.pedometer.hasLocationPermissions
import ru.hse.online.client.ui.theme.ClientTheme

class TestView : ComponentActivity() {
    private val bottomScreenName = BottomScreenName("Pedometer")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClientTheme {
                bottomScreenName.DisplayNameAndDraw {
                    RouteListScreen()
                }
            }
        }
    }
}

@Composable
fun RouteListScreen(viewModel: LocationViewModel = koinViewModel()) {
    val routePoints by viewModel.routePoints.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity
    var showRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        when {
            activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showRationale = true
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermissions(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    if (showRationale) {
        Text("Location permission is required for this feature")
        Button(onClick = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            showRationale = false
        }) {
            Text("Request Again")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Route History",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (routePoints.isEmpty()) {
                EmptyRouteMessage()
            } else {
                RoutePointsList(routePoints = routePoints)
            }
        }
    }
}

@Composable
private fun RoutePointsList(routePoints: List<LatLng>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(routePoints) { index, point ->
            RoutePointItem(
                index = index + 1,
                latitude = point.latitude,
                longitude = point.longitude
            )
        }
    }
}

@Composable
private fun RoutePointItem(index: Int, latitude: Double, longitude: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#$index",
                style = MaterialTheme.typography.bodyMedium,
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Lat: ${"%.6f".format(latitude)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Lon: ${"%.6f".format(longitude)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyRouteMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No locations recorded yet",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}