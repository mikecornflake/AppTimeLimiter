package io.github.mikecornflake.apptimelimiter.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rule",
    indices = [Index("packageId")],
    foreignKeys = [
        ForeignKey(
            entity = Package::class,
            parentColumns = ["packageId"],
            childColumns = ["packageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Rule(
    @PrimaryKey(autoGenerate = true) val ruleId: Int = 0,
    val packageId: Int,
    val maxDailyDuration: Long? = null,
    val maxSessionDuration: Long? = null,
    val maxDailyDurationActive: Boolean = false,
    val maxSessionDurationActive: Boolean = false
)