package io.github.mikecornflake.apptimelimiter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mikecornflake.apptimelimiter.database.dao.RuleDao
import io.github.mikecornflake.apptimelimiter.database.entities.Rule
import io.github.mikecornflake.apptimelimiter.database.entities.RuleAllowedTime
import io.github.mikecornflake.apptimelimiter.database.entities.RuleWithAllowedTimes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RuleViewModel(private val ruleDao: RuleDao) : ViewModel() {

    fun insert(rule: Rule) {
        viewModelScope.launch {
            ruleDao.insert(rule)
        }
    }

    fun insertAllowedTime(allowedTime: RuleAllowedTime) {
        viewModelScope.launch {
            ruleDao.insertAllowedTime(allowedTime)
        }
    }

    fun update(rule: Rule) {
        viewModelScope.launch {
            ruleDao.update(rule)
        }
    }

    fun delete(rule: Rule) {
        viewModelScope.launch {
            ruleDao.delete(rule)
        }
    }

    fun getRulesForPackage(packageId: Int): Flow<List<RuleWithAllowedTimes>> = ruleDao.getRulesForPackage(packageId)
    fun getAllRules(): Flow<List<RuleWithAllowedTimes>> = ruleDao.getAllRules()
}