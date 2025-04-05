package io.github.mikecornflake.apptimelimiter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mikecornflake.apptimelimiter.database.dao.ActiveSessionDao
import io.github.mikecornflake.apptimelimiter.database.entities.ActiveSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ActiveSessionViewModel(private val activeSessionDao: ActiveSessionDao) : ViewModel() {

    fun insert(activeSession: ActiveSession) {
        viewModelScope.launch {
            activeSessionDao.insert(activeSession)
        }
    }

    fun update(activeSession: ActiveSession) {
        viewModelScope.launch {
            activeSessionDao.update(activeSession)
        }
    }

    fun delete(activeSession: ActiveSession) {
        viewModelScope.launch {
            activeSessionDao.delete(activeSession)
        }
    }

    fun getActiveSessionsForPackage(packageId: Int): Flow<List<ActiveSession>> =
        activeSessionDao.getActiveSessionsForPackage(packageId)

    fun getAllActiveSessions(): Flow<List<ActiveSession>> = activeSessionDao.getAllActiveSessions()
}