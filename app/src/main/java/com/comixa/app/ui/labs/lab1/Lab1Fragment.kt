package com.comixa.app.ui.labs.lab1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.comixa.app.R
import com.comixa.app.databinding.FragmentLab1Binding
import com.comixa.app.ui.camera.CameraCaptureActivity
import com.comixa.app.ui.camera.CameraCaptureActivity.Companion.EXTRA_USE_FRONT
import com.comixa.app.ui.preview.ImagePreviewActivity
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri

class Lab1Fragment : Fragment(R.layout.fragment_lab1) {

    private var _binding: FragmentLab1Binding? = null
    private val binding get() = _binding!!

    private val reqPostNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* ignore */ }

    @SuppressLint("UseKtx")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLab1Binding.bind(view)

        binding.btnNotify.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                reqPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            val request = OneTimeWorkRequestBuilder<LabNotificationWorker>()
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setInputData(workDataOf("title" to "Comixa", "text" to "Hello! This is a notification in 10 seconds."))
                .build()
            WorkManager.getInstance(requireContext()).enqueue(request)
            Toast.makeText(requireContext(), "Notification scheduled in 10 seconds", Toast.LENGTH_SHORT).show()
        }

        binding.btnSearch.setOnClickListener {
            val q = binding.etQuery.text?.toString().orEmpty()
            if (q.isBlank()) {
                Toast.makeText(requireContext(), "Enter a query", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val url = "https://www.google.com/search?q=" + URLEncoder.encode(q, "UTF-8")
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        }

        binding.btnOpenCamera.setOnClickListener {
            val useFront = binding.rbFront.isChecked
            startActivity(Intent(requireContext(), CameraCaptureActivity::class.java).putExtra(EXTRA_USE_FRONT, useFront))
        }

        binding.btnOpenPreview.setOnClickListener {
            val last = requireContext().getSharedPreferences("lab1", 0).getString("last_photo_uri", null)
            if (last == null) {
                Toast.makeText(requireContext(), "No photos yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(requireContext(), ImagePreviewActivity::class.java).setData(last.toUri()))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
