package com.comixa.app.ui.labs.lab4

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.comixa.app.R
import com.comixa.app.databinding.FragmentLab4Binding
import com.comixa.app.viewmodel.lab4.Lab4ViewModel
import kotlinx.coroutines.launch

class Lab4Fragment : Fragment(R.layout.fragment_lab4) {

    private var _binding: FragmentLab4Binding? = null
    private val binding get() = _binding!!
    private val vm: Lab4ViewModel by viewModels()
    private lateinit var overlay: LoadingOverlayView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLab4Binding.bind(view)

        overlay = LoadingOverlay.attach(binding.lab4Root as ViewGroup)
        overlay.onStopClick = { vm.stopTask() }

        binding.btnStart.setOnClickListener { vm.startTask() }
        binding.btnStop.setOnClickListener { vm.stopTask() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.progress.collect { p ->
                    if (p == null) overlay.stop()
                    else {
                        overlay.start()
                        overlay.setProgress(p)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
