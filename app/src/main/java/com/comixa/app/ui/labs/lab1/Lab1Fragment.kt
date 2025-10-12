package com.comixa.app.ui.labs.lab1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.comixa.app.R
import com.comixa.app.databinding.FragmentLab1Binding
import com.comixa.app.model.UiEvent
import com.comixa.app.ui.camera.CameraCaptureActivity
import com.comixa.app.ui.camera.CameraCaptureActivity.Companion.EXTRA_USE_FRONT
import com.comixa.app.ui.preview.ImagePreviewActivity
import com.comixa.app.viewmodel.lab1.Lab1ViewModel
import kotlinx.coroutines.flow.collectLatest

class Lab1Fragment : Fragment(R.layout.fragment_lab1) {

    private var _binding: FragmentLab1Binding? = null
    private val binding get() = _binding!!

    private val vm: Lab1ViewModel by viewModels()

    private val reqPostNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) vm.onNotifyClicked()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLab1Binding.bind(view)

        // Кнопки -> делегируем во ViewModel
        binding.btnNotify.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                reqPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                vm.onNotifyClicked()
            }
        }

        binding.btnSearch.setOnClickListener {
            vm.onSearchClicked(binding.etQuery.text?.toString().orEmpty())
        }

        binding.btnOpenCamera.setOnClickListener {
            vm.onOpenCameraClicked(binding.rbFront.isChecked)
        }

        binding.btnOpenPreview.setOnClickListener {
            vm.onOpenPreviewClicked()
        }

        // Подписка на одноразовые события UI
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.events.collectLatest { ev ->
                when (ev) {
                    is UiEvent.ShowToast ->
                        Toast.makeText(requireContext(), ev.msg, Toast.LENGTH_SHORT).show()

                    is UiEvent.OpenUrl ->
                        startActivity(Intent(Intent.ACTION_VIEW, ev.url.toUri()))

                    is UiEvent.OpenCamera ->
                        startActivity(
                            Intent(requireContext(), CameraCaptureActivity::class.java)
                                .putExtra(EXTRA_USE_FRONT, ev.useFront)
                        )

                    is UiEvent.OpenPreview ->
                        startActivity(
                            Intent(requireContext(), ImagePreviewActivity::class.java)
                                .setData(ev.uri.toUri())
                        )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
