package ru.hse.online.client.presentation.map

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.min

fun List<LatLng>.toGoogleMapsFormat(): String {
    return buildString {
        append("[[")
        this@toGoogleMapsFormat.forEachIndexed { index, latLng ->
            append("[${latLng.latitude}, ${latLng.longitude}]")
            if (index < this@toGoogleMapsFormat.size - 1) {
                append(", ")
            }
        }
        append("]]")
    }
}

fun String.googleMapsFormatToLatLngList(): List<LatLng> {
    val cleaned = this.replace("\\s".toRegex(), "")

    val coordPattern = "\\[([-+]?[0-9]*\\.?[0-9]+),([-+]?[0-9]*\\.?[0-9]+)]".toRegex()
    val matches = coordPattern.findAll(cleaned)

    return matches.map { matchResult ->
        val (lat, lng) = matchResult.destructured
        LatLng(lat.toDouble(), lng.toDouble())
    }.toList()
}

fun calculateCameraUpdate(
    path: List<LatLng>,
    tilt: Float = 45f
): CameraUpdate {
    val bounds = LatLngBounds.Builder().apply {
        path.forEach { include(it) }
    }.build()

    return CameraUpdateFactory.newCameraPosition(
        CameraPosition.Builder()
            .target(bounds.center)
            .zoom(calculateDynamicZoom(bounds) - 0.2f)
            .tilt(tilt)
            .build()
    )
}

fun calculateDynamicZoom(bounds: LatLngBounds): Float {
    val latDiff = bounds.northeast.latitude - bounds.southwest.latitude
    val lngDiff = bounds.northeast.longitude - bounds.southwest.longitude

    val midLat = bounds.center.latitude
    val latZoom = ln(360 / latDiff) / ln(2.0)
    val lngZoom = ln(360 / (lngDiff * cos(Math.toRadians(midLat)))) / ln(2.0)

    return min(latZoom, lngZoom).toFloat()
}

