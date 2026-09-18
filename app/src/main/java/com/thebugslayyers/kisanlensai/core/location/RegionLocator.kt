package com.thebugslayyers.kisanlensai.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.thebugslayyers.kisanlensai.domain.model.CropRegion
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Works out which cropping region the device is in from its actual location.
 *
 * Uses the platform [LocationManager] rather than play-services-location: the app has no Play
 * Services dependency, and all this needs is a fix accurate to a degree or so, which the
 * network (coarse) provider gives for free and without a GPS warm-up.
 *
 * A fix is only ever a best effort. Location may be switched off, the permission may be refused,
 * or the device may simply have no cached position and not acquire one in time - every one of
 * those paths returns null so the caller can fall back to the default region and let the farmer
 * override it by hand.
 */
class RegionLocator(private val context: Context) {

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    /** The detected region, or null if no fix could be obtained. */
    suspend fun resolveRegion(): CropRegion? {
        if (!hasLocationPermission()) return null

        val fix = lastKnownLocation() ?: requestCurrentLocation()
        return fix?.let { CropRegion.forCoordinates(it.latitude, it.longitude) }
    }

    private fun locationManager(): LocationManager? =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    /**
     * Checks every provider's cache and takes the freshest fix. A cached position is good enough
     * here: the peninsula boundary is a straight latitude line, so a farmer would have to move
     * several hundred kilometres before a stale fix gave the wrong region.
     */
    @SuppressLint("MissingPermission") // guarded by hasLocationPermission() in resolveRegion()
    private fun lastKnownLocation(): Location? {
        val manager = locationManager() ?: return null
        return PROVIDERS.mapNotNull { provider ->
            try {
                if (manager.isProviderEnabled(provider)) manager.getLastKnownLocation(provider) else null
            } catch (e: SecurityException) {
                Log.d(TAG, "No access to $provider: ${e.message}")
                null
            } catch (e: IllegalArgumentException) {
                Log.d(TAG, "Provider $provider not present on this device")
                null
            }
        }.maxByOrNull { it.time }
    }

    /**
     * Asks for a single fresh fix when nothing is cached, giving up after [FIX_TIMEOUT_MS].
     *
     * The timeout matters: a cold GPS fix indoors can take minutes, and the Home screen should
     * never be left waiting on it.
     */
    @SuppressLint("MissingPermission") // guarded by hasLocationPermission() in resolveRegion()
    private suspend fun requestCurrentLocation(): Location? {
        val manager = locationManager() ?: return null
        val provider = PROVIDERS.firstOrNull {
            try {
                manager.isProviderEnabled(it)
            } catch (e: IllegalArgumentException) {
                false
            }
        } ?: return null

        var registered: LocationListener? = null
        return try {
            withTimeoutOrNull(FIX_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            if (continuation.isActive) continuation.resume(location)
                        }

                        // Required on API < 30; the platform still calls it there.
                        @Deprecated("Deprecated in API 30, still called on older devices")
                        override fun onStatusChanged(p: String?, status: Int, extras: Bundle?) = Unit

                        override fun onProviderEnabled(p: String) = Unit

                        override fun onProviderDisabled(p: String) {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                    registered = listener
                    continuation.invokeOnCancellation { manager.removeUpdates(listener) }

                    try {
                        manager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
                    } catch (e: SecurityException) {
                        Log.d(TAG, "Location request refused: ${e.message}")
                        if (continuation.isActive) continuation.resume(null)
                    }
                }
            }
        } finally {
            // Runs on the happy path, on timeout, and on cancellation - so no listener is leaked.
            registered?.let { manager.removeUpdates(it) }
        }
    }

    private companion object {
        const val TAG = "RegionLocator"
        const val FIX_TIMEOUT_MS = 8_000L

        /** Coarse providers first - they are what the permission we ask for actually grants. */
        val PROVIDERS = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
            LocationManager.GPS_PROVIDER
        )
    }
}
