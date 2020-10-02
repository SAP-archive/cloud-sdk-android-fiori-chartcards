package com.example.fiorichartcardsapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.channels.Channel

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
}

/**
 * Fake [CovidDailyUsDao] for use in tests.
 */
class CovidDailyStatesDaoFake(initialStatesList: List<DatabaseCovidDailyState>) : CovidDailyStatesDao {
    /**
     * A channel is a Coroutine based implementation of a blocking queue.
     *
     * We're using it here as a buffer of inserted elements.
     *
     * This uses a channel instead of a list to allow multiple threads to call insertAll and
     * synchronize the results with the test thread.
     */
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

}