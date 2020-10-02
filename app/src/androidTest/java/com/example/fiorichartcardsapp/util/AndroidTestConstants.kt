package com.example.fiorichartcardsapp.util

import androidx.test.platform.app.InstrumentationRegistry
import com.example.fiorichartcardsapp.api.*
import com.example.fiorichartcardsapp.database.DatabaseCovidDailyState
import com.example.fiorichartcardsapp.database.DatabaseCovidDailyUs
import com.example.fiorichartcardsapp.domain.*
import com.example.fiorichartcardsapp.utilities.DataFormatter
import com.example.fiorichartcardsapp.viewmodels.FioriChartCardsViewModel
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json

class AndroidTestConstants {
    companion object {
        val dbCovidDailyStates1 = DatabaseCovidDailyState(
            date = 20200701,
            state = "CA",
            positive = 100000,
            hospitalizedCurrently = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        val dbCovidDailyStates2 = DatabaseCovidDailyState(
            date = 20200701,
            state = "NY",
            positive = 150000,
            hospitalizedCurrently = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        //val dbCovidDailyStatesList = listOf<DatabaseCovidDailyState>(dbCovidDailyStates1, dbCovidDailyStates2)

        val dbCovidDailyUs1 = DatabaseCovidDailyUs(
            date = 20200701,
            states = 56,
            positive = 100000,
            hospitalizedCumulative = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        val dbCovidDailyUs2 = DatabaseCovidDailyUs(
            date = 20200701,
            states = 56,
            positive = 150000,
            hospitalizedCumulative = 2000,
            death = 300,
            positiveIncrease = 1000,
            deathIncrease = 30,
            hospitalizedIncrease = 200
        )

        //val dbCovidDailyUsList = listOf<DatabaseCovidDailyUs>(dbCovidDailyUs1, dbCovidDailyUs2)

        @OptIn(UnstableDefault::class)
        val networkCovidDailyStateList = readFile("states_current.json" )?.trimIndent()?.let {
            Json.parse(NetworkCovidDailyStates.serializer().list, it)
        }

        @OptIn(UnstableDefault::class)
        val networkCovidDailyUsList = readFile("us_daily.json")?.trimIndent()?.let {
            Json.parse(NetworkCovidDailyUs.serializer().list, it)
        }

        val dbCovidDailyUsList = networkCovidDailyUsList?.map { it.asDatabaseModel()}
        val dbCovidDailyStatesList = networkCovidDailyStateList?.map { it.asDatabaseModel() }

        val covidDailyStatesList = networkCovidDailyStateList?.asDomainStateModel()

        val covidDailyUsList = networkCovidDailyUsList?.asDomainUsModel()

        val xLabels4UsData = covidDailyUsList?.let { prepareXLabels4UsData(it) }
        val chartCardUsDaily0 = CovidUsDailyChartCardData(0, "US Total Cases",
            null, null, null,
            null, null, null, arrayOf(FioriChartCardsViewModel.TOTAL_CASES),
            covidDailyUsList)

        val chartCardUsDaily1 = CovidUsDailyChartCardData(2, "US Deaths and Hospitalized",
            null, null, null,
            null, null, null, arrayOf(FioriChartCardsViewModel.DEATHS, FioriChartCardsViewModel.HOSPITALIZED),
            covidDailyUsList)

        val chartCardUsDaily2 = CovidUsDailyChartCardData(1, "US COVID-19 Data",
            null, null, null,
            null, null, null, arrayOf(FioriChartCardsViewModel.DEATHS),
            covidDailyUsList)

        val covidDataStatesDaily = covidDailyStatesList?.let { CovidDataStatesDaily(it) }
        val chartCardStatesDaily1 = covidDataStatesDaily?.let {
            CovidStatesCurrentChartCardData(1, "Total Cases By States",
                null, null, null,
                null, null, null, arrayOf(FioriChartCardsViewModel.TOTAL_CASES), it)
        }

        val chartCardStatesDaily2 = covidDataStatesDaily?.let {
            CovidStatesCurrentChartCardData(2, "Most Deaths and Hospitalized By States",
                null, null, null,
                null, null, null,
                arrayOf(FioriChartCardsViewModel.DEATHS, FioriChartCardsViewModel.HOSPITALIZED), it)
        }

        val lineChartCardDetailChartData = DetailChartData(
            chartCardUsDaily0.plotType,
            chartCardUsDaily0.chartCardTitle!!,
            chartCardUsDaily0.chartCardTimestamp!!,
            xLabels4UsData?: mutableListOf(""),
            chartCardUsDaily0.plotDataSet!!
        )

        val columnChartCardDetailChartData = DetailChartData(
            chartCardUsDaily2.plotType,
            chartCardUsDaily2.chartCardTitle!!,
            chartCardUsDaily2.chartCardTimestamp!!,
            xLabels4UsData?: mutableListOf(""),
            chartCardUsDaily2.plotDataSet!!
        )

        val barChartCardDetailChartData = DetailChartData(
            chartCardUsDaily1.plotType,
            chartCardUsDaily1.chartCardTitle!!,
            chartCardUsDaily1.chartCardTimestamp!!,
            xLabels4UsData?: mutableListOf(""),
            chartCardUsDaily1.plotDataSet!!
        )
        private fun readFile(name: String): String? {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            return  appContext.assets.open(name).bufferedReader().use { it.readText() }

        }

        private fun prepareXLabels4UsData(covidDataUsDailyList: List<CovidDataUsDaily>): MutableList<String> {
            val origXLabels = mutableListOf<String>()
            val sortedList = covidDataUsDailyList.reversed()
            val numberOfDataPoints = sortedList.size
            for (i in 0 until numberOfDataPoints) {
                origXLabels.add(i, sortedList[i].date.let { DataFormatter.formatDateForXLabel(it) })
            }
            return origXLabels
        }
    }

}