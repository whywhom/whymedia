package com.mammoth.whymedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mammoth.whymedia.R
import com.mammoth.whymedia.databinding.ItemRecycleBookBinding
import com.mammoth.whymedia.model.Book
import utils.singleClick

class BookshelfAdapter(private val onBookClick: (Book) -> Unit,
                       private val onBookLongClick: (Book) -> Unit
) : ListAdapter<Book, BookshelfAdapter.ViewHolder>(BookListDiff()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycle_book, parent, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val book = getItem(position)

        viewHolder.bind(book)
    }

    inner class ViewHolder(private val binding: ItemRecycleBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.book = book
            binding.root.singleClick {
                onBookClick(book)
            }
            binding.root.setOnLongClickListener {
                onBookLongClick(book)
                true
            }
        }
    }

    private class BookListDiff : DiffUtil.ItemCallback<Book>() {

        override fun areItemsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.href == newItem.href
                    && oldItem.author == newItem.author
                    && oldItem.identifier == newItem.identifier
                    && oldItem.progression == newItem.progression
        }
    }

}