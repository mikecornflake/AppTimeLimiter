package io.github.mikecornflake.apptimelimiter.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "log",
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
data class Log(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val packageId: Long,
    val startTime: Long,
    val endTime: Long,
    val duration: Long = 0,
    val overtime: Long = 0
)