package io.github.mikecornflake.apptimelimiter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.mikecornflake.apptimelimiter.database.AppDatabase

class ViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PackageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PackageViewModel(database.packageDao()) as T
        } else if (modelClass.isAssignableFrom(RuleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RuleViewModel(database.ruleDao()) as T
        } else if (modelClass.isAssignableFrom(ActiveSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActiveSessionViewModel(database.activeSessionDao()) as T
        } else if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogViewModel(database.logDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}