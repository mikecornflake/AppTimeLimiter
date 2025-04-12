package io.github.mikecornflake.apptimelimiter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.mikecornflake.apptimelimiter.database.AppDatabase
import io.github.mikecornflake.apptimelimiter.databinding.FragmentLogBinding
import io.github.mikecornflake.apptimelimiter.ui.adapters.LogRecyclerViewAdapter
import io.github.mikecornflake.apptimelimiter.viewmodels.LogViewModel
import io.github.mikecornflake.apptimelimiter.viewmodels.ViewModelFactory

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var logAdapter: LogRecyclerViewAdapter
    private val logViewModel: LogViewModel by activityViewModels {
        ViewModelFactory(AppDatabase.getDatabase(requireContext()))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}