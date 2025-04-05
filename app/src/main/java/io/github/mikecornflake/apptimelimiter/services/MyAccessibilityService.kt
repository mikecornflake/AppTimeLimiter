package io.github.mikecornflake.apptimelimiter.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import io.github.mikecornflake.apptimelimiter.database.AppDatabase
import io.github.mikecornflake.apptimelimiter.database.entities.ActiveSession
import io.github.mikecornflake.apptimelimiter.database.entities.Package
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import io.github.mikecornflake.apptimelimiter.util.SettingsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.Instant
import kotlinx.coroutines.flow.firstOrNull

class MyAccessibilityService : AccessibilityService() {

    // TODO: Rewrite to be more app agnostic
    companion object {
        private const val TAG = "AppTimeLimiter:MyAccessibilityService"
    }

    private lateinit var database: AppDatabase

//    private var isAppEnabled: Boolean = false
//    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        android.util.Log.d(TAG, "onCreate called")

        startMyForegroundService()

        database = AppDatabase.getDatabase(applicationContext)


//        //get the state from the datastore, and watch it for changes
//        serviceScope.launch{
//            SettingsHelper.getAppEnabledState(applicationContext).collectLatest {
//                isAppEnabled = it
//                Log.d(TAG, "App enabled: $isAppEnabled")
//            }
//        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        android.util.Log.d(TAG, "onAccessibilityEvent: $eventType")

        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            if (packageName == "com.android.systemui" || packageName == this.packageName) {
                return
            }

            val now = Instant.now().toEpochMilli()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get the Package or create it if it doesn't exist
                    var packageItem = database.packageDao().getPackage(packageName).firstOrNull()

                    if (packageItem == null) {
                        // Package doesn't exist, so create it
                        packageItem = Package(
                            packageName = packageName,
                            name = SettingsHelper.getAppName(applicationContext, packageName)
                        )
                        database.packageDao().insert(packageItem)
                    }

                    // Check for an existing active session
                    val existingSession = database.activeSessionDao().getAllActiveSessions().firstOrNull()?.firstOrNull()

                    // Create a new log entry
                    val logEntry = Log(
                        packageId = packageItem.packageId,
                        startTime = now,
                        endTime = now
                    )

                    database.logDao().insert(logEntry)

                    // Clear any previous active session
                    if (existingSession != null) {
                        database.activeSessionDao().delete(existingSession)
                    }

                    // Create a new active session
                    val newActiveSession = ActiveSession(
                        packageId = packageItem.packageId,
                        startTime = now
                    )
                    database.activeSessionDao().insert(newActiveSession)

                    android.util.Log.i("ACCESSIBILITY", "New active session started for: $packageName")
                } catch (e: Exception) {
                    android.util.Log.e("ACCESSIBILITY", "Error processing session", e)
                }
            }
        }


//        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            val packageName = event.packageName.toString()
//            Log.d(TAG, "Package Name: $packageName")
//
//            // Excluding Android System, track the apps most likely to be activated by User
//            if (packageName != "com.android.systemui") {
//                SettingsHelper.active_package = packageName
//                SettingsHelper.active_application = getApplicationName(packageName)
//            }
//
//            if (packageName == FACEBOOK_PACKAGE) {
//                if (SettingsHelper.facebook_start_time.time==0L) {
//                    SettingsHelper.facebook_start_time=Date()
//                    MyForegroundService.instance?.setNotification("Facebook has started")
//                }
//            } else if (packageName != "com.android.systemui") {
//                    // SettingsHelper.facebook_start_time=Date(0)
//                    MyForegroundService.instance?.setNotification("Active package = $packageName")
//                }
//
//            if ((isAppEnabled) and (FACEBOOK_PACKAGE == packageName)) {
//                Log.d(TAG, "Facebook is in the foreground!")
//                doHomeButton()
//            }
//        }
    }

    override fun onInterrupt() {
        android.util.Log.d(TAG, "onInterrupt")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        android.util.Log.d(TAG, "onServiceConnected")

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }

        serviceInfo = info
    }

    private fun doHomeButton() {
        android.util.Log.d(TAG, "doHomeButton")
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
        //serviceScope.cancel()
    }
}