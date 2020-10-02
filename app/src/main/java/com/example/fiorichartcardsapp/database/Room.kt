package com.example.fiorichartcardsapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CovidDailyStatesDao {
    @Query("select * from databasecoviddailystate")
    fun getCovidDailyStates(): LiveData<List<DatabaseCovidDailyState>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(covidDailyStateList: List<DatabaseCovidDailyState>)

    @Query("delete from databasecoviddailystate")
    suspend fun deleteAll()

}

@Dao
interface CovidDailyUsDao {
    @Query("select * from databasecoviddailyus")
    fun getCovidDailyUs(): LiveData<List<DatabaseCovidDailyUs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(covidDailyUsList: List<DatabaseCovidDailyUs>)

    @Query("delete from databasecoviddailyus")
    suspend fun deleteAll()
}


@Database(entities = [DatabaseCovidDailyState::class, DatabaseCovidDailyUs::class], version = 1)
abstract class CovidDataBase: RoomDatabase() {
    abstract val covidStatesDao: CovidDailyStatesDao
    abstract val covidUsDao: CovidDailyUsDao
}

private lateinit var INSTANCE: CovidDataBase

fun getDatabase(context: Context): CovidDataBase {
    synchronized(CovidDataBase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                CovidDataBase::class.java,
                "CovidData").build()
        }
    }
    return INSTANCE
}
