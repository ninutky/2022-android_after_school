package com.example.quizquiz

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.quizquiz.database.Quiz
import com.example.quizquiz.database.QuizDatabase
import com.google.android.material.navigation.NavigationView
import org.w3c.dom.Element
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    lateinit var drawerToggle : ActionBarDrawerToggle
    lateinit var db : QuizDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = QuizDatabase.getInstance(this)

        Thread(Runnable {
            for(quiz in db.quizDAO().getAll()) {
                Log.d("mytag", quiz.toString())
            }
        }).start()

        val sp : SharedPreferences = getSharedPreferences(
            "pref", Context.MODE_PRIVATE)
        if(sp.getBoolean("initialized", true)) {
            initQuizDataFromXMLFile()
            val editor = sp.edit()
            editor.putBoolean("initialized", false)
            editor.commit()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.drawer_nav_view)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.frame, QuizFragment())
            .commit()

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.quiz_solve -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizFragment())
                        .commit()
                }
                R.id.quiz_manage -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizListFragment())
                        .commit()
                }
                R.id.quiz_add -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizCreateFragment())
                        .commit()
                }
            }

            drawerLayout.closeDrawers()

            true
        }

        drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        ) {}
        // isDrawerIndicatorEnabled 속성을 true로 설정해 액션바의 왼쪽 상단에 위치한 햄버거 아이콘을 통해 내비게이션 드로어를 표시하고 숨길 수 있도록 합니다.
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        // setDisplayHomeAsUpEnabled 메서드를 호출해서 햄버거 아이콘을 표시하고 해당 아이콘을 클릭해 내비게이션 드로어를 열고 닫을 수 있도록 설
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        drawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun initQuizDataFromXMLFile() {
        AsyncTask.execute {
            val stream = assets.open("quizzes.xml")

            val docBuilder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
            val doc = docBuilder.parse(stream)

            val quizzesFromXMLDoc = doc.getElementsByTagName("quiz")
            val quizList = mutableListOf<Quiz>()
            Log.d("mytag", quizzesFromXMLDoc.length.toString())
            for(idx in 0 until quizzesFromXMLDoc.length) {
                // org.w3c.dom 패키지의 Element 클래스 import
                val e = quizzesFromXMLDoc.item(idx) as Element

                val type = e.getAttribute("type")
                val question = e.getElementsByTagName("question").item(0).textContent
                val answer = e.getElementsByTagName("answer").item(0).textContent
                val category = e.getElementsByTagName("category").item(0).textContent

                Log.d("mytag", type)
                Log.d("mytag", question)
                when(type) {
                    "ox" -> {
                        quizList.add(
                            Quiz(type=type,
                                question=question,
                                answer=answer,
                                category = category)
                        )
                    }
                    "multiple_choice" -> {
                        var choices = e.getElementsByTagName("choice")
                        var choiceList = mutableListOf<String>()
                        for(idx in 0 until choices.length) {
                            choiceList.add(choices.item(idx).textContent)
                        }
                        quizList.add(
                            Quiz(type=type, question=question,
                                answer=answer, category=category,
                                guesses=choiceList))
                    }
                }
            }

            for(quiz in quizList) {
                db.quizDAO().insert(quiz)
            }

        }
    }
}
