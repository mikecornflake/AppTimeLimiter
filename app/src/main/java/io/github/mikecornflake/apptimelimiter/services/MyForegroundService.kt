package io.github.mikecornflake.apptimelimiter.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import io.github.mikecornflake.apptimelimiter.R
import io.github.mikecornflake.apptimelimiter.database.AppDatabase
import io.github.mikecornflake.apptimelimiter.util.SettingsHelper
import io.github.mikecornflake.apptimelimiter.util.TimeHelper
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyForegroundService : Service() {
    companion object {
        private const val TAG = "AppTimeLimiter:MyForegroundService"
        private const val FACEBOOK_PACKAGE = "com.facebook.katana"
        var instance: MyForegroundService? = null
        private lateinit var database: AppDatabase
    }

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val NOTIFICATION_ID = 1
    private lateinit var handler: Handler
    private lateinit var timerRunnable: Runnable

    private var isAppEnabled: Boolean = false
    private var count: Int = 0

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate called")

        instance = this

        if (!checkForegroundServicePermission()) {
            Log.e(TAG, "FOREGROUND_SERVICE permission not granted. Stopping service.")

            //stop service
            stopSelf()
        }

        database = AppDatabase.getDatabase(applicationContext)

        // Create the notification channel (required for Android 8.0+)
        createNotificationChannel()

        // Build the notification
        val notification: Notification = createNotification() // NEW: Use createNotification

        // Start the service as a foreground service
        try{
            startForeground(NOTIFICATION_ID, notification)
        } catch (ex: Exception){
            Log.e(TAG, "Failed to start foreground service, stopping.")
            stopSelf()
        }

        // The Timer will be running in the main thread.
        handler = Handler(Looper.getMainLooper())

        // Start the timer using the Runnable interface.
        // The Anonymous function here is assigned to onRun()
        timerRunnable = Runnable {
            doTimer()

            // Reschedule the task
            handler.postDelayed(timerRunnable, 30 * 1000L)
        }

        //get the state from the datastore, and watch it for changes
        serviceScope.launch{
            SettingsHelper.getAppEnabledState(applicationContext).collectLatest {
                isAppEnabled = it
                Log.d(TAG, "App enabled: $isAppEnabled")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the 30 sec timer
        handler.post(timerRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null

        // kill the Timer
        handler.removeCallbacks(timerRunnable)

        // stop listening for application state change
        serviceScope.cancel()

        // And bring us to a stop
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // This is called by a Runnable every 30 seconds
    private fun doTimer() {
        var content:String = "No app running"

        CoroutineScope(Dispatchers.IO).launch {
            var content = "No app running"

            val existingSession = database.activeSessionDao().getAllActiveSessions().firstOrNull()?.firstOrNull()

            existingSession?.let { session ->
                val activePackage = database.packageDao().getPackage(session.packageId).firstOrNull()
                val now = System.currentTimeMillis()
                val duration = (now - session.startTime).toDuration(DurationUnit.MILLISECONDS)
                val formattedDuration = TimeHelper.formatDuration(duration)

                content = "${activePackage?.name ?: "Unknown package"} ($formattedDuration)"
            }

            // Update the notification on the main thread
            withContext(Dispatchers.Main) {
                setNotification(content)
            }
        }

//        // 30-second notification update code goes here
//        if ((SettingsHelper.facebook_start_time.time!=0L) && (SettingsHelper.active_package == FACEBOOK_PACKAGE)) {
//            val now = Date()
//            val differenceInMillis = now.time - SettingsHelper.facebook_start_time.time
//            val ageInSeconds = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis)
//
//            content = "Facebook has been running for $ageInSeconds seconds"
//
//            if ((ageInSeconds > 4*60) && (ageInSeconds < 5*60)) {
//                content = "Facebook will be closed shortly"
//
//                doToast(content)
//            }
//
//            if (ageInSeconds > 5*60) {
//                content = "Facebook has been running for 5 minutes and will be closed"
//
//                doToast(content)
//                doHomeButton()
//            }
//        }


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun doHomeButton() {
        Log.d(TAG, "DoHomeButton")
        val startMain = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(startMain)
    }

    fun doToast(content: String) {
        Toast.makeText(
            applicationContext,
            content,
            Toast.LENGTH_LONG
        ).show()
    }

    // NEW: Creates the notification with the pending intent
    private fun createNotification(content: String = "Monitoring app usage"): Notification {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_home)
            .createPendingIntent()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Time Limiter")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_avg_time_border_24px)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun setNotification(content : String) {
        val notification = createNotification(content)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun checkForegroundServicePermission(): Boolean {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED
        } else{
            //not required for older versions
            true
        }
    }
}