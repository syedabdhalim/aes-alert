package com.aesalert.app.logic

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

class AlertManager(@Suppress("unused") private val context: android.content.Context) {

    private var toneGenerator: ToneGenerator? = null
    private var lastAlertState = AlertState.CLEAR
    private var beepCount = 0
    private val handler = Handler(Looper.getMainLooper())

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (_: Exception) {
            // Some devices may not support ToneGenerator
        }
    }

    fun handleAlert(alert: CameraAlert) {
        when (alert.state) {
            AlertState.APPROACHING -> {
                if (lastAlertState != AlertState.APPROACHING) {
                    beepCount = 0
                    playBeepSequence()
                }
            }
            AlertState.PASSING -> {
                // Keep alert visual active, no additional beeps
            }
            AlertState.PASSED, AlertState.CLEAR -> {
                beepCount = 0
            }
        }
        lastAlertState = alert.state
    }

    private fun playBeepSequence() {
        if (beepCount >= 3) return
        playBeep()
        beepCount++
        if (beepCount < 3) {
            handler.postDelayed({ playBeepSequence() }, 600)
        }
    }

    private fun playBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)
        } catch (_: Exception) {
            // Fail silently
        }
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        toneGenerator?.release()
        toneGenerator = null
    }
}
