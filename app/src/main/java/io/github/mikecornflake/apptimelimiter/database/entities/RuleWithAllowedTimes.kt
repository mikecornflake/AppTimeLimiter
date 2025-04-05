package io.github.mikecornflake.apptimelimiter.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class RuleWithAllowedTimes(
    @Embedded val rule: Rule,
    @Relation(
        parentColumn = "ruleId",
        entityColumn = "ruleId"
    )
    val allowedTimes: List<RuleAllowedTime>
)