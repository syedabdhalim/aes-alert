package com.aesalert.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aes_cameras")
data class AESCamera(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val speedLimit: Int,
    val bearingAngle: Float,
    val bearingTolerance: Float = 60f,
    val direction: String,
    val state: String,
    val highway: String
)
