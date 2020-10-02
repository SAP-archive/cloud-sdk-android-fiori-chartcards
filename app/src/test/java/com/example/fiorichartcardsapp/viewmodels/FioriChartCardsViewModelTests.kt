package com.example.fiorichartcardsapp.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.fiorichartcardsapp.repos.CovidDataRefreshError
import com.example.fiorichartcardsapp.repos.DefaultCovidDataRepository
import com.example.fiorichartcardsapp.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class FioriChartCardsViewModelTests {

    val context = ApplicationProvider.getApplicationContext<Context>()!!

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineScopeRule()

    private lateinit var subject: FioriChartCardsViewModel

    @Before
    fun setup() {
        val covidDailyUsDao = CovidDailyUsDaoFake(TestConstants.dbCovidDailyUsList)
        val covidDailyStatesDao = CovidDailyStatesDaoFake(TestConstants.dbCovidDailyStatesList)
        val networkCovidDailyUsList = TestConstants.networkCovidDailyUsList ?: listOf()
        val networkCovidDailyStatesList = TestConstants.networkCovidDailyStateList ?: listOf()
        val repo = DefaultCovidDataRepository(
            context = context as Application,
            network = CovidDataNetworkFake(networkCovidDailyUsList, networkCovidDailyStatesList),
            covidUsDao = covidDailyUsDao,
            covidStatesDao = covidDailyStatesDao,
            dispatchers = testCoroutineRule.testDispatcherProvider
        )
        subject = FioriChartCardsViewModel(repo, context)
    }

    @Test
    fun loadCovidDataByDefault() {
        assertEquals(2, subject.covidDataUsDailyList.getValueForTest()?.size)
        assertEquals(2, subject.covidDataStatesDailyList.getValueForTest()?.size)
        assertEquals(5, subject.covidUsChartCards.getValueForTest()?.size)
        assertEquals(4, subject.covidStatesChartCards.getValueForTest()?.size)
    }

    @Test
    fun whenCovidDataRefreshed_dataUpdated() = testCoroutineRule.runBlockingTest {
        val covidDailyUsDao = CovidDailyUsDaoFake(TestConstants.dbCovidDailyUsList)
        val covidDailyStatesDao = CovidDailyStatesDaoFake(TestConstants.dbCovidDailyStatesList)
        val networkCovidDailyUsList = TestConstants.networkCovidDailyUsList ?: listOf()
        val networkCovidDailyStatesList = TestConstants.networkCovidDailyStateList ?: listOf()
        val repo = DefaultCovidDataRepository(
            context = context as Application,
            network = CovidDataNetworkFake(networkCovidDailyUsList, networkCovidDailyStatesList),
            covidUsDao = covidDailyUsDao,
            covidStatesDao = covidDailyStatesDao,
            dispatchers = testCoroutineRule.testDispatcherProvider
        )
        subject = FioriChartCardsViewModel(repo, context)
        assertEquals(2, subject.covidDataUsDailyList.getValueForTest()?.size)
        assertEquals(2, subject.covidDataStatesDailyList.getValueForTest()?.size)

        subject.refreshDataFromRepository(true)
        assertEquals(136, covidDailyUsDao.nextInsertedOrNull()?.size)
        assertEquals(10, covidDailyStatesDao.nextInsertedOrNull()?.size)

        subject.refreshDataFromRepository(false)
        assertEquals(136, covidDailyUsDao.nextInsertedOrNull()?.size)
        assertEquals(10, covidDailyStatesDao.nextInsertedOrNull()?.size)
    }

    @Test
    fun whenErrorLiveDataRefresh_itShowsErrorText() = testCoroutineRule.runBlockingTest {
        val network = CovidDataNetworkCompletableFake()
        val covidDailyUsDao = CovidDailyUsDaoFake(TestConstants.dbCovidDailyUsList)
        val covidDailyStatesDao = CovidDailyStatesDaoFake(TestConstants.dbCovidDailyStatesList)
        val repo = DefaultCovidDataRepository(
            context = context as Application,
            network = network,
            covidUsDao = covidDailyUsDao,
            covidStatesDao = covidDailyStatesDao,
            dispatchers = testCoroutineRule.testDispatcherProvider
        )
        subject = FioriChartCardsViewModel(repo, context)
        assertEquals(2, subject.covidDataUsDailyList.getValueForTest()?.size)
        assertEquals(2, subject.covidDataStatesDailyList.getValueForTest()?.size)
        assertEquals(false, subject.eventNetworkError.getValueForTest())
        subject.refreshDataFromRepository(true)
        network.sendErrorToCurrentRequests(makeErrorResult("An error"))
        assertEquals(true, subject.eventNetworkError.getValueForTest())
        assertEquals(false, subject.isNetworkErrorShown.getValueForTest())
        subject.onNetworkErrorShown()
        assertEquals(true, subject.isNetworkErrorShown.getValueForTest())
    }

    private fun makeErrorResult(result: String): CovidDataRefreshError {
        return CovidDataRefreshError(result,
            HttpException(Response.error<String>(
            500,
                "\"$result\"".toResponseBody("application/json".toMediaType())
            )
            ))

    }
}