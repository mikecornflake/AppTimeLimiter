package io.github.mikecornflake.apptimelimiter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.mikecornflake.apptimelimiter.databinding.FragmentMoreBinding
import io.github.mikecornflake.apptimelimiter.util.SettingsHelper

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //add the listener to the button
        binding.accessibilitySettingsButton.setOnClickListener {
            SettingsHelper.openAccessibilitySettings(requireContext())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}