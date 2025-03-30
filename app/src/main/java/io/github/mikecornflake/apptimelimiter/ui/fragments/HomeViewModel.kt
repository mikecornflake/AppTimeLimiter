package io.github.mikecornflake.apptimelimiter.ui.fragments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mikecornflake.apptimelimiter.settings.SettingsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateAccessibilityServiceStatus(context: Context) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isAccessibilityServiceEnabled = SettingsHelper.hasAccessibilityPermission(context))
            }
        }
    }

    //Observe the datastore for changes, then update the uiState
    fun observeDataStore(context: Context) {
        viewModelScope.launch {
            SettingsHelper.getAppEnabledState(context).collect{isEnabled ->
                _uiState.update {
                    it.copy(isAppEnabled = isEnabled)
                }
            }
        }
    }

    //save to the datastore when the uiState changes
    fun saveState(context: Context){
        viewModelScope.launch {
            SettingsHelper.saveAppEnabledState(context, uiState.value.isAppEnabled)
        }
    }

    fun toggleAppEnabled(context: Context) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val newEnabledState = !currentState.isAppEnabled
                currentState.copy(isAppEnabled = newEnabledState)
            }
        }
    }

    fun loadAppEnabledState(context: Context) {
        viewModelScope.launch {
            SettingsHelper.getAppEnabledState(context).collect { isEnabled ->
                _uiState.update { currentState ->
                    currentState.copy(isAppEnabled = isEnabled)
                }
            }
        }
    }
}