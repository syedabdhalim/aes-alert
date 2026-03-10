package com.aesalert.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AESCamera::class], version = 1, exportSchema = false)
abstract class AESDatabase : RoomDatabase() {

    abstract fun cameraDao(): CameraDao

    suspend fun ensureSeeded() {
        val dao = cameraDao()
        if (dao.getCameraCount() == 0) {
            dao.insertAll(CameraData.getAllCameras())
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AESDatabase? = null

        fun getDatabase(context: Context): AESDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AESDatabase::class.java,
                    "aes_cameras.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
