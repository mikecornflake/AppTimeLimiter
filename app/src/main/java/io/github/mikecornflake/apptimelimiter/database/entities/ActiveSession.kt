package io.github.mikecornflake.apptimelimiter.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "active_session",
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
data class ActiveSession(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val packageId: Long,
    val startTime: Long,
    val endTime:Long? = null,
    val overtime: Long = 0
)
