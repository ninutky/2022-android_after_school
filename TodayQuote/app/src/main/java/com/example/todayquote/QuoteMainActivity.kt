package com.example.todayquote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.*

data class Quote(var idx: Int, var text: String, var from: String = "")

class QuoteMainActivity : AppCompatActivity() {
    // 액티비티는 생성자 호출을 우리가 할 수 없고, 운영체제가 수행해주므로
    // 생성자에서 해당 값을 초기화를 못시켜주니까 lateinit으로 해서
    // 나중에 해당 값이 사용 전 반드시 초기화됨을 약속함
    private lateinit var quotes: MutableList<Quote>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quote_main_activity)
        
        var quoteText = findViewById<TextView>(R.id.quote_text)
        var quoteFrom = findViewById<TextView>(R.id.quote_from)

        quotes = mutableListOf()

        var q1 = Quote(1, "명언 1", "출처 1")
        quotes.add(q1)
        quotes.add(Quote(2, "명언 2", "출처 2"))
        quotes.add(Quote(3, "명언 3", "출처 3"))

        // java.util.Random
        val randomIndex = Random().nextInt(quotes.size)
        var randomQuote = quotes[randomIndex]

        quoteText.text = randomQuote.text
        quoteFrom.text = randomQuote.from
    }
}