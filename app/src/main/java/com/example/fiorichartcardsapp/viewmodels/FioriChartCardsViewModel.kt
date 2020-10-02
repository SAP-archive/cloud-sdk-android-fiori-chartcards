package com.example.fiorichartcardsapp.viewmodels

import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.example.fiorichartcardsapp.domain.*
import com.example.fiorichartcardsapp.repos.CovidDataRefreshError
import com.example.fiorichartcardsapp.repos.CovidDataRepository
import com.example.fiorichartcardsapp.utilities.DataFormatter
import com.sap.cloud.mobile.fiori.chartcard.ChartCardDataModel
import kotlinx.coroutines.launch

/**
 * The Chart Cards view model which provides data for BaseCardsFragement and ScrollableCardsFragment.
 */
class FioriChartCardsViewModel(
    private val covidDataRepository: CovidDataRepository,
    val application: Application) : ViewModel() {

    private var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    /**
     * List of CovidDataStatesDaily obtained from view model's CovidDataRepository
     */
    val covidDataStatesDailyList = covidDataRepository.covidDailyStates

    /**
     * List of CovidDataUsDaily obtained from view model's CovidDataRepository
     */
    val covidDataUsDailyList = covidDataRepository.covidDailyUs

    /**
     * List of CovidUsDailyChartCardData (i.e. ChartCardDataModel) transformed from list of CovidDataUsDaily
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val covidUsChartCards: LiveData<MutableList<ChartCardDataModel>> = Transformations.map(
        covidDataUsDailyList){
            if (it.isNotEmpty())
                configureUsChartCards(it)
            else
                mutableListOf()
        }

    /**
     * List of X label strings for US Data chart cards (displayed on BaseCardsFragment)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val xLabels4UsData: LiveData<MutableList<String>> = Transformations.map(
        covidDataUsDailyList) {
            if (it.isNotEmpty())
                prepareXLabels4UsData(it)
            else
                mutableListOf()
    }

    /**
     * List of CovidStatesCurrentChartCardData (i.e. ChartCardDataModel) transformed from list of CovidDataStateDaily
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val covidStatesChartCards: LiveData<MutableList<ChartCardDataModel>> = Transformations.map(
        covidDataStatesDailyList){
        if (it.isNotEmpty())
            configureStatesChartCards(it)
        else
            mutableListOf()
    }

    /**
     * List of X label strings for States Current chart cards (displayed on ScrollableCardsFragment)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val xLabels4StatesData: LiveData<MutableList<String>> = Transformations.map(
        covidDataStatesDailyList) {
        if (it.isNotEmpty())
            prepareXLabels4StatesData(it)
        else
            mutableListOf()
    }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        refreshDataFromRepository(sharedPreferences.getBoolean("useLiveData", true))
    }

    fun onRefresh() {
        refreshDataFromRepository(sharedPreferences.getBoolean("useLiveData", true))
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    fun refreshDataFromRepository(useLiveData: Boolean) = launchDataLoad {
        _dataLoading.value = true
        covidDataRepository.refreshCovidData(useLiveData)
    }

    private fun launchDataLoad(block: suspend () -> Unit): Unit {
        viewModelScope.launch {
            try {
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
                block()
            } catch (error: CovidDataRefreshError) {
                /* if(covidDataStatesDailyList.value.isNullOrEmpty()
                     || covidDataUsDailyList.value.isNullOrEmpty())*/
                _eventNetworkError.value = true
            } finally {
                _dataLoading.value = false
            }
        }
    }

    /**
     * The function to transform the list of CovidDataUsDaily objects into a list of ChartCardDataModel
     * that can be displayed in ChartCardView
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun configureUsChartCards(covidDataUsDailyList: List<CovidDataUsDaily>): MutableList<ChartCardDataModel> {
        val usChartCards = mutableListOf<ChartCardDataModel>()

        val chartCardUsDaily0 = CovidUsDailyChartCardData(ChartCardDataModel.COLUMN_CHART,
            "US COVID-19 Data",
            null, null, null,
            null, null, null,
            arrayOf(TOTAL_CASES, DEATHS, HOSPITALIZED), covidDataUsDailyList)
        usChartCards.add(chartCardUsDaily0)

        val chartCardUsDaily1 = CovidUsDailyChartCardData(ChartCardDataModel.LINE_CHART,
            "US COVID-19 Total Cases",
            null, null, null,
            null, null, null, arrayOf(TOTAL_CASES), covidDataUsDailyList)
        chartCardUsDaily1.limitXLabels = true
        usChartCards.add(chartCardUsDaily1)

        val chartCardUsDaily2 = CovidUsDailyChartCardData(ChartCardDataModel.HORIZONTAL_BAR_CHART,
            "US Deaths and Hospitalized",
            null, null, null,
            null, null, null, arrayOf(DEATHS, HOSPITALIZED), covidDataUsDailyList)
        usChartCards.add(chartCardUsDaily2)

        val chartCardUsDaily3 = CovidUsDailyChartCardData(ChartCardDataModel.COLUMN_CHART,
            "US COVID-19 Deaths",
            null, null, null,
            null, null, null, arrayOf(DEATHS), covidDataUsDailyList)
        usChartCards.add(chartCardUsDaily3)

        val chartCardUsDaily4 = CovidUsDailyChartCardData(ChartCardDataModel.LINE_CHART,
            "US COVID-19 Hospitalized",
            null, null, null,
            null, null, null, arrayOf(HOSPITALIZED), covidDataUsDailyList)
        chartCardUsDaily4.limitXLabels = true
        usChartCards.add(chartCardUsDaily4)

        return usChartCards
    }

    /**
     * Transforms the list of CovidDataUsDaily objects into a list of strings for X labels
     * displayed in ChartCardView and ChartView
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareXLabels4UsData(covidDataUsDailyList: List<CovidDataUsDaily>): MutableList<String> {
        val origXLabels = mutableListOf<String>()
        val sortedList = covidDataUsDailyList.reversed()
        val numberOfDataPoints = sortedList.size
        for (i in 0 until numberOfDataPoints) {
            origXLabels.add(i, sortedList[i].date.let { DataFormatter.formatDateForXLabel(it) })
        }
        return origXLabels
    }

    /**
     * The function to transform the list of CovidDataStateDaily objects into a list of ChartCardDataModel
     * that can be displayed in ChartCardView
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun configureStatesChartCards(covidDataStateDailyList: List<CovidDataStateDaily>): MutableList<ChartCardDataModel> {
        val statesChartCards = mutableListOf<ChartCardDataModel>()
        val covidDataStatesDaily = CovidDataStatesDaily(covidDataStateDailyList)

        val chartCardData0 = CovidStatesCurrentChartCardData(1, "Total Cases By States",
        null, null, null,
        null, null, null, arrayOf(TOTAL_CASES), covidDataStatesDaily)
        statesChartCards.add(chartCardData0)

        val chartCardData1 = CovidStatesCurrentChartCardData(2, "Total Cases By States",
            null, null, null,
            null, null, null, arrayOf(TOTAL_CASES), covidDataStatesDaily)
        statesChartCards.add(chartCardData1)

        val chartCardData2 = CovidStatesCurrentChartCardData(2, "Deaths and Hospitalized By States",
            null, null, null,
            null, null, null,
            arrayOf(DEATHS, HOSPITALIZED), covidDataStatesDaily)
        statesChartCards.add(chartCardData2)

        val chartCardData3 = CovidStatesCurrentChartCardData(1, "Deaths and Hospitalized By States",
            null, null, null,
            null, null, null,
            arrayOf(DEATHS, HOSPITALIZED), covidDataStatesDaily)
        statesChartCards.add(chartCardData3)

        return statesChartCards
    }

    /**
     * Transforms the list of CovidDataStateDaily objects into a list of strings for X labels
     * displayed in ChartCardView and ChartView
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareXLabels4StatesData(covidDataStateDailyList: List<CovidDataStateDaily>): MutableList<String> {
        val origXLabels = mutableListOf<String>()
        val sortedList = covidDataStateDailyList.sortedByDescending { it.positive }
        val numberOfDataPoints = sortedList.size
        for (i in 0 until numberOfDataPoints) {
            origXLabels.add(i, sortedList[i].state)
        }
        return origXLabels
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Factory for constructing FioriChartCardsViewModel with parameter
     */
    class Factory(val covidDataRepository: CovidDataRepository, val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FioriChartCardsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FioriChartCardsViewModel(covidDataRepository, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    companion object {
        const val TOTAL_CASES = "Total Cases"
        const val DEATHS = "Deaths"
        const val HOSPITALIZED = "Hospitalized"
    }
}
