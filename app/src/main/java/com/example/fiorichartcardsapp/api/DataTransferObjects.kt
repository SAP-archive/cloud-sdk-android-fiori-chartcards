package com.example.fiorichartcardsapp.api

import com.example.fiorichartcardsapp.database.DatabaseCovidDailyState
import com.example.fiorichartcardsapp.database.DatabaseCovidDailyUs
import com.example.fiorichartcardsapp.domain.CovidDataStateDaily
import com.example.fiorichartcardsapp.domain.CovidDataUsDaily
import kotlinx.serialization.*

@Serializable
data class NetworkCovidDailyStates (
    val date: Int,
    val state: String,
    val positive: Int?,
    val negative: Int?,
    val pending: Int?,
    val hospitalizedCurrently: Int?,
    val hospitalizedCumulative: Int?,
    val inIcuCurrently: Int?,
    val inIcuCumulative: Int?,
    val onVentilatorCurrently: Int?,
    val onVentilatorCumulative: Int?,
    val recovered: Int?,
    val dataQualityGrade: String,
    val lastUpdateEt: String,
    val dateModified: String,
    val checkTimeEt: String,
    val death: Int,
    val hospitalized: Int?,
    val dateChecked: String,
    val totalTestsViral: Int?,
    val positiveTestsViral: Int?,
    val negativeTestsViral: Int?,
    val positiveCasesViral: Int?,
    val fips: String,
    val positiveIncrease: Int?,
    val negativeIncrease: Int?,
    val total: Int?,
    val totalTestResults: Int?,
    val totalTestResultsIncrease: Int?,
    val posNeg: Int?,
    val deathIncrease: Int?,
    val hospitalizedIncrease: Int?,
    val hash: String,
    val commercialScore: Int?,
    val negativeRegularScore: Int?,
    val negativeScore: Int?,
    val positiveScore: Int?,
    val score: Int?,
    val grade: String?
)

fun NetworkCovidDailyStates.asDatabaseModel(): DatabaseCovidDailyState {
    return DatabaseCovidDailyState(
            date = date,
            state = state,
            positive = positive,
            hospitalizedCurrently = hospitalizedCurrently,
            death = death,
            positiveIncrease = positiveIncrease,
            deathIncrease = deathIncrease,
            hospitalizedIncrease = hospitalizedIncrease)
}

fun List<NetworkCovidDailyStates>.asDomainStateModel(): List<CovidDataStateDaily> {
    return map {
        CovidDataStateDaily(
            date = it.date,
            state = it.state,
            positive = it.positive,
            hospitalizedCurrently = it.hospitalizedCurrently,
            death = it.death,
            positiveIncrease = it.positiveIncrease,
            deathIncrease = it.deathIncrease,
            hospitalizedIncrease = it.hospitalizedIncrease
        )
    }
}

@Serializable
data class NetworkCovidDailyUs (
    val date: Int,
    val states: Int,
    val positive: Int?,
    val negative: Int?,
    val pending: Int?,
    val hospitalizedCurrently: Int?,
    val hospitalizedCumulative: Int?,
    val inIcuCurrently: Int?,
    val inIcuCumulative: Int?,
    val onVentilatorCurrently: Int?,
    val onVentilatorCumulative: Int?,
    val recovered: Int?,
    val dateChecked: String,
    val death: Int?,
    val hospitalized: Int?,
    val lastModified: String,
    val total: Int?,
    val totalTestResults: Int?,
    val posNeg: Int?,
    val deathIncrease: Int?,
    val hospitalizedIncrease: Int?,
    val positiveIncrease: Int?,
    val negativeIncrease: Int?,
    val totalTestResultsIncrease: Int?,
    val hash: String
)

fun NetworkCovidDailyUs.asDatabaseModel(): DatabaseCovidDailyUs {
    return DatabaseCovidDailyUs(
        date = date,
        states = states,
        positive = positive,
        hospitalizedCumulative = hospitalizedCumulative,
        death = death,
        positiveIncrease = positiveIncrease,
        deathIncrease = deathIncrease,
        hospitalizedIncrease = hospitalizedIncrease)
}

fun List<NetworkCovidDailyUs>.asDomainUsModel(): List<CovidDataUsDaily> {
    return map {
        CovidDataUsDaily(
            date = it.date,
            states = it.states,
            positive = it.positive,
            hospitalizedCumulative = it.hospitalizedCumulative,
            death = it.death,
            deathIncrease = it.deathIncrease,
            hospitalizedIncrease = it.hospitalizedIncrease,
            positiveIncrease = it.positiveIncrease
        )
    }
}