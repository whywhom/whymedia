package com.mammoth.whymedia.ui.book

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mammoth.whymedia.adapter.GridRecyclerViewAdapter
import com.mammoth.whymedia.databinding.FragmentBookBinding
import com.mammoth.whymedia.room.BookDb
import java.util.*

class BookFragment : Fragment() {

    private lateinit var adapter: GridRecyclerViewAdapter
    private var bookList: ArrayList<BookDb> = arrayListOf()
    private lateinit var bookViewModel: BookViewModel
    private var _binding: FragmentBookBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val clipData = result.data?.clipData
                val strPath = result.data?.data
                val list: MutableList<String> = arrayListOf()
                strPath?.let {
                    val strUri = context?.let { ctx -> getPath(ctx,it) }
                    if (strUri != null) {
                        list.add(strUri)
                    }
                }
                clipData?.let {
                    val count = it.itemCount
                    var index = 0;
                    while(index < count){
                        val strUri = context?.let { ctx -> getPath(ctx,it.getItemAt(index).uri) }
                        if (strUri != null) {
                            list.add(strUri)
                        }
                        index++
                    }
                }
                if (list.size > 0){
                    bookViewModel.saveBooksToDb(list)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bookViewModel =
            ViewModelProvider(this).get(BookViewModel::class.java)

        _binding = FragmentBookBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.fab.setOnClickListener {
            importBooks()
        }
        initViewModel()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GridLayoutManager(
            requireContext(), // context
            3, // span count
            RecyclerView.VERTICAL, // orientation
            false // reverse layout
        ).apply {
            // specify the layout manager for recycler view
            binding.recyclerView.layoutManager = this
        }
        adapter = GridRecyclerViewAdapter(requireActivity(), bookList)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : GridRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {

            }
        })
        bookViewModel.readBooksFromDb()
    }

    private val bookChangeObserver = Observer<MutableList<BookDb>> { it ->
        if(it.size >= 0){
            bookList = ArrayList<BookDb>(it)
            adapter.setData(bookList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initViewModel() {
        bookViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        bookViewModel.bookListLiveData.observe(viewLifecycleOwner, bookChangeObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun importBooks() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/epub+zip"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(intent)
    }

    private fun getPath(context: Context, uri: Uri): String? {
        var path: String? = null
        // Start with file:
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            path = uri.path
            return path
        }
        // Start with content://, such as content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            val cursor: Cursor? = context.contentResolver
                .query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex)
                    }
                }
                cursor.close()
            }
            return path
        }
        // 4.4
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        path = context.getExternalFilesDir(split[1]).toString()
                        return path
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    path = getDataColumn(context, contentUri, null, null)
                    return path
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    path = contentUri?.let { getDataColumn(context, it, selection, selectionArgs) }
                    return path
                }
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}