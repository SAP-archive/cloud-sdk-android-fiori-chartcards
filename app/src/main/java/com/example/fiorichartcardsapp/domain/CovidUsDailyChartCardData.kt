package com.example.fiorichartcardsapp.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fiorichartcardsapp.utilities.DataFormatter
import com.sap.cloud.mobile.fiori.chartcard.ChartCardDataModel

@RequiresApi(Build.VERSION_CODES.O)
class CovidUsDailyChartCardData(
    plotType: Int,
    chartCardTitle: String?,
    chartCardTimestamp: String?,
    chartCardSummary: ChartCardSummary?,
    chartCardTrend: ChartCardTrend?,
    xLabels: MutableList<String?>?,
    yLabels: MutableList<String?>?,
    noDataText: ChartCardNoDataText?,
    dataSetNames: Array<String>,
    covidDataUsDaily: List<CovidDataUsDaily>?
)
    : ChartCardDataModel(plotType, chartCardTitle, chartCardTimestamp, chartCardSummary, chartCardTrend, xLabels, yLabels, noDataText) {

    private var sortedList = covidDataUsDaily?.reversed()
    private var includeTotal = false
    private var includeDeaths = false
    private var includeHospitalized = false
    private var numberOfDataPoints = sortedList?.size ?: 12

    /* add your own initialization method to populate the plot data set.*/
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initializePlotDataSet(numberOfDataSets: Int) {
        if (!includeTotal && !includeDeaths && !includeHospitalized)
            includeTotal = true
        val totalCases = FloatArray(numberOfDataPoints)
        val totalDeaths = FloatArray(numberOfDataPoints)
        val hospitalized = FloatArray(numberOfDataPoints)
        xLabels = mutableListOf()

        chartCardTimestamp = "As of ${sortedList?.get(0)?.date?.let { DataFormatter.formatDateForXLabel(it) }}"
        for (i in 0 until numberOfDataPoints) {
            if (includeTotal)
                totalCases[i] = sortedList?.get(i)?.positiveIncrease!!.toFloat()
            if (includeDeaths)
                totalDeaths[i] = sortedList?.get(i)?.deathIncrease!!.toFloat()
            if (includeHospitalized)
                hospitalized[i] = sortedList?.get(i)?.hospitalizedIncrease!!.toFloat()
            xLabels!!.add(i, sortedList?.get(i)?.date?.let { DataFormatter.formatDateForXLabel(it) })
        }
        if (includeTotal)
            plotDataSet!![TOTAL_CASES] = totalCases
        if (includeDeaths)
            plotDataSet!![DEATHS] = totalDeaths
        if (includeHospitalized)
            plotDataSet!![HOSPITALIZED] = hospitalized
        xLabels = xLabels!!
        limitXLabels = false

    }

    private fun setSummaryAndTrend() {
        val today = sortedList!![0]
        val totalCases = today.positive
        val totalIncrease = today.positiveIncrease
        val totalDeath = today.death
        val totalDeathIncrease = today.deathIncrease
        val totalHospitalized = today.hospitalizedCumulative
        val totalHospitalizedIncrease = today.hospitalizedIncrease

        if (includeTotal) {
            chartCardSummary = ChartCardSummary(totalCases.toString(), "Total: ", "")
            if (totalIncrease != null) {
                chartCardTrend = ChartCardTrend(totalIncrease > 0,
                    (if (totalIncrease > 0) "+ " else "") + totalIncrease.toString())
            }
        } else {
            if (includeDeaths) {
                chartCardSummary = ChartCardSummary(totalDeath.toString(), "Total Deaths: ", "")
                if (totalDeathIncrease != null) {
                    chartCardTrend = ChartCardTrend(totalDeathIncrease > 0,
                        (if (totalDeathIncrease > 0) "+ " else "") + totalDeathIncrease.toString())
                }
            } else if (includeHospitalized) {
                chartCardSummary = ChartCardSummary(totalHospitalized.toString(), "Total Hospitalized: ", "")
                if (totalHospitalizedIncrease != null) {
                    chartCardTrend = ChartCardTrend(totalHospitalizedIncrease > 0,
                        (if (totalHospitalizedIncrease > 0) "+ " else "") + totalHospitalizedIncrease.toString())
                }
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
