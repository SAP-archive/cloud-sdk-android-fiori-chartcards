package com.example.fiorichartcardsapp

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.fiorichartcardsapp.repos.CovidDataRepository
import com.example.fiorichartcardsapp.repos.CovidDataRepositoryFake
import com.example.fiorichartcardsapp.util.AndroidTestConstants
import com.example.fiorichartcardsapp.utilities.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CardDetailsFragmentTests {

    private lateinit var repository: CovidDataRepository

    @Before
    fun initRepository() {
        repository = CovidDataRepositoryFake()
        ServiceLocator.covidDataRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun lineChartCardDetailsFragment() = runBlockingTest {
        repository.refreshCovidData(true)
        val detailCardData = AndroidTestConstants.lineChartCardDetailChartData
        val fragmentArgs = Bundle().apply {
            putParcelable("chartViewData", detailCardData)
        }
        val scenario = launchFragmentInContainer<CardDetailsFragment>(
            fragmentArgs, themeResId= R.style.SampleAppTheme)
        onView(withId(R.id.card_details_title)).check(matches(withText("Card Details")))
        onView(allOf(withId(R.id.line_chart), withParent(withId(R.id.line_chart_view)))).check(matches(isDisplayed()))
    }

    @Test
    fun columnChartCardDetailsFragment() = runBlockingTest {
        repository.refreshCovidData(true)
        val detailCardData = AndroidTestConstants.columnChartCardDetailChartData
        val fragmentArgs = Bundle().apply {
            putParcelable("chartViewData", detailCardData)
        }
        val scenario = launchFragmentInContainer<CardDetailsFragment>(
            fragmentArgs, themeResId= R.style.SampleAppTheme)
        onView(withId(R.id.card_details_title)).check(matches(withText("Card Details")))
        onView(withId(R.id.column_chart_view)).check(matches(isDisplayed()))
    }

    @Test
    fun barChartCardDetailsFragment() = runBlockingTest {
        repository.refreshCovidData(true)
        val detailCardData = AndroidTestConstants.barChartCardDetailChartData
        val fragmentArgs = Bundle().apply {
            putParcelable("chartViewData", detailCardData)
        }
        val scenario = launchFragmentInContainer<CardDetailsFragment>(
            fragmentArgs, themeResId= R.style.SampleAppTheme)
        onView(withId(R.id.card_details_title)).check(matches(withText("Card Details")))
        onView(withId(R.id.bar_chart_view)).check(matches(isDisplayed()))
    }
}