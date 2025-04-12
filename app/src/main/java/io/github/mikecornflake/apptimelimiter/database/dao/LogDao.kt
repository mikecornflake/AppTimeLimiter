package io.github.mikecornflake.apptimelimiter.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert
    suspend fun insert(log: Log)

    @Query("SELECT * FROM log ORDER BY logId DESC")
    fun getAllLogs(): Flow<List<Log>>

    @Delete
    suspend fun delete(log: Log)

    @Transaction
    suspend fun insertLog(log: Log){
        insert(log)
    }
}