package com.aesalert.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aesalert.app.data.AppSettings
import com.aesalert.app.ui.theme.AlertRed
import com.aesalert.app.ui.theme.InfoBlue
import com.aesalert.app.ui.theme.SafeGreen
import com.aesalert.app.ui.theme.SpeedWhite
import com.aesalert.app.ui.theme.SurfaceDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    currentDistanceM: Int,
    isSimulating: Boolean,
    onSimulate: (String) -> Unit,
    onStopSimulation: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (distanceM: Int) -> Unit
) {
    var selected by remember { mutableIntStateOf(currentDistanceM) }

    val uriHandler = LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "Alert Distance",
                color = SpeedWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.wrapContentHeight().verticalScroll(rememberScrollState())) {
                Text(
                    text = "How far before AES camera to trigger alert?",
                    color = SpeedWhite.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppSettings.DISTANCE_OPTIONS.forEach { distM ->
                        val isSelected = distM == selected
                        val label = if (distM >= 1000) "${distM / 1000} km" else "$distM m"

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    2.dp,
                                    if (isSelected) InfoBlue else SpeedWhite.copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (isSelected) InfoBlue.copy(alpha = 0.2f)
                                    else SpeedWhite.copy(alpha = 0.05f)
                                )
                                .clickable { selected = distM }
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) InfoBlue else SpeedWhite.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val scanLabel = if (selected * 2 >= 1000) "${selected * 2 / 1000} km" else "${selected * 2} m"
                Text(
                    text = "Scan radius: $scanLabel",
                    color = SpeedWhite.copy(alpha = 0.4f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = SpeedWhite.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Simulation",
                    color = SpeedWhite.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (isSimulating) {
                        SimChip("Stop Simulation", AlertRed) { onStopSimulation() }
                    } else {
                        SimChip("Sim Kajang", InfoBlue) { onSimulate("kajang") }
                        SimChip("Sim JB", InfoBlue) { onSimulate("jb") }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = SpeedWhite.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "About",
                    color = SpeedWhite.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AES Alert Tracker v0.1.0",
                    color = SpeedWhite,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "github.com/syedabdhalim/aes-alert",
                    color = InfoBlue,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri("https://github.com/syedabdhalim/aes-alert") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selected) }) {
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

@Composable
private fun SimChip(text: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
