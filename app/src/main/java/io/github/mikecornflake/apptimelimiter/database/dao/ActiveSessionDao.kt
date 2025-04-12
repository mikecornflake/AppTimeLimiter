package io.github.mikecornflake.apptimelimiter.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import io.github.mikecornflake.apptimelimiter.database.entities.ActiveSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveSessionDao {
    @Insert
    suspend fun insert(activeSession: ActiveSession)

    @Delete
    suspend fun delete(activeSession: ActiveSession)

    @Query("SELECT * FROM active_session")
    fun getAllActiveSessions(): Flow<List<ActiveSession>>

    @Transaction
    suspend fun insertNewActiveSession(newActiveSession: ActiveSession, oldActiveSession: ActiveSession?){
        if (oldActiveSession != null) {
            delete(oldActiveSession)
        }
        insert(newActiveSession)
    }
}