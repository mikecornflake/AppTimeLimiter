package io.github.mikecornflake.apptimelimiter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.mikecornflake.apptimelimiter.database.dao.ActiveSessionDao
import io.github.mikecornflake.apptimelimiter.database.dao.LogDao
import io.github.mikecornflake.apptimelimiter.database.dao.PackageDao
import io.github.mikecornflake.apptimelimiter.database.dao.RuleDao
import io.github.mikecornflake.apptimelimiter.database.entities.ActiveSession
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import io.github.mikecornflake.apptimelimiter.database.entities.Package
import io.github.mikecornflake.apptimelimiter.database.entities.Rule
import io.github.mikecornflake.apptimelimiter.database.entities.RuleAllowedTime

@Database(
    entities = [
        Package::class,
        Rule::class,
        RuleAllowedTime::class,
        ActiveSession::class,
        Log::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun packageDao(): PackageDao
    abstract fun ruleDao(): RuleDao
    abstract fun activeSessionDao(): ActiveSessionDao
    abstract fun logDao(): LogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}