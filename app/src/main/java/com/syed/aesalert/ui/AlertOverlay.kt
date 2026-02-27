package com.syed.aesalert.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syed.aesalert.logic.CameraAlert
import com.syed.aesalert.ui.theme.AlertRed
import com.syed.aesalert.ui.theme.DarkBg
import com.syed.aesalert.ui.theme.SafeGreen
import com.syed.aesalert.ui.theme.SpeedWhite
import com.syed.aesalert.ui.theme.WarningAmber

@Composable
fun AlertOverlay(
    alert: CameraAlert,
    modifier: Modifier = Modifier
) {
    val camera = alert.camera ?: return
    val isOverSpeed = alert.speedKmh > camera.speedLimit

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg.copy(alpha = 0.95f))
                .border(4.dp, AlertRed, RoundedCornerShape(0.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Warning header
                Text(
                    text = "!! AES CAMERA AHEAD !!",
                    color = AlertRed,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Camera name
                Text(
                    text = camera.name,
                    color = SpeedWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = camera.highway,
                    color = SpeedWhite.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Distance
                val distText = if (alert.distanceMeters >= 1000) {
                    "%.1f km".format(alert.distanceMeters / 1000)
                } else {
                    "%.0f m".format(alert.distanceMeters)
                }
                Text(
                    text = "Distance: $distText",
                    color = WarningAmber,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Speed limit
                Row(
                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speed limit circle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SPEED LIMIT",
                            color = SpeedWhite.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .border(4.dp, AlertRed, RoundedCornerShape(50.dp))
                                .background(SpeedWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${camera.speedLimit}",
                                color = DarkBg,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Current speed
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "YOUR SPEED",
                            color = SpeedWhite.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "%.0f".format(alert.speedKmh),
                            color = if (isOverSpeed) AlertRed else SafeGreen,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "km/h",
                            color = if (isOverSpeed) AlertRed else SafeGreen,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
