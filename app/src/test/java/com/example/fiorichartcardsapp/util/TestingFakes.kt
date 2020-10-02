package com.example.fiorichartcardsapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fiorichartcardsapp.api.CovidDataNetwork
import com.example.fiorichartcardsapp.api.NetworkCovidDailyStates
import com.example.fiorichartcardsapp.api.NetworkCovidDailyUs
import com.example.fiorichartcardsapp.database.CovidDailyStatesDao
import com.example.fiorichartcardsapp.database.CovidDailyUsDao
import com.example.fiorichartcardsapp.database.DatabaseCovidDailyState
import com.example.fiorichartcardsapp.database.DatabaseCovidDailyUs
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * Fake [CovidDailyUsDao] for use in tests.
 */
class CovidDailyUsDaoFake(covidDailyUsList: List<DatabaseCovidDailyUs>) : CovidDailyUsDao {
    /**
     * A channel is a Coroutine based implementation of a blocking queue.
     *
     * We're using it here as a buffer of inserted elements.
     *
     * This uses a channel instead of a list to allow multiple threads to call insertAll and
     * synchronize the results with the test thread.
     */
    private val insertedAll = Channel<List<DatabaseCovidDailyUs>>(capacity = Channel.BUFFERED)

    override suspend fun insertAll(covidDailyUsList: List<DatabaseCovidDailyUs>) {
        insertedAll.send(covidDailyUsList)
        _covidDailyUsList.postValue(covidDailyUsList)
    }

    override suspend fun deleteAll() {
        _covidDailyUsList = MutableLiveData()
    }

    private var _covidDailyUsList = MutableLiveData<List<DatabaseCovidDailyUs>>(covidDailyUsList)

    override fun getCovidDailyUs(): LiveData<List<DatabaseCovidDailyUs>> {
        return _covidDailyUsList
    }

    fun nextInsertedOrNull(timeout: Long = 2_000): List<DatabaseCovidDailyUs>? {
        var result: List<DatabaseCovidDailyUs>? = null
        runBlocking {
            // wait for the next insertion to complete
            try {
                withTimeout(timeout) {
                    result = insertedAll.receive().toList()
                }
            } catch (ex: TimeoutCancellationException) {
                // ignore
            }
        }
        return result
    }
}

/**
 * Fake [CovidDailyUsDao] for use in tests.
 */
class CovidDailyStatesDaoFake(initialStatesList: List<DatabaseCovidDailyState>) : CovidDailyStatesDao {

    private val insertedAll = Channel<List<DatabaseCovidDailyState>>(capacity = Channel.BUFFERED)

    override suspend fun insertAll(statesList: List<DatabaseCovidDailyState>) {
        insertedAll.send(statesList)
        _covidDailyStatesList.postValue(statesList)
    }

    override suspend fun deleteAll() {
        _covidDailyStatesList = MutableLiveData()
    }

    private var _covidDailyStatesList = MutableLiveData<List<DatabaseCovidDailyState>>(initialStatesList)

    override fun getCovidDailyStates(): LiveData<List<DatabaseCovidDailyState>> {
        return _covidDailyStatesList
    }

    fun nextInsertedOrNull(timeout: Long = 2_000): List<DatabaseCovidDailyState>? {
        var result: List<DatabaseCovidDailyState>? = null
        runBlocking {
            // wait for the next insertion to complete
            try {
                withTimeout(timeout) {
                    result = insertedAll.receive().toList()
                }
            } catch (ex: TimeoutCancellationException) {
                // ignore
            }
        }
        return result
    }
}

/**
 * Testing Fake implementation of [CovidDataNetwork]
 */
class CovidDataNetworkFake(
    var covidDailyUsList: List<NetworkCovidDailyUs>,
    var covidDailyStatesList: List<NetworkCovidDailyStates>) : CovidDataNetwork {

    override suspend fun getStatesCurrent() = covidDailyStatesList

    override suspend fun getUsDaily() = covidDailyUsList
}

/**
 * Testing Fake for [CovidDataNetwork] that lets you complete or error all current requests
 */
class CovidDataNetworkCompletableFake : CovidDataNetwork {
    private var completableUs = CompletableDeferred<List<NetworkCovidDailyUs>>()
    private var completableStates = CompletableDeferred<List<NetworkCovidDailyStates>>()

    fun sendCompletionToAllCurrentRequests(
        covidDailyUsList: List<NetworkCovidDailyUs>,
        covidDailyStatesList: List<NetworkCovidDailyStates>) {
        completableUs.complete(covidDailyUsList)
        completableUs = CompletableDeferred()

        completableStates.complete(covidDailyStatesList)
        completableStates = CompletableDeferred()
    }

    fun sendErrorToCurrentRequests(throwable: Throwable) {
        completableUs.completeExceptionally(throwable)
        completableUs = CompletableDeferred()

        completableStates.completeExceptionally(throwable)
        completableStates = CompletableDeferred()
    }

    override suspend fun getStatesCurrent(): List<NetworkCovidDailyStates> = completableStates.await()

    override suspend fun getUsDaily(): List<NetworkCovidDailyUs> = completableUs.await()

}