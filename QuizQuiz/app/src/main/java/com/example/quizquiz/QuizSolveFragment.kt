package com.example.quizquiz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.quizquiz.database.Quiz

class QuizSolveFragment : Fragment() {
    interface QuizSolveListener {
        fun onAnswerSelected(isCorrect: Boolean)
    }
    lateinit var listener : QuizSolveListener
    lateinit var quiz : Quiz

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(parentFragment is QuizSolveListener) {
            listener = parentFragment as QuizSolveListener
        } else {
            throw Exception("QuizSolveListener 미구현")
        }
    }

    // newInstance 클래스 메서드 (퀴즈 객체를 전달받도록 구현)
    companion object {
        fun newInstance(quiz: Quiz) : QuizSolveFragment {
            val fragment = QuizSolveFragment()

            val args = Bundle()
            args.putParcelable("quiz", quiz)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.quiz_solve_fragment, container, false)

        quiz = arguments?.getParcelable("quiz")!!
        view.findViewById<TextView>(R.id.question).text = quiz.question
        val choices = view.findViewById<ViewGroup>(R.id.choices)

        val answerSelectListener = View.OnClickListener {
            val guess = (it as Button).text.toString()

            // guess와 퀴즈의 실제 답을 비교해서 listener의
            // onAnswerSelected를 적절히 호출
            if(guess == quiz.answer) listener.onAnswerSelected(true)
            else listener.onAnswerSelected(false)
        }

        when(quiz.type) {
            "ox" -> {
                for(sign in listOf("o", "x")) {
                    var btn = Button(activity)
                    btn.text = sign
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
            "multiple_choice" -> {
                for(sign in quiz.guesses!!) {
                    var btn = Button(activity)
                    btn.text = sign
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
        }

        return view
    }
}





