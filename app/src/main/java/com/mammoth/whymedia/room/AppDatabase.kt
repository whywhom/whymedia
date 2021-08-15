package com.mammoth.whymedia.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookDb::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
