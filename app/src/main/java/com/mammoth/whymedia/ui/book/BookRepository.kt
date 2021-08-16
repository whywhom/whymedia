/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package com.mammoth.whymedia.ui.book

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import com.mammoth.whymedia.db.BooksDao
import com.mammoth.whymedia.model.Book
import com.mammoth.whymedia.model.Bookmark
import com.mammoth.whymedia.model.Highlight
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.indexOfFirstWithHref
import utils.extensions.authorName

class BookRepository(private val booksDao: BooksDao) {

    fun books(): LiveData<List<Book>> = booksDao.getAllBooks()

    suspend fun insertBook(href: String, extension: String, publication: Publication): Long {
        val book = Book(
            creation = DateTime().toDate().time,
            title = publication.metadata.title,
            author = publication.metadata.authorName,
            href = href,
            identifier = publication.metadata.identifier ?: "",
            ext = ".$extension",
            progression = "{}"
        )
        return booksDao.insertBook(book)
    }

    suspend fun deleteBook(id: Long) = booksDao.deleteBook(id)

    suspend fun saveProgression(locator: String, bookId: Long) =
        booksDao.saveProgression(locator, bookId)

    suspend fun insertBookmark(bookId: Long, publication: Publication, locator: Locator): Long {
        val resource = publication.readingOrder.indexOfFirstWithHref(locator.href)!!
        val bookmark = Bookmark(
            creation = DateTime().toDate().time,
            bookId = bookId,
            publicationId = publication.metadata.identifier ?: publication.metadata.title,
            resourceIndex = resource.toLong(),
            resourceHref = locator.href,
            resourceType = locator.type,
            resourceTitle = locator.title.orEmpty(),
            location = Locator.Locations(
                progression = locator.locations.progression,
                position = locator.locations.position
            ).toJSON().toString(),
            locatorText = Locator.Text().toJSON().toString()
        )

        return booksDao.insertBookmark(bookmark)
    }

    fun bookmarksForBook(bookId: Long): LiveData<MutableList<Bookmark>> =
        booksDao.getBookmarksForBook(bookId)

    suspend fun deleteBookmark(bookmarkId: Long) = booksDao.deleteBookmark(bookmarkId)

    suspend fun highlightById(id: Long): Highlight? =
        booksDao.getHighlightById(id)

    fun highlightsForBook(bookId: Long): Flow<List<Highlight>> =
        booksDao.getHighlightsForBook(bookId)

    suspend fun addHighlight(bookId: Long, style: Highlight.Style, @ColorInt tint: Int, locator: Locator, annotation: String): Long =
        booksDao.insertHighlight(Highlight(bookId, style, tint, locator, annotation))

    suspend fun deleteHighlight(id: Long) = booksDao.deleteHighlight(id)

    suspend fun updateHighlightAnnotation(id: Long, annotation: String) {
        booksDao.updateHighlightAnnotation(id, annotation)
    }

    suspend fun updateHighlightStyle(id: Long, style: Highlight.Style, @ColorInt tint: Int) {
        booksDao.updateHighlightStyle(id, style, tint)
    }
}