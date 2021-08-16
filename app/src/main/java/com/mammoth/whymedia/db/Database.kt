package com.mammoth.whymedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mammoth.whymedia.model.*

@Database(
    entities = [Book::class, Bookmark::class, Highlight::class, Catalog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(HighlightConverters::class)
abstract class BookDatabase : RoomDatabase() {

    abstract fun booksDao(): BooksDao

    abstract fun catalogDao(): CatalogDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "books_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}