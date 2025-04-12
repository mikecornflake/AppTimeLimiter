package io.github.mikecornflake.apptimelimiter.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

object TimeHelper {
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    fun formatDuration(duration: Duration): String {
        val hours = duration.inWholeHours
        val minutes = duration.minus(hours.hours).inWholeMinutes

        return when {
            hours > 0 && minutes > 0 -> "$hours hours and $minutes minutes"
            hours > 0 -> "$hours hours"
            minutes > 0 -> "$minutes minutes"
            else -> "Less than a minute"
        }
    }
}