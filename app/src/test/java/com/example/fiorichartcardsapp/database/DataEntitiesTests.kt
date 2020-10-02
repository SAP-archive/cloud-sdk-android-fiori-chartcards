package com.example.fiorichartcardsapp.database

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.fiorichartcardsapp.util.TestCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.runBlockingTest

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DataEntitiesTests {

    private val context: Context = ApplicationProvider.getApplicationContext<Context>()

    private lateinit var statesDao: CovidDailyStatesDao
    private lateinit var usDao: CovidDailyUsDao
    private lateinit var db: CovidDataBase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineScopeRule()

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(context, CovidDataBase::class.java)
            .setTransactionExecutor(testCoroutineRule.dispatcher.asExecutor())
            .setQueryExecutor(testCoroutineRule.dispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()
        statesDao = db.covidStatesDao
        usDao = db.covidUsDao
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertCovidDailyStatesData() = testCoroutineRule.runBlockingTest {
        statesDao.insertAll(listOf(covidDataStateDaily1))
        statesDao.getCovidDailyStates().value?.isNotEmpty()?.let { assert(it) }
        statesDao.getCovidDailyStates().value?.size?.equals(1)?.let { assert(it) }
    }

    @Test
    fun clearCovidDailyStatesData() = runBlockingTest {
        statesDao.insertAll(covidDataStateDailyList)
        statesDao.getCovidDailyStates().value?.isNotEmpty()?.let { assert(it) }
        statesDao.getCovidDailyStates().value?.size?.equals(2)?.let { assert(it) }

        statesDao.deleteAll()
        statesDao.getCovidDailyStates().value?.isEmpty()?.let { assert(it) }
    }

    @Test
    fun insertCovidDailyUsData() = runBlockingTest {
        usDao.insertAll(listOf(covidDataUsDaily2))
        usDao.getCovidDailyUs().value?.isNotEmpty()?.let { assert(it) }
        usDao.getCovidDailyUs().value?.size?.equals(1)?.let { assert(it) }
    }

    @Test
    fun clearCovidDailyUsData() = runBlockingTest{
        usDao.insertAll(covidDataUsDailyList)
        usDao.getCovidDailyUs().value?.isNotEmpty()?.let { assert(it) }
        usDao.getCovidDailyUs().value?.size?.equals(2)?.let { assert(it) }

        usDao.deleteAll()
        usDao.getCovidDailyUs().value?.isEmpty()?.let { assert(it) }
    }

    companion object {
        val covidDataStateDaily1 = DatabaseCovidDailyState(
            date = 20200701,
            state = "CA",
            positive = 100000,
            hospitalizedCurrently = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        val covidDataStateDaily2 = DatabaseCovidDailyState(
            date = 20200701,
            state = "NY",
            positive = 150000,
            hospitalizedCurrently = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        val covidDataStateDailyList = listOf(covidDataStateDaily1, covidDataStateDaily2)

        val covidDataUsDaily1 = DatabaseCovidDailyUs(
            date = 20200701,
            states = 56,
            positive = 100000,
            hospitalizedCumulative = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        val covidDataUsDaily2 = DatabaseCovidDailyUs(
            date = 20200701,
            states = 56,
            positive = 150000,
            hospitalizedCumulative = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        val covidDataUsDailyList = listOf(covidDataUsDaily1, covidDataUsDaily2)
    }
}