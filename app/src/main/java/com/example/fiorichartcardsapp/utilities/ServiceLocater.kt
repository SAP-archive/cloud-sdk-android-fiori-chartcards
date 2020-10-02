package com.example.fiorichartcardsapp.utilities

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.fiorichartcardsapp.api.getNetworkService
import com.example.fiorichartcardsapp.database.CovidDataBase
import com.example.fiorichartcardsapp.repos.CovidDataRepository
import com.example.fiorichartcardsapp.repos.DefaultCovidDataRepository

/**
 * A Service Locator for the [CovidDataRepository]. This is the prod version, with a
 * the "real" [DefaultCovidDataRepository].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: CovidDataBase? = null
    @Volatile
    var covidDataRepository: CovidDataRepository? = null
        @VisibleForTesting set

    fun provideCovidDataRepository(context: Context): CovidDataRepository {
        synchronized(this) {
            return covidDataRepository ?: createCovidDataRepository(context)
        }
    }

    private fun createCovidDataRepository(context: Context): CovidDataRepository {
        val newDataBase = database?: createDatabase(context)
        val newRepo = DefaultCovidDataRepository(
            context = context as Application,
            network = getNetworkService(),
            covidStatesDao = newDataBase.covidStatesDao,
            covidUsDao = newDataBase.covidUsDao
        )
        covidDataRepository = newRepo
        return newRepo
    }

    private fun createDatabase(context: Context): CovidDataBase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            CovidDataBase::class.java, "CovidData.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            covidDataRepository = null
        }
    }
}
