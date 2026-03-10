package com.aesalert.app.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.aesalert.app.MainActivity
import com.aesalert.app.R
import com.aesalert.app.data.AESDatabase
import com.aesalert.app.data.AppSettings
import com.aesalert.app.logic.AlertManager
import com.aesalert.app.logic.AlertState
import com.aesalert.app.logic.CameraAlert
import com.aesalert.app.logic.ProximityEngine
import com.aesalert.app.logic.RouteSimulator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val speed: Float = 0f,
    val bearing: Float = 0f,
    val hasGps: Boolean = false,
    val alert: CameraAlert? = null,
    val cameraCount: Int = 0,
    val simulating: Boolean = false
)

class LocationTrackingService : Service(), LocationListener {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var locationManager: LocationManager? = null
    private var proximityEngine: ProximityEngine? = null
    private var alertManager: AlertManager? = null
    private var lastLocation: Location? = null
    private var simulationJob: Job? = null

    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): LocationTrackingService = this@LocationTrackingService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        alertManager = AlertManager(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        serviceScope.launch(Dispatchers.IO) {
            val db = AESDatabase.getDatabase(applicationContext)
            db.ensureSeeded()
            val cameras = db.cameraDao().getAllCamerasList()
            val settings = AppSettings(applicationContext)
            proximityEngine = ProximityEngine(cameras, settings.alertDistanceM)
            _locationState.value = _locationState.value.copy(cameraCount = cameras.size)
        }

        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        simulationJob?.cancel()
        locationManager?.removeUpdates(this)
        alertManager?.release()
        serviceScope.cancel()
    }

    fun reloadCameras() {
        serviceScope.launch(Dispatchers.IO) {
            val db = AESDatabase.getDatabase(applicationContext)
            val cameras = db.cameraDao().getAllCamerasList()
            val settings = AppSettings(applicationContext)
            proximityEngine = ProximityEngine(cameras, settings.alertDistanceM)
            _locationState.value = _locationState.value.copy(cameraCount = cameras.size)
        }
    }

    fun updateAlertDistance(distanceM: Int) {
        proximityEngine?.updateAlertDistance(distanceM)
    }

    fun startSimulation(route: String = "kajang") {
        simulationJob?.cancel()
        val simulator = RouteSimulator()
        val flow = when (route) {
            "jb" -> simulator.simulateJBApproach()
            else -> simulator.simulateKajangApproach()
        }

        simulationJob = serviceScope.launch {
            flow.collect { point ->
                val engine = proximityEngine ?: return@collect
                val alert = engine.checkProximity(
                    lat = point.latitude,
                    lon = point.longitude,
                    bearing = point.bearing,
                    speedKmh = point.speed
                )

                alertManager?.handleAlert(alert)

                _locationState.value = LocationState(
                    latitude = point.latitude,
                    longitude = point.longitude,
                    speed = point.speed,
                    bearing = point.bearing,
                    hasGps = true,
                    alert = alert,
                    cameraCount = _locationState.value.cameraCount,
                    simulating = true
                )
            }
            // Simulation ended - reset everything cleanly
            proximityEngine?.reset()
            _locationState.value = LocationState(
                cameraCount = _locationState.value.cameraCount,
                simulating = false
            )
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        proximityEngine?.reset()
        _locationState.value = LocationState(
            cameraCount = _locationState.value.cameraCount,
            simulating = false
        )
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            10f,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        // Ignore real GPS during simulation
        if (simulationJob?.isActive == true) return

        var bearing = location.bearing

        if (bearing == 0f && location.speed > 1f) {
            lastLocation?.let { prev ->
                bearing = prev.bearingTo(location)
                if (bearing < 0) bearing += 360f
            }
        }

        lastLocation = location

        val speedKmh = location.speed * 3.6f

        val engine = proximityEngine ?: return
        val alert = engine.checkProximity(
            lat = location.latitude,
            lon = location.longitude,
            bearing = bearing,
            speedKmh = speedKmh
        )

        alertManager?.handleAlert(alert)

        _locationState.value = LocationState(
            latitude = location.latitude,
            longitude = location.longitude,
            speed = speedKmh,
            bearing = bearing,
            hasGps = true,
            alert = alert,
            cameraCount = _locationState.value.cameraCount
        )
    }

    @Deprecated("Required for API < 29")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "AES Alert Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "GPS tracking for AES camera alerts"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AES Alert Active")
            .setContentText("Monitoring speed cameras")
            .setSmallIcon(R.drawable.ic_speed)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "aes_alert_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
