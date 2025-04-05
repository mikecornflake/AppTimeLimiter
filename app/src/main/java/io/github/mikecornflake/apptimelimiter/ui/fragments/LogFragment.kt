package io.github.mikecornflake.apptimelimiter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.mikecornflake.apptimelimiter.databinding.FragmentLogsBinding

class LogFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val logViewModel =
            ViewModelProvider(this).get(LogViewModel::class.java)

        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textLogs
        logViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}