package com.syed.aesalert.data

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("aes_settings", Context.MODE_PRIVATE)

    var alertDistanceM: Int
        get() = prefs.getInt(KEY_ALERT_DISTANCE, DEFAULT_ALERT_DISTANCE)
        set(value) = prefs.edit().putInt(KEY_ALERT_DISTANCE, value).apply()

    companion object {
        private const val KEY_ALERT_DISTANCE = "alert_distance_m"
        const val DEFAULT_ALERT_DISTANCE = 1000

        val DISTANCE_OPTIONS = listOf(500, 1000, 2000, 3000, 5000)
    }
}
