package com.example.fiorichartcardsapp

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.fiorichartcardsapp.repos.CovidDataRepository
import com.example.fiorichartcardsapp.repos.CovidDataRepositoryFake
import com.example.fiorichartcardsapp.utilities.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device or emulator.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class ChartCardsFragmentTests {

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext!!

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

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
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.fiorichartcardsapp", appContext.packageName)
    }

    @Test
    fun createBaseCardsFragment() = runBlockingTest {
        repository.refreshCovidData(true)
        val scenario = launchFragmentInContainer<BaseCardsFragment>(themeResId= R.style.SampleAppTheme)
        onView(withId(R.id.chartCardView)).check(matches(isDisplayed()))
    }

    @Test
    fun createScrollableCardsFragment() = runBlockingTest {
        repository.refreshCovidData(true)
        val scenario = launchFragmentInContainer<ScrollableCardsFragment>(themeResId= R.style.SampleAppTheme)
        onView(withId(R.id.chartCardViewScrollable)).check(matches(isDisplayed()))
    }

    @Test
    fun clickBaseCard_navigateToCardDetailsFragment() = runBlockingTest {
        repository.refreshCovidData(true)

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.chartcards_navigation)

        val scenario = launchFragmentInContainer<BaseCardsFragment>(themeResId= R.style.SampleAppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.chart_card_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        assertEquals(R.id.card_details_fragment, navController.currentDestination?.id)
    }

    @Test
    fun clickScrollableCard_navigateToCardDetailsFragment() = runBlockingTest {
        repository.refreshCovidData(true)

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.chartcards_navigation)

        val scenario = launchFragmentInContainer<ScrollableCardsFragment>(themeResId= R.style.SampleAppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.chart_card_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        assertEquals(R.id.card_details_fragment, navController.currentDestination?.id)
    }

    @Test
    fun countChartCards_inBaseCardsFragment() = runBlockingTest {
        repository.refreshCovidData(true)
        val scenario = launchFragmentInContainer<BaseCardsFragment>(themeResId= R.style.SampleAppTheme)
        onView(withId(R.id.chart_card_recycler_view))
            .check(matches(CustomMatchers.withItemCount(5) as Matcher<in View>?))
    }

    @Test
    fun countChartCards_inScrollableCardsFragment() = runBlockingTest {
        repository.refreshCovidData(true)
        val scenario = launchFragmentInContainer<ScrollableCardsFragment>(themeResId= R.style.SampleAppTheme)
        onView(withId(R.id.chart_card_recycler_view))
            .check(matches(CustomMatchers.withItemCount(4) as Matcher<in View>?))
    }

    class CustomMatchers {
        companion object {
            fun withItemCount(count: Int): Any {
                return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                    override fun describeTo(description: Description?) {
                        description?.appendText("RecyclerView with item count: $count")
                    }

                    override fun matchesSafely(item: RecyclerView?): Boolean {
                        return item?.adapter?.itemCount == count
                    }
                }
            }
        }
    }

    class CustomAssertions {
        companion object {
            fun hasItemCount(count: Int): ViewAssertion {
                return RecyclerViewItemCountAssertion(count)
            }
        }

        private class RecyclerViewItemCountAssertion(private val count: Int) : ViewAssertion {

            override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
                if (noViewFoundException != null) {
                    throw noViewFoundException
                }

                if (view !is RecyclerView) {
                    throw IllegalStateException("The asserted view is not RecyclerView")
                }

                if (view.adapter == null) {
                    throw IllegalStateException("No adapter is assigned to RecyclerView")
                }

                assertThat("RecyclerView item count", view.adapter!!.itemCount, CoreMatchers.equalTo(count))
            }
        }
    }
}
