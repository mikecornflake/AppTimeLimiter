package io.github.mikecornflake.apptimelimiter.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.mikecornflake.apptimelimiter.database.entities.Package
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface PackageDao {
    @Insert
    suspend fun insert(packageItem: Package)

    @Update
    suspend fun update(packageItem: Package)

    @Delete
    suspend fun delete(packageItem: Package)

    @Query("SELECT * FROM package")
    fun getAllPackages(): Flow<List<Package>>

    @Query("SELECT * FROM package WHERE packageId = :packageId")
    fun getPackage(packageId: Long): Flow<Package>

    @Query("SELECT * FROM package WHERE packageName = :packageName")
    fun getPackage(packageName: String): Flow<Package>

    @Transaction
    suspend fun insertIfNotExist(packageItem: Package): Package? {
        val existingPackage = getPackage(packageItem.packageName).firstOrNull()
        return if (existingPackage == null) {
            insert(packageItem)
            getPackage(packageItem.packageName).firstOrNull()
        } else {
            existingPackage
        }
    }
}