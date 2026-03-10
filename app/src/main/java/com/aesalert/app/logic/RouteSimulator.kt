package com.aesalert.app.logic

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class SimulatedPoint(
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val bearing: Float
)

class RouteSimulator {

    // Simulates driving northbound on PLUS E2 towards KM301.6 Kajang camera
    // Camera is at: lat 2.9743424, lon 101.7426033, bearing 350, limit 90
    // Route: start ~2.5km south, pass through, continue ~1.5km past camera
    fun simulateKajangApproach(): Flow<SimulatedPoint> = flow {
        val bearing = 350f
        val speedKmh = 95f

        // Start 2.5km south, end 1.5km north of camera
        val startLat = 2.9520
        val startLon = 101.7440
        val endLat = 2.9878    // ~1.5km north of camera (lat 2.9743)
        val endLon = 101.7415

        val steps = 80
        val latStep = (endLat - startLat) / steps
        val lonStep = (endLon - startLon) / steps

        for (i in 0..steps) {
            val lat = startLat + (latStep * i)
            val lon = startLon + (lonStep * i)

            val speed = when {
                i < 5 -> 60f + (i * 7f)
                else -> speedKmh + (if (i % 3 == 0) -2f else 2f)
            }

            emit(SimulatedPoint(lat, lon, speed, bearing))
            delay(800)
        }
    }

    // Simulates driving southbound on PLUS E2 past KM1 JB camera
    // Camera is at: lat 1.5276324, lon 103.7535588, bearing 115, limit 110
    // Route: start ~2.5km before, continue ~1.5km past
    fun simulateJBApproach(): Flow<SimulatedPoint> = flow {
        val bearing = 115f
        val speedKmh = 105f

        val startLat = 1.5400
        val startLon = 103.7380
        val endLat = 1.5160    // ~1.5km past camera (lat 1.5276 heading SE)
        val endLon = 103.7670

        val steps = 80
        val latStep = (endLat - startLat) / steps
        val lonStep = (endLon - startLon) / steps

        for (i in 0..steps) {
            val lat = startLat + (latStep * i)
            val lon = startLon + (lonStep * i)

            val speed = when {
                i < 5 -> 70f + (i * 7f)
                else -> speedKmh + (if (i % 2 == 0) -1f else 1f)
            }

            emit(SimulatedPoint(lat, lon, speed, bearing))
            delay(800)
        }
    }
}
