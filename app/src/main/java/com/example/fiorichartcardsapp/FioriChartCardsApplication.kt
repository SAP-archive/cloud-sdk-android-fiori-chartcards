package com.example.fiorichartcardsapp

import android.app.Application
import com.example.fiorichartcardsapp.repos.CovidDataRepository
import com.example.fiorichartcardsapp.utilities.ServiceLocator

class FioriChartCardsApplication : Application() {

    val covidDataRepository: CovidDataRepository
        get() = ServiceLocator.provideCovidDataRepository(this)

}