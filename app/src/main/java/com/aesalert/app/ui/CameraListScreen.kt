package com.aesalert.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aesalert.app.data.AESCamera
import com.aesalert.app.ui.theme.AlertRed
import com.aesalert.app.ui.theme.DarkBg
import com.aesalert.app.ui.theme.InfoBlue
import com.aesalert.app.ui.theme.SafeGreen
import com.aesalert.app.ui.theme.SpeedWhite
import com.aesalert.app.ui.theme.SurfaceDark
import com.aesalert.app.ui.theme.WarningAmber

@Composable
fun CameraListScreen(
    cameras: List<AESCamera>,
    onBack: () -> Unit,
    onEdit: (AESCamera) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onBack() }
                    .background(SpeedWhite.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "< Back",
                    color = InfoBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "AES Cameras (${cameras.size})",
                color = SpeedWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onReset() }
                    .background(AlertRed.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Reset All",
                    color = AlertRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Camera list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            items(cameras, key = { it.id }) { camera ->
                CameraRow(camera = camera, onClick = { onEdit(camera) })
                HorizontalDivider(
                    color = SpeedWhite.copy(alpha = 0.08f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
private fun CameraRow(camera: AESCamera, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speed limit badge
        Box(
            modifier = Modifier
                .width(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(AlertRed.copy(alpha = 0.15f))
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${camera.speedLimit}",
                color = AlertRed,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Camera info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = camera.name,
                color = SpeedWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${camera.highway}  |  ${camera.state}  |  ${camera.direction}",
                color = SpeedWhite.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // Coordinates
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "%.5f, %.5f".format(camera.latitude, camera.longitude),
                color = SpeedWhite.copy(alpha = 0.35f),
                fontSize = 11.sp
            )
            Text(
                text = "Bearing: %.0f".format(camera.bearingAngle),
                color = SpeedWhite.copy(alpha = 0.35f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun EditCameraDialog(
    camera: AESCamera,
    onDismiss: () -> Unit,
    onSave: (lat: Double, lon: Double, bearing: Float, speedLimit: Int) -> Unit
) {
    var latText by remember { mutableStateOf(camera.latitude.toString()) }
    var lonText by remember { mutableStateOf(camera.longitude.toString()) }
    var bearingText by remember { mutableStateOf(camera.bearingAngle.toInt().toString()) }
    var speedText by remember { mutableStateOf(camera.speedLimit.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = camera.name,
                color = SpeedWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${camera.highway} | ${camera.state}",
                    color = SpeedWhite.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SpeedWhite,
                    unfocusedTextColor = SpeedWhite,
                    focusedBorderColor = InfoBlue,
                    unfocusedBorderColor = SpeedWhite.copy(alpha = 0.3f),
                    focusedLabelColor = InfoBlue,
                    unfocusedLabelColor = SpeedWhite.copy(alpha = 0.5f),
                    cursorColor = InfoBlue
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latText,
                        onValueChange = { latText = it },
                        label = { Text("Latitude") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lonText,
                        onValueChange = { lonText = it },
                        label = { Text("Longitude") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bearingText,
                        onValueChange = { bearingText = it },
                        label = { Text("Bearing (0-360)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = speedText,
                        onValueChange = { speedText = it },
                        label = { Text("Speed Limit") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = fieldColors,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val lat = latText.toDoubleOrNull() ?: camera.latitude
                    val lon = lonText.toDoubleOrNull() ?: camera.longitude
                    val bearing = bearingText.toFloatOrNull() ?: camera.bearingAngle
                    val speed = speedText.toIntOrNull() ?: camera.speedLimit
                    onSave(lat, lon, bearing, speed)
                }
            ) {
                Text("Save", color = SafeGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SpeedWhite.copy(alpha = 0.6f))
            }
        }
    )
}
