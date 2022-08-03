package com.example.quizquiz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class QuizResultFragment : Fragment() {
    interface QuizResultListener {
        fun onRetry()
    }
    lateinit var listener : QuizResultListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(parentFragment is QuizResultListener) {
            listener = parentFragment as QuizResultListener
        } else {
            throw Exception("QuizResultListener 미구현")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.quiz_result_fragment, container, false)

        val correctCount = arguments?.getInt("correct_count")!!
        val totalCount = arguments?.getInt("total_count")!!
        val ratio = correctCount.toDouble() / totalCount.toDouble()

        view.findViewById<TextView>(R.id.score).text = "${correctCount}/${totalCount}"
        view.findViewById<TextView>(R.id.score_ratio).text = ratio.toString()
        view.findViewById<TextView>(R.id.retry).setOnClickListener {
            listener.onRetry()
        }

        return view
    }

    companion object {
        fun newInstance(correctCount: Int, totalCount: Int)
                : QuizResultFragment {
            val fragment = QuizResultFragment()

            val args = Bundle()
            args.putInt("correct_count", correctCount)
            args.putInt("total_count", totalCount)
            fragment.arguments = args

            return fragment
        }
    }
}

