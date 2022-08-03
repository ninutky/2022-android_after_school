package com.example.quizquiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.quizquiz.database.Quiz
import com.example.quizquiz.database.QuizDatabase

class QuizCreateFragment : Fragment() {
    lateinit var db : QuizDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.quiz_create_fragment,
            container,
            false)
        db = QuizDatabase.getInstance(requireContext())

        view.findViewById<Button>(R.id.add_quiz_btn).setOnClickListener {
            val question = view.findViewById<EditText>(R.id.edit_question).text.toString()
            val answer = view.findViewById<EditText>(R.id.edit_answer).text.toString()
            val category = view.findViewById<EditText>(R.id.edit_category).text.toString()

            val quiz = Quiz(type="ox", question=question, answer=answer, category=category)
            Thread {
                db.quizDAO().insert(quiz)
            }.start()
        }

        return view
    }
}