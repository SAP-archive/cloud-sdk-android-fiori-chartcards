package com.example.fiorichartcardsapp.domain

import kotlinx.serialization.*

@Serializable
data class CovidDataStateDaily(
    val date: Int,
    val state: String,
    val positive: Int?,
    val hospitalizedCurrently: Int?,
    val death: Int,
    val positiveIncrease: Int?,
    val deathIncrease: Int?,
    val hospitalizedIncrease: Int?
)
