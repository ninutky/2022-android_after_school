package com.example.weatherdustchecker

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.sql.ClientInfoStatus

@JsonDeserialize(using=MyDeserializer::class)
data class OpenWeatherAPIJSONResponse(val temp: Double, val id: Int)

class MyDeserializer : StdDeserializer<OpenWeatherAPIJSONResponse>(
    OpenWeatherAPIJSONResponse::class.java
) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?
    ): OpenWeatherAPIJSONResponse {
        val node = p?.codec?.readTree<JsonNode>(p)

        val weather = node?.get("weather")
        val firstWeather = weather?.elements()?.next()
        val id = firstWeather?.get("id")?.asInt()
        val main = node?.get("main")
        val temp = main?.get("temp")?.asDouble()

        return OpenWeatherAPIJSONResponse(temp!!, id!!)
    }
}

class WeatherPageFragment : Fragment() {
    lateinit var weatherImage : ImageView
    lateinit var statusText: TextView
    lateinit var temperatureText : TextView
    var APP_ID = "e6d34a6c11941bb61f1c4a527f6272b1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.weather_page_fragment,
            container, false)

        // TODO : aguments 값 참조해서 두 개 값 가져오고, 해당하는 뷰에 출력해주기
        statusText = view.findViewById<TextView>(R.id.weather_status_text)
        temperatureText = view.findViewById<TextView>(R.id.weather_temp_text)
        weatherImage = view.findViewById<ImageView>(R.id.weather_icon)

//        statusText.text = arguments?.getString("status")
//        temperatureText.text= arguments?.getDouble("temperature").toString()
//        // TODO : ImageView 가져와서 sun 이미지 출력하기
//        weatherImage.setImageResource(arguments?.getInt("res_id")!!)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        val url = "https://api.openweathermap.org/data/2.5/weather?units=metric&lat=37.58&lon=126.98&appid=${APP_ID}&lat=${lat}&lon=${lon}"

        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(WeatherAPIService::class.java)
        val apiCallForData = apiService.getWeatherStatusInfo(APP_ID, lat!!, lon!!)
        apiCallForData.enqueue(object : Callback<WeatherAPIService.OpenWeatherAPIJSONResponseGSON> {
            override fun onResponse(
                call: Call<WeatherAPIService.OpenWeatherAPIJSONResponseGSON>,
                response: Response<WeatherAPIService.OpenWeatherAPIJSONResponseGSON>
            ) {
                val data = response.body()
                Log.d("mytag", data.toString())

                // TODO : 다시 날씨 화면 나오도록 코드 작성
                val temp =data?.main?.get("temp")
                val id =data?.weather?.get(0)?.get("id")
                Log.d("mytag", temp.toString())
                Log.d("mytag", id.toString())

            }

            override fun onFailure(
                call: Call<WeatherAPIService.OpenWeatherAPIJSONResponseGSON>,
                t: Throwable
            ) {
                Toast.makeText(activity,
                "에러 발생 : ${t.message}",
                Toast.LENGTH_SHORT).show()
            }
        })
        APICall(object: APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("mytag", result)
                var mapper = jacksonObjectMapper()
                var data = mapper?.readValue<OpenWeatherAPIJSONResponse>(result)

                temperatureText.text = data.temp.toString()

                val id = data.id.toString()
                if(id != null) {
                    statusText.text = when {
                        id.startsWith("2") -> {
                            weatherImage.setImageResource(R.drawable.flash)
                            "천둥, 번개"
                        }
                        id.startsWith("3") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "이슬비"
                        }
                        id.startsWith("5") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "비"
                        }
                        id.startsWith("6") -> {
                            weatherImage.setImageResource(R.drawable.snow)
                            "눈"
                        }
                        id.startsWith("7") -> {
                            weatherImage.setImageResource(R.drawable.cloudy)
                            "흐림"
                        }
                        id.equals("800") -> {
                            weatherImage.setImageResource(R.drawable.sun)
                            "화창"
                        }
                        id.startsWith("8") -> {
                            weatherImage.setImageResource(R.drawable.cloud)
                            "구름 낌"
                        }
                        else -> "알 수 없음"
                    }
                }
            }
        }).execute(URL(url))
    }
    
    companion object {
        fun newInstance(lat: Double, lon: Double)
            : WeatherPageFragment
        {
            val fragment = WeatherPageFragment()
            
            // 번들 객체에 필요한 정보를 저장
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args
            return fragment
            
        }
    }
}