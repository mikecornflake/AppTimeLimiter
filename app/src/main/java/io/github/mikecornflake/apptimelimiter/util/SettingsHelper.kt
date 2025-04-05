package io.github.mikecornflake.apptimelimiter.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import io.github.mikecornflake.apptimelimiter.R
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException

object SettingsHelper {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
    private val APP_ENABLED_KEY = booleanPreferencesKey("app_enabled")
    var facebook_start_time : Date = Date(0)
    var active_package : String = ""
    var active_application : String = ""

    fun hasAccessibilityPermission(context: Context): Boolean {
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledAccessibilityServices =
            accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        for (serviceInfo in enabledAccessibilityServices) {
            if (serviceInfo.resolveInfo.serviceInfo.packageName == context.packageName) {
                // Your app's accessibility service is enabled
                return true
            }
        }
        // Your app's accessibility service is not enabled
        return false
    }

    fun openAccessibilitySettings(context: Context) {
        // Open the Accessibility settings
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
        Toast.makeText(
            context,
            R.string.accessibility_service_explanation,
            Toast.LENGTH_LONG
        ).show()
    }

//    // TODO Not needed at the moment
//    fun hasUsageStatsPermission(context: Context): Boolean {
//        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        val mode: Int
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            // API level 29 (Android 10) and higher
//            mode = appOps.unsafeCheckOpNoThrow(
//                AppOpsManager.OPSTR_GET_USAGE_STATS,
//                android.os.Process.myUid(),
//                context.packageName
//            )
//        } else {
//            // Older API levels (before API 29)
//            mode = appOps.checkOpNoThrow(
//                AppOpsManager.OPSTR_GET_USAGE_STATS,
//                android.os.Process.myUid(),
//                context.packageName
//            )
//        }
//        return mode == AppOpsManager.MODE_ALLOWED
//    }
//
//    // TODO Not needed at the moment
//    fun openUsageAccessSettings(context: Context) {
//        // Open the Usage Access settings
//        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//        intent.setData(Uri.parse("package:" + context.packageName))
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)
//        Toast.makeText(
//            context,
//            R.string.usage_stats_explanation,
//            Toast.LENGTH_LONG
//        ).show()
//    }

    suspend fun saveAppEnabledState(context: Context, isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[APP_ENABLED_KEY] = isEnabled
        }
    }

    fun getAppEnabledState(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[APP_ENABLED_KEY] ?: false
        }
    }

    fun getAppName(context: Context, packageName: String): String {
        val packageManager = context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            // Handle the exception (e.g., log it or return a default name)
            packageName
        }
    }
}