package com.example.fiorichartcardsapp.repos

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.fiorichartcardsapp.api.*
import com.example.fiorichartcardsapp.database.*
import com.example.fiorichartcardsapp.domain.CovidDataStateDaily
import com.example.fiorichartcardsapp.domain.CovidDataUsDaily
import com.example.fiorichartcardsapp.utilities.CovidDataBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json

/**
 * Default implementation of CovidDataRepository
 */
class DefaultCovidDataRepository (
    val context: Application,
    val network: CovidDataNetwork,
    val covidUsDao: CovidDailyUsDao,
    val covidStatesDao: CovidDailyStatesDao,
    val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : CovidDataRepository {

    /**
     * Transforms the list of DatabaseCovidDailyState objects into a list of CovidDataStateDaily domain objects.
     */
    override val covidDailyStates: LiveData<List<CovidDataStateDaily>> = Transformations.map(
        covidStatesDao.getCovidDailyStates()) {
        it.asDomainStateModel()
    }

    /**
     * Transforms the list of DatabaseCovidDailyUs objects into a list of CovidDataUsDaily domain objects.
     */
    override val covidDailyUs: LiveData<List<CovidDataUsDaily>> = Transformations.map(
        covidUsDao.getCovidDailyUs()) {
        it.asDomainUsModel()
    }

    /**
     * Coroutine to refresh the Covid data either from the network API or from the stored local files
     *
     * @param useNetworkData Boolean
     */
    @OptIn(UnstableDefault::class)
    override suspend fun refreshCovidData(useNetworkData: Boolean) {
        // Move the execution of the coroutine to the I/O dispatcher
        withContext(dispatchers.io()) {
            try {
                val covidDailyStatesList: List<DatabaseCovidDailyState>
                val covidDailyUsList: List<DatabaseCovidDailyUs>
                covidStatesDao.deleteAll()
                covidUsDao.deleteAll()
                if (useNetworkData) {
                    covidDailyStatesList = withTimeout(5_000) {
                        network.getStatesCurrent().map { it.asDatabaseModel() }
                        }
                    covidDailyUsList = withTimeout(5_000){
                        network.getUsDaily().map { it.asDatabaseModel() }
                        }
                } else {
                    val jsonStringCovidStates = CovidDataBuilder.getJsonDataFromAsset(context, "states_current.json")
                    covidDailyStatesList = jsonStringCovidStates?.let { Json.parse(NetworkCovidDailyStates.serializer().list, it) }!!
                        .map { it.asDatabaseModel() }
                    val jsonStringCovidUs = CovidDataBuilder.getJsonDataFromAsset(context, "us_daily.json")
                    covidDailyUsList = jsonStringCovidUs?.let { Json.parse(NetworkCovidDailyUs.serializer().list, it) }!!
                        .map { it.asDatabaseModel() }
                }
                covidStatesDao.insertAll(covidDailyStatesList)
                covidUsDao.insertAll(covidDailyUsList)
            } catch (error: Throwable) {
                throw  CovidDataRefreshError("Unable to refresh COVID-19 data", error)
            }
        }
    }
}

interface DispatcherProvider {
    fun main(): CoroutineDispatcher = Dispatchers.Main
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = Dispatchers.IO
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}

class DefaultDispatcherProvider : DispatcherProvider

/**
* Thrown when there was an error fetching the data
*
* @property message user ready error message
* @property cause the original cause of this exception
*/
class CovidDataRefreshError(message: String, cause: Throwable) : Throwable(message, cause)