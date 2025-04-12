package io.github.mikecornflake.apptimelimiter.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "package")
data class Package(
    @PrimaryKey(autoGenerate = true) val packageId: Long = 0,
    val packageName: String,
    val name: String,
    val icon: String? = null // Optional icon
)
