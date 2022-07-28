package com.example.fragmentstudy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.lang.Exception
import java.time.temporal.TemporalAmount

class CurrencyConverterFragment3 : Fragment () {
    interface CurrencyCalculationListener {
        fun onCalculate(result: Double,
            amount: Double,
            from: String,
            to: String)
    }
    lateinit var listener : CurrencyCalculationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is CurrencyCalculationListener) {
            listener = activity as CurrencyCalculationListener
        } else {
            throw Exception("CurrencyCalculationListener 미구현")
        }
    }

    val currencyExchangerMap = mapOf(
        // key, value
        "USD" to 1.0,
        "EUR" to 0.9,
        "JPY" to 110.0,
        "KRW" to 1150.0
    )

    fun calculateCurrency(amount: Double, from: String, to: String) : Double {
        val USDAmount = if(from != "USD") {
            (amount / currencyExchangerMap[from]!!)
        } else {
            amount
        }
        return currencyExchangerMap[to]!! * USDAmount
    }

    lateinit var fromCurrency:String
    lateinit var toCurrency:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(
            R.layout.currency_converter_fragment3,
            container,
            false
        )

        val calculateBtn = view.findViewById<Button>(R.id.calculate)
        val amount = view.findViewById<EditText>(R.id.amount)
        val exchangeType = view.findViewById<TextView>(R.id.exchange_type)

        fromCurrency = arguments?.getString("from", "USD")!!
        toCurrency = arguments?.getString("to", "USD")!!

        exchangeType.text = "${fromCurrency} => ${toCurrency} 변환"

        calculateBtn.setOnClickListener {
            val result = calculateCurrency(
                amount.text.toString().toDouble(),
                fromCurrency,
                toCurrency
            )

            listener.onCalculate(
                result,
                amount.text.toString().toDouble(),
                fromCurrency, toCurrency
            )
        }

        return view
    }

    companion object {
        fun newInstance(from: String, to: String): CurrencyConverterFragment3 {
            val fragment = CurrencyConverterFragment3()

            //번들 객체를 만들고 필요한 데이터 저장
            val args = Bundle()
            args.putString("from", from)
            args.putString("to", to)
            fragment.arguments = args

            return fragment
        }
    }
}