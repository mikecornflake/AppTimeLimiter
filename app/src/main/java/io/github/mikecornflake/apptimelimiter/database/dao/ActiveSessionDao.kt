package io.github.mikecornflake.apptimelimiter.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.mikecornflake.apptimelimiter.database.entities.ActiveSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveSessionDao {
    @Insert
    suspend fun insert(activeSession: ActiveSession)

    @Update
    suspend fun update(activeSession: ActiveSession)

    @Delete
    suspend fun delete(activeSession: ActiveSession)

    @Query("SELECT * FROM active_session WHERE packageId = :packageId")
    fun getActiveSessionsForPackage(packageId: Int): Flow<List<ActiveSession>>

    @Query("SELECT * FROM active_session")
    fun getAllActiveSessions(): Flow<List<ActiveSession>>
}