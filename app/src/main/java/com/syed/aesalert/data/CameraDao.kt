package com.syed.aesalert.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraDao {

    @Query("SELECT * FROM aes_cameras")
    fun getAllCameras(): Flow<List<AESCamera>>

    @Query("SELECT * FROM aes_cameras")
    suspend fun getAllCamerasList(): List<AESCamera>

    @Query("SELECT COUNT(*) FROM aes_cameras")
    suspend fun getCameraCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cameras: List<AESCamera>)

    @Query("UPDATE aes_cameras SET latitude = :lat, longitude = :lon, bearingAngle = :bearing, speedLimit = :speedLimit WHERE id = :id")
    suspend fun updateCamera(id: Int, lat: Double, lon: Double, bearing: Float, speedLimit: Int)

    @Query("DELETE FROM aes_cameras")
    suspend fun deleteAll()
}
