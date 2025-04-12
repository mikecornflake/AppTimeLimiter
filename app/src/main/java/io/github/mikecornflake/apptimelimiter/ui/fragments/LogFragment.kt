package io.github.mikecornflake.apptimelimiter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.mikecornflake.apptimelimiter.R
import io.github.mikecornflake.apptimelimiter.database.AppDatabase
import io.github.mikecornflake.apptimelimiter.databinding.FragmentLogBinding
import io.github.mikecornflake.apptimelimiter.ui.adapters.LogRecyclerViewAdapter
import io.github.mikecornflake.apptimelimiter.viewmodels.LogViewModel
import io.github.mikecornflake.apptimelimiter.viewmodels.ViewModelFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var logAdapter: LogRecyclerViewAdapter
    private val logViewModel: LogViewModel by activityViewModels {
        ViewModelFactory(AppDatabase.getDatabase(requireContext()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = AppDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.title_logs)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_log_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_clear_logs -> {
                        clearOldLogs()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Initialize the adapter
        logAdapter = LogRecyclerViewAdapter()

        // Set up the RecyclerView
        binding.logRecyclerView.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Observe the logs from the ViewModel
        logViewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            logAdapter.setLogs(logs)
        }
    }

    private fun clearOldLogs() {
        val twentyFourHoursAgo = Instant.now().minus(24, ChronoUnit.HOURS).toEpochMilli()

        CoroutineScope(Dispatchers.IO).launch {
            database.logDao().deleteLogsOlderThan(twentyFourHoursAgo)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}