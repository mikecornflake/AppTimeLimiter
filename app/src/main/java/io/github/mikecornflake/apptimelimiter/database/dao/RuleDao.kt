package io.github.mikecornflake.apptimelimiter.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.mikecornflake.apptimelimiter.database.entities.Rule
import io.github.mikecornflake.apptimelimiter.database.entities.RuleAllowedTime
import io.github.mikecornflake.apptimelimiter.database.entities.RuleWithAllowedTimes
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {
    @Insert
    suspend fun insert(rule: Rule)

    @Insert
    suspend fun insertAllowedTime(allowedTime: RuleAllowedTime)

    @Update
    suspend fun update(rule: Rule)

    @Delete
    suspend fun delete(rule: Rule)

    @Transaction
    @Query("SELECT * FROM rule WHERE packageId = :packageId")
    fun getRulesForPackage(packageId: Int): Flow<List<RuleWithAllowedTimes>>

    @Transaction
    @Query("SELECT * FROM rule")
    fun getAllRules(): Flow<List<RuleWithAllowedTimes>>
}