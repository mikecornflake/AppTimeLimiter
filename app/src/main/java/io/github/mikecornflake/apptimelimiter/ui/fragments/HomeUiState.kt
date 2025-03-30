package io.github.mikecornflake.apptimelimiter.ui.fragments

import androidx.annotation.StringRes
import io.github.mikecornflake.apptimelimiter.R

data class HomeUiState(
    val isAppEnabled: Boolean = false,
    val isAccessibilityServiceEnabled: Boolean = false
) {
    @StringRes
    fun getApplicationEnabledStateAsText(): Int {
        return if (isAppEnabled) {
            R.string.disable_application
        } else {
            R.string.enable_application
        }
    }

    @StringRes
    fun getAccessibilityServiceStateAsText(): Int {
        return if (isAccessibilityServiceEnabled) {
            R.string.accessibility_service_enabled
        } else {
            R.string.accessibility_service_disabled
        }
    }
}