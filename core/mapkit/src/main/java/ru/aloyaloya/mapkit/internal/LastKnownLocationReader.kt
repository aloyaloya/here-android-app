package ru.aloyaloya.mapkit.internal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager as AndroidLocationManager
import androidx.core.content.ContextCompat
import com.yandex.mapkit.geometry.Point

/** Последняя известная точка из системного LocationManager (нужны runtime-пермишны). */
internal object LastKnownLocationReader {

    private val providerOrder = listOf(
        AndroidLocationManager.GPS_PROVIDER,
        AndroidLocationManager.NETWORK_PROVIDER,
        AndroidLocationManager.PASSIVE_PROVIDER,
    )

    fun hasLocationPermission(appContext: Context): Boolean =
        ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

    fun readBestPoint(appContext: Context): Point? {
        if (!hasLocationPermission(appContext)) return null
        return readBestPointWhenPermitted(appContext)
    }

    @SuppressLint("MissingPermission")
    private fun readBestPointWhenPermitted(appContext: Context): Point? {
        val lm = appContext.getSystemService(Context.LOCATION_SERVICE) as? AndroidLocationManager
            ?: return null
        var best: android.location.Location? = null
        for (provider in providerOrder) {
            if (!lm.isProviderEnabled(provider)) continue
            val loc = try {
                lm.getLastKnownLocation(provider)
            } catch (_: SecurityException) {
                null
            } ?: continue
            if (best == null || loc.time > best.time) {
                best = loc
            }
        }
        return best?.let { Point(it.latitude, it.longitude) }
    }
}
