package com.cscorner.weatherapp

import retrofit2.Call
import retrofit2.http.Query
import retrofit2.http.GET

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String

    ): Call<WeatherApp>

}