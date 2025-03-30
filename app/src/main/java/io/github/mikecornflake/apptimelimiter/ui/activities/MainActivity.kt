package io.github.mikecornflake.apptimelimiter.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.mikecornflake.apptimelimiter.R
import io.github.mikecornflake.apptimelimiter.databinding.ActivityMainBinding
import io.github.mikecornflake.apptimelimiter.settings.SettingsHelper

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AppTimeLimiter:MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsHelper: SettingsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // User interface
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_rules,
                R.id.navigation_logs,
                R.id.navigation_more
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // settings
        settingsHelper = SettingsHelper()
    }

    override fun onResume() {
        super.onResume()

        // Check Accessibility Service
        if (!settingsHelper.hasAccessibilityPermission(this)) {
            settingsHelper.openAccessibilitySettings(this)
        }
    }
}