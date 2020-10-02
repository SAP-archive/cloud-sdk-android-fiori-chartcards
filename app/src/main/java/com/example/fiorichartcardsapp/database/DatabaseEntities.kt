package com.example.fiorichartcardsapp.database

import androidx.room.Entity
import com.example.fiorichartcardsapp.domain.CovidDataStateDaily
import com.example.fiorichartcardsapp.domain.CovidDataUsDaily

@Entity(primaryKeys = ["date"])
data class DatabaseCovidDailyUs constructor(
    val date: Int,
    val states: Int,
    val positive: Int?,
    val hospitalizedCumulative: Int?,
    val death: Int?,
    val positiveIncrease: Int?,
    val deathIncrease: Int?,
    val hospitalizedIncrease: Int?
)

@Entity(primaryKeys = ["date", "state"])
data class DatabaseCovidDailyState constructor(
    val date: Int,
    val state: String,
    val positive: Int?,
    val hospitalizedCurrently: Int?,
    val death: Int,
    val positiveIncrease: Int?,
    val deathIncrease: Int?,
    val hospitalizedIncrease: Int?
)


/**
 * Map DatabaseCovidDailyState and DatabaseCovidDailyUs to domain entities
 */
fun List<DatabaseCovidDailyState>.asDomainStateModel(): List<CovidDataStateDaily> {
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

fun List<DatabaseCovidDailyUs>.asDomainUsModel(): List<CovidDataUsDaily> {
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

