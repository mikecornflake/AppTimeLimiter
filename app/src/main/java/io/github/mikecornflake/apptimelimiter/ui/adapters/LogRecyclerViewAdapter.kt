package io.github.mikecornflake.apptimelimiter.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.mikecornflake.apptimelimiter.R
import io.github.mikecornflake.apptimelimiter.database.AppDatabase
import io.github.mikecornflake.apptimelimiter.database.entities.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.*
import kotlin.time.Duration.Companion.hours
import io.github.mikecornflake.apptimelimiter.util.TimeHelper

class LogRecyclerViewAdapter : RecyclerView.Adapter<LogRecyclerViewAdapter.LogViewHolder>() {

    private var logs: List<Log> = emptyList()

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packageNameTextView: TextView = itemView.findViewById(R.id.packageNameTextView)
        val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
        val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
        val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
        return LogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val currentLog = logs[position]
        //Get the database
        val database = AppDatabase.getDatabase(holder.itemView.context)
        CoroutineScope(Dispatchers.IO).launch {
            //Get the package name from the packageId
            val packageItem = database.packageDao().getPackage(currentLog.packageId).firstOrNull()
            val packageName = packageItem?.name ?: "Unknown Package"
            CoroutineScope(Dispatchers.Main).launch {
                holder.packageNameTextView.text = packageName
            }
        }
        holder.startTimeTextView.text = "Start: ${TimeHelper.formatTimestamp(currentLog.startTime)}"
        holder.endTimeTextView.text = "End: ${TimeHelper.formatTimestamp(currentLog.endTime)}"

        val duration = currentLog.duration.toDuration(DurationUnit.MILLISECONDS)
        val durationString = TimeHelper.formatDuration(duration)
        holder.durationTextView.text = "Duration: $durationString"
    }

    override fun getItemCount(): Int = logs.size

    fun setLogs(logs: List<Log>) {
        this.logs = logs
        notifyDataSetChanged()
    }
}