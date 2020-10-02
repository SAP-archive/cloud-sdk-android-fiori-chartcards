package com.example.fiorichartcardsapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CovidDataNetwork {
    @GET(STATES_CURRENT_ROUTE)
    suspend fun getStatesCurrent(): List<NetworkCovidDailyStates>

    @GET(US_DAILY_ROUTE)
    suspend fun getUsDaily(): List<NetworkCovidDailyUs>
}

private val service: CovidDataNetwork by lazy {

    val logging = HttpLoggingInterceptor()

    logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)

    val httpClient = OkHttpClient.Builder()

    httpClient.addInterceptor(logging) // <-- this is the important line!

    val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

    retrofit.create(CovidDataNetwork::class.java)
}

fun getNetworkService() = service

const val BASE_URL = "https://api.covidtracking.com/v1/"
const val US_DAILY_ROUTE = "us/daily.json"
const val STATES_CURRENT_ROUTE = "states/current.json"
