package com.syed.aesalert.logic

import com.syed.aesalert.data.AESCamera
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class AlertState {
    CLEAR,
    APPROACHING,
    PASSING,
    PASSED
}

data class CameraAlert(
    val state: AlertState,
    val camera: AESCamera? = null,
    val distanceMeters: Double = 0.0,
    val speedKmh: Float = 0f
)

class ProximityEngine(
    private val cameras: List<AESCamera>,
    alertDistanceM: Int = 1000
) {

    private var currentState = AlertState.CLEAR
    private var activeCamera: AESCamera? = null
    private var passedTimestamp = 0L

    // Derived from user-configured alert distance
    var approachRadiusM: Double = alertDistanceM.toDouble()
        private set
    var scanRadiusM: Double = alertDistanceM * 2.0
        private set

    fun updateAlertDistance(distanceM: Int) {
        approachRadiusM = distanceM.toDouble()
        scanRadiusM = distanceM * 2.0
    }

    fun reset() {
        currentState = AlertState.CLEAR
        activeCamera = null
        passedTimestamp = 0L
    }

    companion object {
        private const val PASSING_RADIUS_M = 200.0
        private const val PASSED_COOLDOWN_MS = 30_000L
    }

    fun checkProximity(
        lat: Double,
        lon: Double,
        bearing: Float,
        speedKmh: Float
    ): CameraAlert {
        // Cooldown after passing a camera
        if (currentState == AlertState.PASSED) {
            if (System.currentTimeMillis() - passedTimestamp < PASSED_COOLDOWN_MS) {
                return CameraAlert(AlertState.PASSED, activeCamera, 0.0, speedKmh)
            }
            currentState = AlertState.CLEAR
            activeCamera = null
        }

        // Find nearest camera within scan radius that user is heading TOWARDS
        var nearestCamera: AESCamera? = null
        var nearestDist = Double.MAX_VALUE

        for (camera in cameras) {
            val dist = haversineMeters(lat, lon, camera.latitude, camera.longitude)

            if (dist > scanRadiusM) continue

            // Check user's travel direction matches camera's monitored direction
            if (!isDirectionMatch(bearing, camera.bearingAngle, camera.bearingTolerance)) continue

            // Check camera is AHEAD of user, not behind
            // Calculate bearing from user position to camera position
            val bearingToCamera = bearingBetween(lat, lon, camera.latitude, camera.longitude)
            if (!isCameraAhead(bearing, bearingToCamera)) continue

            if (dist < nearestDist) {
                nearestDist = dist
                nearestCamera = camera
            }
        }

        if (nearestCamera == null) {
            if (currentState == AlertState.APPROACHING || currentState == AlertState.PASSING) {
                currentState = AlertState.PASSED
                passedTimestamp = System.currentTimeMillis()
                return CameraAlert(AlertState.PASSED, activeCamera, 0.0, speedKmh)
            }
            currentState = AlertState.CLEAR
            activeCamera = null
            return CameraAlert(AlertState.CLEAR, speedKmh = speedKmh)
        }

        activeCamera = nearestCamera

        currentState = when {
            nearestDist <= PASSING_RADIUS_M -> AlertState.PASSING
            nearestDist <= approachRadiusM -> AlertState.APPROACHING
            else -> AlertState.APPROACHING
        }

        return CameraAlert(currentState, nearestCamera, nearestDist, speedKmh)
    }

    // Check user's travel direction matches camera's monitored direction
    private fun isDirectionMatch(
        userBearing: Float,
        cameraBearing: Float,
        tolerance: Float
    ): Boolean {
        var diff = abs(userBearing - cameraBearing)
        if (diff > 180f) diff = 360f - diff
        return diff <= tolerance
    }

    // Check camera is in front of user (within 90 degrees of travel direction)
    private fun isCameraAhead(userBearing: Float, bearingToCamera: Float): Boolean {
        var diff = abs(userBearing - bearingToCamera)
        if (diff > 180f) diff = 360f - diff
        return diff <= 90f
    }

    // Calculate bearing from point A to point B in degrees (0-360)
    private fun bearingBetween(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        val bearing = Math.toDegrees(atan2(y, x)).toFloat()
        return (bearing + 360f) % 360f
    }

    private fun haversineMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
