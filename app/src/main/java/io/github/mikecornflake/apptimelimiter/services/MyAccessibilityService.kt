package io.github.mikecornflake.apptimelimiter.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import io.github.mikecornflake.apptimelimiter.settings.SettingsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.cancel
import java.util.Date

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

        Log.d(TAG, "onCreate called")

        startMyForegroundService()

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

            if (FACEBOOK_PACKAGE == packageName) {
                if (SettingsHelper.facebook_start_time.time==0L) {
                    SettingsHelper.facebook_start_time=Date()
                    MyForegroundService.instance?.setNotification("Facebook has started")
                }
            } else {
                if (packageName != "com.android.systemui") {
                    // SettingsHelper.facebook_start_time=Date(0)
                    MyForegroundService.instance?.setNotification("Active package = $packageName")
                }
            }

            if ((isAppEnabled) and (FACEBOOK_PACKAGE == packageName)) {
                Log.d(TAG, "Facebook is in the foreground!")
                doHomeButton()
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

    private fun doHomeButton() {
        Log.d(TAG, "doHomeButton")
        val startMain = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(startMain)
    }

    private fun startMyForegroundService(){
        val startForeground = Intent(applicationContext, MyForegroundService::class.java)
        applicationContext.startForegroundService(startForeground)
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}