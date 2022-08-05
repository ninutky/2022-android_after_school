package com.example.bookreview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BookRetouch : Fragment() {
    interface BookRetouchListener {
        fun onBookRetouch()
    }
    lateinit var listener : BookRetouchListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(parentFragment is BookRetouchListener) {
            listener = parentFragment as BookRetouchListener
        } else {
            throw Exception("BookRetouchListenerr 미구현")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.book_retouch,
            container,
            false)

        return view
    }
}