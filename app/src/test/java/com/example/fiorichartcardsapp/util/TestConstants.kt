package com.example.fiorichartcardsapp.util

import com.example.fiorichartcardsapp.api.NetworkCovidDailyStates
import com.example.fiorichartcardsapp.api.NetworkCovidDailyUs
import com.example.fiorichartcardsapp.database.DatabaseCovidDailyState
import com.example.fiorichartcardsapp.database.DatabaseCovidDailyUs
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json

class TestConstants{
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

        val dbCovidDailyStatesList = listOf(dbCovidDailyStates1, dbCovidDailyStates2)

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

        val dbCovidDailyUsList = listOf(dbCovidDailyUs1, dbCovidDailyUs2)

        @OptIn(UnstableDefault::class)
        val networkCovidDailyStateList = readFile("states_current.json")?.trimIndent()?.let {
            Json.parse(NetworkCovidDailyStates.serializer().list, it)
        }

        @OptIn(UnstableDefault::class)
        val networkCovidDailyUsList = readFile("us_daily.json")?.trimIndent()?.let {
            Json.parse(NetworkCovidDailyUs.serializer().list, it)
        }

        private fun readFile(name : String) : String? {
            return this::class.java.classLoader?.getResource(name)?.readText()
        }


    }
}