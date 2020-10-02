package com.example.fiorichartcardsapp.repos

import androidx.lifecycle.LiveData
import com.example.fiorichartcardsapp.domain.CovidDataStateDaily
import com.example.fiorichartcardsapp.domain.CovidDataUsDaily

interface CovidDataRepository {
    val covidDailyStates: LiveData<List<CovidDataStateDaily>>
    val covidDailyUs: LiveData<List<CovidDataUsDaily>>
    suspend fun refreshCovidData(useNetworkData: Boolean)
}