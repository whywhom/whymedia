package com.mammoth.whymedia.room

import androidx.room.*

@Dao
interface BookDao {
    @Query("SELECT * FROM bookDb")
    fun getAll(): List<BookDb>

    @Query("SELECT * FROM bookDb WHERE bookIds IN (:bookIds)")
    fun loadAllByIds(bookIds: IntArray): List<BookDb>

    @Query(
        "SELECT * FROM bookDb WHERE bookName IN (:bookName)"
    )
    fun findByName(bookName: String): BookDb

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg books: BookDb)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(book: BookDb)

    @Delete
    fun delete(book: BookDb)
}
