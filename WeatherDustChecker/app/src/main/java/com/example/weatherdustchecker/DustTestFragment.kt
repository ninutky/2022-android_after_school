package com.example.weatherdustchecker

import android.os.Bundle
import android.telecom.Call
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
import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

@JsonDeserialize(using=DustCheckerResponseDeserializer::class)
data class DustCheckResponse(
    val pm10: Int,
    val pm25: Int,
    val pm10Status: String,
    val pm25Status: String
)

class DustCheckerResponseDeserializer :
    StdDeserializer<DustCheckResponse>(DustCheckResponse::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): DustCheckResponse {
        fun checkCategory( aqi: Int ): String {
            return when(aqi) {
                in (0 .. 100) -> "좋음"
                in (101 .. 200) -> "보통"
                in (201 .. 300) -> "나쁨"
                else -> "매우 나쁨"
            }
        }

        var node = p?.codec?.readTree<JsonNode>(p)
        var data = node?.get("data")
        var iaqi = data?.get("iaqi")
        var pm10 = iaqi?.get("pm10")?.get("v")?.asInt()
        var pm25 = iaqi?.get("pm25")?.get("v")?.asInt()

        return DustCheckResponse(pm10!!, pm25!!,
            checkCategory(pm10!!),
            checkCategory(pm25!!))
    }
}

class DustPageFragment : Fragment() {
    val APP_TOKEN = "e5f3c86658dad419e4a7ad4b59299a8bed4c3c41"
    lateinit var statusImage : ImageView
    lateinit var pm25StatusText : TextView
    lateinit var pm25IntensityText : TextView
    lateinit var pm10StatusText : TextView
    lateinit var pm10IntensityText : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.dust_page_fragment,
                container, false)

        statusImage = view.findViewById(R.id.dust_status_icon)
        pm25StatusText = view.findViewById(R.id.dust_pm25_status_text)
        pm25IntensityText = view.findViewById(R.id.dust_pm25_intensity_text)
        pm10StatusText = view.findViewById(R.id.dust_pm10_status_text)
        pm10IntensityText = view.findViewById(R.id.dust_pm10_intensity_text)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments?.getDouble("lat")
        val lon = arguments?.getDouble("lon")
        val url = "http://api.waqi.info/feed/geo:${lat};${lon}/?token=${APP_TOKEN}"
        Log.d("mytag", url)
        APICall(object: APICall.APICallback {
            override fun onComplete(result: String) {
                Log.d("mytag", result)

                var mapper = jacksonObjectMapper()
                val data = mapper.readValue<DustCheckResponse>(result)

                Log.d("mytag", data.toString())

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.waqi.info")
                    .addConverterFactory(
                        GsonConverterFactory.create(
                            GsonBuilder().registerTypeAdapter(
                                DustCheckResponseGSON::class.java,
                                DustCheckerResponseDeserializerGSON()
                            ).create()
                        )
                    ).build()

                val apiService = retrofit.create(DustAPIService::class.java)
                val apiCallForData = apiService.getDustStatusInfo(lat!!, lon!!, APP_TOKEN)

                apiCallForData.enqueue(object : Callback<DustCheckResponseGSON> {

                    //                    override fun onResponse(
//                        call: Call<DustCheckResponseGSON>,
//                        response: Response<DustCheckResponseGSON>
//                    ) {
//                        val data = response.body()!!
//                        Log.d("mytag", data.toString())
//                    }
//
//                    override fun onFailure(call: Call<DustCheckResponseGSON>, t: Throwable) {
//                        Toast.makeText(activity,
//                        "에러 발생 : ${t.message}",
//                        Toast.LENGTH_SHORT).show()
//                    }
                    override fun onResponse(
                        call: retrofit2.Call<DustCheckResponseGSON>,
                        response: Response<DustCheckResponseGSON>
                    ) {
                        val data = response.body()!!
                        Log.d("mytag", data.toString())

                        // TODO : 화면 구성하기
                        statusImage.setImageResource(when(data.pm25Status) {
                            "좋음" -> R.drawable.good
                            "보통" -> R.drawable.normal
                            "나쁨" -> R.drawable.bad
                            else -> R.drawable.very_bad
                        })
                        pm25IntensityText.text = data.pm25?.toString() ?: "알 수 없음"
                        pm10IntensityText.text = data.pm10?.toString() ?: "알 수 없음"

                        pm25StatusText.text = "${data.pm25Status} (초미세먼지)"
                        pm10StatusText.text = "${data.pm10Status} (미세먼지)"

                    }

                    override fun onFailure(
                        call: retrofit2.Call<DustCheckResponseGSON>,
                        t: Throwable
                    ) {
                        Toast.makeText(activity,
                        "에러 발생 : ${t.message}",
                        Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }).execute(URL(url))
    }

    companion object {
        fun newInstance(lat: Double, lon: Double)
                : DustPageFragment {
            val fragment = DustPageFragment()

            // 번들 객체에 필요한 정보를 저장
            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.arguments = args

            return fragment
        }
    }
}