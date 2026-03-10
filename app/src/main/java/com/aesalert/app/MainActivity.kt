package com.aesalert.app

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.aesalert.app.data.AESCamera
import com.aesalert.app.data.AESDatabase
import com.aesalert.app.data.AppSettings
import com.aesalert.app.data.CameraData
import com.aesalert.app.service.LocationState
import com.aesalert.app.service.LocationTrackingService
import com.aesalert.app.ui.CameraListScreen
import com.aesalert.app.ui.EditCameraDialog
import com.aesalert.app.ui.MainScreen
import com.aesalert.app.ui.SettingsDialog
import com.aesalert.app.ui.theme.AESAlertTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var trackingService: LocationTrackingService? = null
    private var bound by mutableStateOf(false)
    private lateinit var appSettings: AppSettings

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as LocationTrackingService.LocalBinder
            trackingService = localBinder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            trackingService = null
            bound = false
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (fineGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                startTrackingService()
            }
        }
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        startTrackingService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appSettings = AppSettings(applicationContext)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        checkAndRequestPermissions()

        setContent {
            AESAlertTheme {
                var showCameraList by remember { mutableStateOf(false) }
                var showSettings by remember { mutableStateOf(false) }
                var editingCamera by remember { mutableStateOf<AESCamera?>(null) }

                val flow = if (bound) {
                    trackingService?.locationState
                } else {
                    null
                } ?: MutableStateFlow(LocationState())

                val locationState by flow.collectAsState()
                val cameras by (if (bound) {
                    AESDatabase.getDatabase(applicationContext).cameraDao().getAllCameras()
                } else {
                    MutableStateFlow(emptyList())
                }).collectAsState(initial = emptyList())

                if (showCameraList) {
                    CameraListScreen(
                        cameras = cameras,
                        onBack = { showCameraList = false },
                        onEdit = { camera -> editingCamera = camera },
                        onReset = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = AESDatabase.getDatabase(applicationContext).cameraDao()
                                dao.deleteAll()
                                dao.insertAll(CameraData.getAllCameras())
                                trackingService?.reloadCameras()
                            }
                        }
                    )
                } else {
                    MainScreen(
                        locationState = locationState,
                        onSimulate = { route -> trackingService?.startSimulation(route) },
                        onStopSimulation = { trackingService?.stopSimulation() },
                        onOpenCameras = { showCameraList = true },
                        onOpenSettings = { showSettings = true }
                    )
                }

                editingCamera?.let { camera ->
                    EditCameraDialog(
                        camera = camera,
                        onDismiss = { editingCamera = null },
                        onSave = { lat, lon, bearing, speedLimit ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = AESDatabase.getDatabase(applicationContext).cameraDao()
                                dao.updateCamera(camera.id, lat, lon, bearing, speedLimit)
                                trackingService?.reloadCameras()
                            }
                            editingCamera = null
                        }
                    )
                }

                if (showSettings) {
                    SettingsDialog(
                        currentDistanceM = appSettings.alertDistanceM,
                        onDismiss = { showSettings = false },
                        onSave = { distanceM ->
                            appSettings.alertDistanceM = distanceM
                            trackingService?.updateAlertDistance(distanceM)
                            showSettings = false
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindTrackingService()
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            locationPermissionLauncher.launch(notGranted.toTypedArray())
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bgGranted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (!bgGranted) {
                    backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    startTrackingService()
                }
            } else {
                startTrackingService()
            }
        }
    }

    private fun startTrackingService() {
        val intent = Intent(this, LocationTrackingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindTrackingService()
    }

    private fun bindTrackingService() {
        val intent = Intent(this, LocationTrackingService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}
