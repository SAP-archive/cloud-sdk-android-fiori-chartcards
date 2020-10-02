package com.example.fiorichartcardsapp.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.sap.cloud.mobile.fiori.chartcard.ChartCardDataModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CovidStatesCurrentChartCardData(
    plotType: Int,
    chartCardTitle: String?,
    chartCardTimestamp: String?,
    chartCardSummary: ChartCardSummary?,
    chartCardTrend: ChartCardTrend?,
    xLabels: MutableList<String?>?,
    yLabels: MutableList<String?>?,
    noDataText: ChartCardNoDataText?,
    dataSetNames: Array<String>,
    covidDataStatesDaily: CovidDataStatesDaily
)
    : ChartCardDataModel(plotType, chartCardTitle, chartCardTimestamp, chartCardSummary, chartCardTrend, xLabels, yLabels, noDataText) {

    private val sortedList = covidDataStatesDaily.covidDataStateDailyList.sortedByDescending { it.positive }
    private var includeTotal = false
    private var includeDeaths = false
    private var includeHospitalized = false

    /* add your own initialization method to populate the plot data set.*/
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initializePlotDataSet(numberOfDataSets: Int) {
        if (!includeTotal && !includeDeaths && !includeHospitalized)
            includeTotal = true
        val size = sortedList.size
        val totalCases = FloatArray(size)
        val totalDeaths = FloatArray(size)
        val hospitalized = FloatArray(size)
        xLabels = mutableListOf()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date = LocalDate.parse(sortedList[0].date.toString(), formatter)
        chartCardTimestamp = date.toString()
        for (i in 0 until size) {
            if (includeTotal)
                totalCases[i] = sortedList[i].positive!!.toFloat()
            if (includeDeaths) {
                totalDeaths[i] = (sortedList[i].death).toFloat()
            }
            if (includeHospitalized) {
                hospitalized[i] = ((sortedList[i].hospitalizedCurrently)?:0).toFloat()
            }
            xLabels!!.add(i, sortedList[i].state.toUpperCase())
        }
        if (includeTotal)
            plotDataSet!![TOTAL_CASES] = totalCases
        if (includeDeaths)
            plotDataSet!![DEATHS] = totalDeaths
        if (includeHospitalized)
            plotDataSet!![HOSPITALIZED] = hospitalized
        limitXLabels = false
    }

    fun setSummaryAndTrend() {
        var totalCases = 0
        var totalIncrease = 0
        var totalDeath = 0
        var totalDeathIncrease = 0
        var totalHospitalized = 0
        var totalHospitalizedIncrease = 0

        sortedList.forEach {
            totalCases += it.positive!!
            totalIncrease += it.positiveIncrease!!
            totalDeath += it.death
            totalDeathIncrease += it.deathIncrease!!
            totalHospitalized += it.hospitalizedCurrently ?: 0
            totalHospitalizedIncrease += it.hospitalizedIncrease ?: 0
        }
        if (includeTotal) {
            chartCardSummary = ChartCardSummary(totalCases.toString(), "Test Positive: ", "")
            chartCardTrend = ChartCardTrend(totalIncrease > 0,
                (if (totalIncrease > 0) "+ " else "") + totalIncrease.toString())
        } else {
            if (includeDeaths) {
                chartCardSummary = ChartCardSummary(totalDeath.toString(), "Total Deaths: ", "")
                chartCardTrend = ChartCardTrend(totalDeathIncrease > 0,
                    (if (totalDeathIncrease > 0) "+ " else "") + totalDeathIncrease.toString())
            } else if (includeHospitalized) {
                chartCardSummary = ChartCardSummary(totalHospitalized.toString(), "Total Hospitalized: ", "")
                chartCardTrend = ChartCardTrend(totalHospitalizedIncrease > 0,
                    (if (totalHospitalizedIncrease > 0) "+ " else "") + totalHospitalizedIncrease.toString())
            }
        }
    }

    init {
        if (dataSetNames.isEmpty()) {
            includeTotal = true
            initializePlotDataSet(1)
        } else {
            if (dataSetNames.contains(TOTAL_CASES))
                includeTotal = true
            if (dataSetNames.contains(DEATHS))
                includeDeaths = true
            if (dataSetNames.contains(HOSPITALIZED))
                includeHospitalized = true
            initializePlotDataSet(dataSetNames.size)
        }
        setSummaryAndTrend()
    }

    companion object {
        private const val TOTAL_CASES = "Total Cases"
        private const val DEATHS = "Deaths"
        private const val HOSPITALIZED = "Hospitalized"
    }
}
