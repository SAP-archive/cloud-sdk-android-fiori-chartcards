package com.example.fiorichartcardsapp.repos

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.fiorichartcardsapp.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DefaultCovidDataRepositoryTests {

    private val context: Context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineScopeRule()

    @Test
    fun whenRefreshCovidDataSuccess_insertsRows() = runBlockingTest {
        val covidDailyUsDao = CovidDailyUsDaoFake(TestConstants.dbCovidDailyUsList)
        val covidDailyStatesDao = CovidDailyStatesDaoFake(TestConstants.dbCovidDailyStatesList)
        val networkCovidDailyUsList = TestConstants.networkCovidDailyUsList ?: listOf()
        val networkCovidDailyStatesList = TestConstants.networkCovidDailyStateList ?: listOf()
        val subject = DefaultCovidDataRepository(
            context = context as Application,
            network = CovidDataNetworkFake(networkCovidDailyUsList, networkCovidDailyStatesList),
            covidUsDao = covidDailyUsDao,
            covidStatesDao = covidDailyStatesDao,
            dispatchers = testCoroutineRule.testDispatcherProvider
        )

        subject.refreshCovidData(true)
        val resultUs = covidDailyUsDao.nextInsertedOrNull()
        assertEquals(136, resultUs?.size)
        val resultStates = covidDailyStatesDao.nextInsertedOrNull()
        assertEquals(10, resultStates?.size)
    }

    @Test (expected = CovidDataRefreshError::class)
    fun whenRefreshCovidDataTimeout_throws() = testCoroutineRule.runBlockingTest {

            val network = CovidDataNetworkCompletableFake()
            val covidDailyUsDao = CovidDailyUsDaoFake(TestConstants.dbCovidDailyUsList)
            val covidDailyStatesDao = CovidDailyStatesDaoFake(TestConstants.dbCovidDailyStatesList)
            val subject = DefaultCovidDataRepository(
                context = context as Application,
                network = network,
                covidUsDao = covidDailyUsDao,
                covidStatesDao = covidDailyStatesDao,
                dispatchers = testCoroutineRule.testDispatcherProvider
            )

            //Use `async` to start the coroutine for timeout test.
            val error = async {
                subject.refreshCovidData(true)
            }
            advanceTimeBy(5_000)
            //call .await() on a deferred value to get its eventual result. An exception should be expected here.
            error.await()
        }
}
