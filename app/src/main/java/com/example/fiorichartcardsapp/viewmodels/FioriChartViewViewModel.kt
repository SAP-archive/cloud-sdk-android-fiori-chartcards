package com.example.fiorichartcardsapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.fiorichartcardsapp.domain.DetailChartData

class FioriChartViewViewModel (
    application: Application,
    detailChartData: DetailChartData) : AndroidViewModel(application) {

    private val _selectedChartCard: MutableLiveData<DetailChartData> = MutableLiveData()
    val selectChartCard: LiveData<DetailChartData>
        get() = _selectedChartCard

    init {
        _selectedChartCard.postValue(detailChartData)
    }

    /**
     * Factory for constructing FioriChartViewViewModel with parameter
     */
    class Factory(val app: Application, private val detailChartData: DetailChartData) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FioriChartViewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FioriChartViewViewModel(app, detailChartData) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}