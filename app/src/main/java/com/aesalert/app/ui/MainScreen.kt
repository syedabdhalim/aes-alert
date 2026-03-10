package com.aesalert.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aesalert.app.logic.AlertState
import com.aesalert.app.service.LocationState
import com.aesalert.app.ui.theme.AlertRed
import com.aesalert.app.ui.theme.DarkBg
import com.aesalert.app.ui.theme.InfoBlue
import com.aesalert.app.ui.theme.SafeGreen
import com.aesalert.app.ui.theme.SpeedWhite
import com.aesalert.app.ui.theme.SurfaceDark
import com.aesalert.app.ui.theme.WarningAmber

@Composable
fun MainScreen(
    locationState: LocationState,
    onSimulate: (String) -> Unit = {},
    onStopSimulation: () -> Unit = {},
    onOpenCameras: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val alert = locationState.alert
    val isAlertActive = alert != null &&
            (alert.state == AlertState.APPROACHING || alert.state == AlertState.PASSING)

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        if (isAlertActive && alert != null) {
            AlertOverlay(alert = alert)
        } else {
            DashboardContent(
                locationState = locationState,
                onSimulate = onSimulate,
                onStopSimulation = onStopSimulation,
                onOpenCameras = onOpenCameras,
                onOpenSettings = onOpenSettings
            )
        }
    }
}

@Composable
private fun DashboardContent(
    locationState: LocationState,
    onSimulate: (String) -> Unit,
    onStopSimulation: () -> Unit,
    onOpenCameras: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            // Left panel - Speed
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "%.0f".format(locationState.speed),
                    color = SpeedWhite,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "km/h",
                    color = SpeedWhite.copy(alpha = 0.6f),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                val gpsColor = if (locationState.hasGps) SafeGreen else AlertRed
                val gpsText = when {
                    locationState.simulating -> "SIM MODE"
                    locationState.hasGps -> "GPS: Active"
                    else -> "GPS: Waiting..."
                }
                val gpsDisplayColor = if (locationState.simulating) WarningAmber else gpsColor
                Text(
                    text = gpsText,
                    color = gpsDisplayColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Cameras: ${locationState.cameraCount}",
                    color = SpeedWhite.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 24.dp),
                thickness = 1.dp,
                color = SpeedWhite.copy(alpha = 0.2f)
            )

            // Right panel - Camera info
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "AES Alert Tracker",
                    color = InfoBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                val alert = locationState.alert
                if (alert?.camera != null && alert.state != AlertState.CLEAR) {
                    val camera = alert.camera

                    InfoRow("Next:", camera.name)
                    InfoRow("Highway:", camera.highway)

                    val distText = when {
                        alert.state == AlertState.PASSED -> "--"
                        alert.distanceMeters >= 1000 -> "%.1f km".format(alert.distanceMeters / 1000)
                        else -> "%.0f m".format(alert.distanceMeters)
                    }
                    InfoRow("Distance:", distText)
                    InfoRow("Limit:", "${camera.speedLimit} km/h")
                    InfoRow("Direction:", camera.direction)

                    Spacer(modifier = Modifier.height(12.dp))

                    val statusColor = when (alert.state) {
                        AlertState.APPROACHING -> WarningAmber
                        AlertState.PASSING -> AlertRed
                        AlertState.PASSED -> SafeGreen
                        AlertState.CLEAR -> SafeGreen
                    }
                    val statusText = when (alert.state) {
                        AlertState.APPROACHING -> "APPROACHING"
                        AlertState.PASSING -> "PASSING"
                        AlertState.PASSED -> "PASSED"
                        AlertState.CLEAR -> "CLEAR"
                    }

                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    InfoRow("Status:", "CLEAR")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "No AES cameras nearby",
                        color = SpeedWhite.copy(alpha = 0.4f),
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (locationState.hasGps) {
                        Text(
                            text = "Bearing: %.0f".format(locationState.bearing),
                            color = SpeedWhite.copy(alpha = 0.3f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Lat: %.5f  Lon: %.5f".format(
                                locationState.latitude,
                                locationState.longitude
                            ),
                            color = SpeedWhite.copy(alpha = 0.3f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Simulation controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TEST:",
                color = SpeedWhite.copy(alpha = 0.4f),
                fontSize = 12.sp
            )

            if (locationState.simulating) {
                SimButton("Stop", AlertRed) { onStopSimulation() }
            } else {
                SimButton("Sim Kajang", InfoBlue) { onSimulate("kajang") }
                SimButton("Sim JB", InfoBlue) { onSimulate("jb") }
            }

            Spacer(modifier = Modifier.weight(1f))

            SimButton("Settings", SpeedWhite.copy(alpha = 0.6f)) { onOpenSettings() }
            SimButton("Cameras", WarningAmber) { onOpenCameras() }
        }
    }
}

@Composable
private fun SimButton(text: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = SpeedWhite.copy(alpha = 0.6f),
            fontSize = 16.sp,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            color = SpeedWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}
