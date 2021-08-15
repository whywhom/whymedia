package com.mammoth.whymedia.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "bookDb")
data class BookDb(@PrimaryKey (autoGenerate = true) val bookIds: Int,
                  @ColumnInfo(name = "bookName") val bookName: String?,
                  @ColumnInfo(name = "strUri") val strUri: String?)  {

}
