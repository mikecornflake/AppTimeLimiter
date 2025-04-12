package io.github.mikecornflake.apptimelimiter.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import io.github.mikecornflake.apptimelimiter.database.AppDatabase
import io.github.mikecornflake.apptimelimiter.database.entities.ActiveSession
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import io.github.mikecornflake.apptimelimiter.database.entities.Package
import io.github.mikecornflake.apptimelimiter.util.SettingsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import kotlinx.coroutines.flow.firstOrNull
import java.util.Timer
import java.util.TimerTask

class MyAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MyAccessibilityService"
        // Add known problematic launchers to this set
        private val KNOWN_PROBLEM_LAUNCHERS = setOf("com.oppo.launcher")
    }

    private lateinit var database: AppDatabase
    private var isProcessingEvent = false
    private var lastEventJob: Job? = null
    private val processingDelay = 500L // Adjust delay as needed

    // Periodically check for the foreground app
    private var foregroundAppCheckTimer: Timer? = null
    private val foregroundAppCheckPeriod = 2000L // Check every 2 seconds (adjust as needed)

    override fun onCreate() {
        super.onCreate()

        android.util.Log.d(TAG, "onCreate called")

        startMyForegroundService()

        database = AppDatabase.getDatabase(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        android.util.Log.d(TAG, "onAccessibilityEvent: eventType=$eventType")
        processEvent(event)
    }

    private fun processEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Check if the event is from a known problematic launcher
            if (KNOWN_PROBLEM_LAUNCHERS.contains(packageName)) {
                android.util.Log.d(TAG, "onAccessibilityEvent: Skipping known problematic launcher event: $packageName")
                return // Skip processing this event
            }

            processPackage(packageName)
        }
    }

    private fun processPackage(packageName: String) {
        android.util.Log.d(TAG, "processPackage: packageName=$packageName")

        val now = Instant.now().toEpochMilli()
        lastEventJob?.cancel()
        lastEventJob = CoroutineScope(Dispatchers.IO).launch {
            if (isProcessingEvent) {
                delay(processingDelay) // Wait for processing to finish
            }

            isProcessingEvent = true
            try {
                // Find the correct packageId.  Cache all values in Table:Package using atomic transaction
                val packageItem = Package(
                    packageName = packageName,
                    name = SettingsHelper.getAppName(applicationContext, packageName)
                )
                val savedPackage = database.packageDao().insertIfNotExist(packageItem)!!

                // Check for an existing active session
                val existingSession = database.activeSessionDao().getAllActiveSessions().firstOrNull()?.firstOrNull()

                // Has the active session changed?
                if (existingSession == null || existingSession.packageId != savedPackage.packageId) {
                    // If there was an existing session, we need to log it.
                    if (existingSession != null) {
                        // Create a new log entry
                        val logEntry = Log(
                            packageId = existingSession.packageId,
                            startTime = existingSession.startTime,
                            endTime = now,
                            duration = now - existingSession.startTime
                        )
                        database.logDao().insertLog(logEntry)
                    }

                    // Create a new active session, using atomic transaction
                    val newActiveSession = ActiveSession(
                        packageId = savedPackage.packageId,
                        startTime = now
                    )
                    database.activeSessionDao().insertNewActiveSession(newActiveSession, existingSession)
                    android.util.Log.i("ACCESSIBILITY", "New active session started for: $packageName")
                }
            } catch (e: Exception) {
                android.util.Log.e("ACCESSIBILITY", "Error processing session", e)
            } finally {
                isProcessingEvent = false // Allow processing of new events
            }
        }
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
        startForegroundAppCheckTimer()
    }

    private fun startForegroundAppCheckTimer() {
        android.util.Log.d(TAG, "onServiceConnected")

        foregroundAppCheckTimer = Timer()
        foregroundAppCheckTimer?.schedule(object : TimerTask() {
            override fun run() {
                val currentPackage = getForegroundAppPackageName()
                if (currentPackage != null) {
                    processPackage(currentPackage)
                }
            }
        }, foregroundAppCheckPeriod, foregroundAppCheckPeriod)
    }

    private fun stopForegroundAppCheckTimer() {
        foregroundAppCheckTimer?.cancel()
        foregroundAppCheckTimer = null
    }

    private fun getForegroundAppPackageName(): String? {
        val window: AccessibilityNodeInfo? = rootInActiveWindow
        val packageName = window?.packageName?.toString()
        return packageName
    }

    private fun doHomeButton() {
        android.util.Log.d(TAG, "doHomeButton")
        val startMain = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(startMain)
    }

    private fun startMyForegroundService() {
        val startForeground = Intent(applicationContext, MyForegroundService::class.java)
        applicationContext.startForegroundService(startForeground)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForegroundAppCheckTimer()
    }
}