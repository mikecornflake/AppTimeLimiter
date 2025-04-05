package io.github.mikecornflake.apptimelimiter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mikecornflake.apptimelimiter.database.dao.LogDao
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LogViewModel(private val logDao: LogDao) : ViewModel() {
    fun insert(log: Log) {
        viewModelScope.launch {
            logDao.insert(log)
        }
    }

    fun update(log: Log) {
        viewModelScope.launch {
            logDao.update(log)
        }
    }

    fun delete(log: Log) {
        viewModelScope.launch {
            logDao.delete(log)
        }
    }

    fun getLogsForPackage(packageId: Int): Flow<List<Log>> = logDao.getLogsForPackage(packageId)

    fun getAllLogs(): Flow<List<Log>> = logDao.getAllLogs()

    fun deleteLogsOlderThan(timestamp: Long) {
        viewModelScope.launch {
            logDao.deleteLogsOlderThan(timestamp)
        }
    }
}