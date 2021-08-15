package com.mammoth.whymedia.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mammoth.whymedia.MediaApplication
import com.mammoth.whymedia.room.BookDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class BookViewModel : ViewModel() {
    private var bookList:MutableList<BookDb> = mutableListOf()
    private val bookListMutableLiveData = MutableLiveData(bookList)
    val bookListLiveData: LiveData<MutableList<BookDb>> = bookListMutableLiveData
    fun saveBooksToDb(list: MutableList<String>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (fileUri in list) {
                    val file = File(fileUri)
                    if (file.exists()) {
                        val book = BookDb(0, file.name, fileUri.toString())
                        MediaApplication.db.bookDao().insertAll(book)
                    }
                }
                readBooksFromDb()
            }
        }
    }

    fun readBooksFromDb() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bookList = MediaApplication.db.bookDao().getAll().toMutableList()
                bookListMutableLiveData.postValue(bookList)
            }
        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}