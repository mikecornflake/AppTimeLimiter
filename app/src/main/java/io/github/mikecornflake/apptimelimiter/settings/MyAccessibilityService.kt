package io.github.mikecornflake.apptimelimiter.settings

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.content.Context
import androidx.compose.ui.test.cancel
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.github.mikecornflake.apptimelimiter.settings.SettingsHelper
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collectLatest

class MyAccessibilityService : AccessibilityService() {

    // TODO: Rewrite to be more app agnostic
    companion object {
        private const val TAG = "AppTimeLimiter:MyAccessibilityService"
        private const val FACEBOOK_PACKAGE = "com.facebook.katana"
    }

    private var isAppEnabled: Boolean = false
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        //get the state from the datastore, and watch it for changes
        serviceScope.launch{
            SettingsHelper.getAppEnabledState(applicationContext).collectLatest {
                isAppEnabled = it
                Log.d(TAG, "App enabled: $isAppEnabled")
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        Log.d(TAG, "onAccessibilityEvent: $eventType")

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName.toString()
            Log.d(TAG, "Package Name: $packageName")

            if ((isAppEnabled) and (FACEBOOK_PACKAGE == packageName)) {
                Log.d(TAG, "Facebook is in the foreground!")
                DoHomeButton()
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }

        serviceInfo = info
    }

    private fun DoHomeButton() {
        Log.d(TAG, "DoHomeButton")
        val startMain = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(startMain)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}