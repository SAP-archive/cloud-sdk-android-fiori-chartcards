package com.example.fiorichartcardsapp.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.fiorichartcardsapp.database.CovidDailyStatesDaoFake
import com.example.fiorichartcardsapp.database.CovidDailyUsDaoFake
import com.example.fiorichartcardsapp.database.asDomainStateModel
import com.example.fiorichartcardsapp.database.asDomainUsModel
import com.example.fiorichartcardsapp.domain.CovidDataStateDaily
import com.example.fiorichartcardsapp.domain.CovidDataUsDaily
import com.example.fiorichartcardsapp.util.AndroidTestConstants

/**
 * Fake [CovidDataRepository] for use in tests.
 */
class CovidDataRepositoryFake: CovidDataRepository {

    val covidUsDaoFake = AndroidTestConstants.dbCovidDailyUsList?.let { CovidDailyUsDaoFake(it) }
    val covidStatesDaoFake = AndroidTestConstants.dbCovidDailyStatesList?.let { CovidDailyStatesDaoFake(it) }

    override val covidDailyStates: LiveData<List<CovidDataStateDaily>> = Transformations.map(
        covidStatesDaoFake!!.getCovidDailyStates()) {
        it.asDomainStateModel()
    }

    override val covidDailyUs: LiveData<List<CovidDataUsDaily>> = Transformations.map(
        covidUsDaoFake!!.getCovidDailyUs()) {
        it.asDomainUsModel()
    }

    override suspend fun refreshCovidData(useNetworkData: Boolean) {
        covidStatesDaoFake!!.deleteAll()
        covidUsDaoFake!!.deleteAll()
        AndroidTestConstants.dbCovidDailyStatesList?.let { covidStatesDaoFake.insertAll(it) }
        AndroidTestConstants.dbCovidDailyUsList?.let { covidUsDaoFake.insertAll(it) }
    }

}