package com.mammoth.whymedia.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mammoth.whymedia.R
import com.mammoth.whymedia.room.BookDb

class GridRecyclerViewAdapter(var context:Context, data: ArrayList<BookDb>) : RecyclerView.Adapter<GridRecyclerViewAdapter.ViewHolder>() {
    private var itemClickListener: GridRecyclerViewAdapter.OnItemClickListener? = null
    var mData: ArrayList<BookDb> = data

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        itemClickListener = mItemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val root = inflater.inflate(R.layout.book_item, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bookTitle.text = mData[position].bookName
        holder.bookTitle.setOnClickListener { l->{

        } }
        holder.cover.setOnClickListener { l->{

        } }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setData(bookList: ArrayList<BookDb>) {
        mData = bookList
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val bookTitle: TextView = v.findViewById(R.id.bookTitle)
        val cover: ImageView = v.findViewById(R.id.bookCover)
    }
}
