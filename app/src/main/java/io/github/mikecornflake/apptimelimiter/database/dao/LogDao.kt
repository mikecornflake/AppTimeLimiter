package io.github.mikecornflake.apptimelimiter.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert
    suspend fun insert(log: Log)

    @Update
    suspend fun update(log: Log)

    @Delete
    suspend fun delete(log: Log)

    @Query("SELECT * FROM log WHERE packageId = :packageId")
    fun getLogsForPackage(packageId: Int): Flow<List<Log>>

    @Query("SELECT * FROM log")
    fun getAllLogs(): Flow<List<Log>>

    @Query("DELETE FROM log WHERE startTime < :timestamp")
    suspend fun deleteLogsOlderThan(timestamp: Long)
}