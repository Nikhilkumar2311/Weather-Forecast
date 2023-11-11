package com.example.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//API : 86fd0a5c71c31b65cbdb516c919b25b5

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchWeatherData("Patna")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "86fd0a5c71c31b65cbdb516c919b25b5", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    findViewById<TextView>(R.id.temp).text = "$temperature °C"
                    findViewById<TextView>(R.id.weather).text = condition
                    findViewById<TextView>(R.id.max_temp).text = "Max Temp: $maxTemp °C"
                    findViewById<TextView>(R.id.min_temp).text = "Min Temp: $minTemp °C"
                    findViewById<TextView>(R.id.humidity).text = "$humidity %"
                    findViewById<TextView>(R.id.windSpeed).text = "$windSpeed m/s"
                    findViewById<TextView>(R.id.sunRise).text = "${time(sunRise)}"
                    findViewById<TextView>(R.id.sunset).text = "${time(sunSet)}"
                    findViewById<TextView>(R.id.sea).text = "$seaLevel hPa"
                    findViewById<TextView>(R.id.condition).text = condition
                    findViewById<TextView>(R.id.city).text = "$cityName"
                    findViewById<TextView>(R.id.day).text = dayName(System.currentTimeMillis())
                    findViewById<TextView>(R.id.date).text = date()

                    CIATWC(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })

    }

    private fun CIATWC(conditions: String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear" -> {
                findViewById<ConstraintLayout>(R.id.root).setBackgroundResource(R.drawable.sunny_background)
                val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
                lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" -> {
                findViewById<ConstraintLayout>(R.id.root).setBackgroundResource(R.drawable.cloud_background)
                val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
                lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                findViewById<ConstraintLayout>(R.id.root).setBackgroundResource(R.drawable.rain_background)
                val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
                lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Blizzard", "Heavy Snow" -> {
                findViewById<ConstraintLayout>(R.id.root).setBackgroundResource(R.drawable.snow_background)
                val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
                lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                findViewById<ConstraintLayout>(R.id.root).setBackgroundResource(R.drawable.sunny_background)
                val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
                lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        lottieAnimationView.playAnimation()
    }

    private fun date(): String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}