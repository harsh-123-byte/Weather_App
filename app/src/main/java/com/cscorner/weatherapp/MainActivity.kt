package com.cscorner.weatherapp

import android.app.DownloadManager.Query
import android.bluetooth.BluetoothHidDevice
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cscorner.weatherapp.databinding.ActivityMainBinding
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// 91365c96c427454ed8dae5f09fa2db9d
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
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
        val response =
            retrofit.getWeatherData(cityName, "91365c96c427454ed8dae5f09fa2db9d", "metric")
        response.enqueue(object : retrofit2.Callback<WeatherApp> {
            override fun onResponse(
                call: Call<WeatherApp>,
                response: Response<WeatherApp>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    binding.temperature.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxtemp.text = "Max Temp: ${"%.1f".format(maxTemp)} °C"
                    binding.mintemp.text = "Min Temp: ${"%.1f".format(minTemp)} °C"
                    binding.Humidity.text = "$humidity %"
                    binding.Windspeed.text = "$windSpeed m/s"
                    binding.Sunrise.text = "${time(sunRise)}"
                    binding.Sunset.text = "${time(sunSet)}"
                    binding.Sea.text = "$seaLevel hPa"
                    binding.Conditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityname.text = "$cityName"

                    //Log.d("TAG", "onResponse: $temperature")

                    changeImagesAccordingToWeatherCondition(condition)
                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

//    private fun changeImagesAccordingToWeatherCondition(condition: String) {
//        when (condition) {
//            "Clear Sky", "Sunny", "Clear" -> {
//                binding.root.setBackgroundResource(R.drawable.sunny_background)
//                binding.lottieAnimationView.setAnimation(R.raw.sun)
//
//            }
//            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
//                binding.root.setBackgroundResource(R.drawable.colud_background)
//                binding.lottieAnimationView.setAnimation(R.raw.cloud)
//
//            }
//            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
//                binding.root.setBackgroundResource(R.drawable.rain_background)
//                binding.lottieAnimationView.setAnimation(R.raw.rain)
//
//            }
//            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
//                binding.root.setBackgroundResource(R.drawable.snow_background)
//                binding.lottieAnimationView.setAnimation(R.raw.snow)
//
//            }
//            else -> {
//                binding.root.setBackgroundResource(R.drawable.sunny_background)
//                binding.lottieAnimationView.setAnimation(R.raw.sun)
//
//            }
//
//
//
//        }
//        binding.lottieAnimationView.playAnimation()
//
//    }

    private fun changeImagesAccordingToWeatherCondition(condition: String) {
        when (condition) {
            "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Clouds", "Mist", "Fog" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Rain", "Drizzle", "Thunderstorm" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }


    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))

    }
    fun date(): String
    {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }


}
