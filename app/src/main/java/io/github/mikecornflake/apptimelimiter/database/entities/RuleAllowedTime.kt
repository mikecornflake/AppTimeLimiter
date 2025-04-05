package io.github.mikecornflake.apptimelimiter.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rule_allowed_time",
    indices = [Index("ruleId")],
    foreignKeys = [
        ForeignKey(
            entity = Rule::class,
            parentColumns = ["ruleId"],
            childColumns = ["ruleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RuleAllowedTime(
    @PrimaryKey(autoGenerate = true) val allowedTimeId: Int = 0,
    val ruleId: Int,
    val startTime: Long,
    val endTime: Long
)
