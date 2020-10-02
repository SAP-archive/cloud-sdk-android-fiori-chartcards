package com.example.fiorichartcardsapp.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailChartData(
    val plotType: Int,
    var title: String,
    var timestamp: String,
    var xLabels: MutableList<String>,
    var plotDataSet: MutableMap<String, FloatArray>?
) : Parcelable
