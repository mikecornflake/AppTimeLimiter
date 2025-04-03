package io.github.mikecornflake.apptimelimiter.ui.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.mikecornflake.apptimelimiter.databinding.FragmentHomeBinding
import io.github.mikecornflake.apptimelimiter.settings.SettingsHelper
import java.util.Date
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val appEnabledButton: Button = binding.appEnabledButton
        val accessibilityStatusText: TextView = binding.accessibilityStatusText

        //Call observeDataStore to watch for changes
        homeViewModel.observeDataStore(requireContext())

        appEnabledButton.setOnClickListener {
            homeViewModel.toggleAppEnabled(requireContext())
            //Call saveState to save to the datastore
            homeViewModel.saveState(requireContext())

            SettingsHelper.facebook_start_time=Date(0)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect { uiState ->
                    // Update the UI based on the uiState
                    appEnabledButton.setText(uiState.getApplicationEnabledStateAsText())
                    accessibilityStatusText.setText(uiState.getAccessibilityServiceStateAsText())
                }
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.updateAccessibilityServiceStatus(requireContext())
        homeViewModel.loadAppEnabledState(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}