package io.github.mikecornflake.apptimelimiter

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast

class SettingsHelper {
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
}