package io.github.mikecornflake.apptimelimiter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.github.mikecornflake.apptimelimiter.database.dao.LogDao
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import kotlinx.coroutines.launch

class LogViewModel(private val logDao: LogDao) : ViewModel() {

    val allLogs: LiveData<List<Log>> = logDao.getAllLogs().asLiveData()
    fun insert(log: Log) {
        viewModelScope.launch {
            logDao.insertLog(log)
        }
    }
}