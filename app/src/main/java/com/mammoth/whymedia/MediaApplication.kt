package com.mammoth.whymedia

import android.app.Application
import androidx.room.Room
import com.mammoth.whymedia.room.AppDatabase

class MediaApplication: Application() {

    companion object {
        lateinit var db: AppDatabase
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "media-database"
        ).build()
    }
}