package com.example.fiorichartcardsapp.domain

import kotlinx.serialization.Serializable

@Serializable
data class CovidDataUsDaily (
    val date: Int,
    val states: Int,
    val positive: Int?,
    val hospitalizedCumulative: Int?,
    val death: Int?,
    val deathIncrease: Int?,
    val hospitalizedIncrease: Int?,
    val positiveIncrease: Int?
)