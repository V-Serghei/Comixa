package com.comixa.app.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.comixa.app.databinding.ActivityCameraCaptureBinding
import com.comixa.app.ui.preview.ImagePreviewActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.content.edit

@Suppress("DEPRECATION")
class CameraCaptureActivity : AppCompatActivity() {

    companion object { const val EXTRA_USE_FRONT = "use_front" }

    private lateinit var binding: ActivityCameraCaptureBinding
    private var imageCapture: ImageCapture? = null

    private val reqCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera() else finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            reqCamera.launch(Manifest.permission.CAMERA)
        } else startCamera()

        binding.btnCapture.setOnClickListener { takePhoto() }
    }

    @SuppressLint("MissingPermission")
    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val useFront = intent.getBooleanExtra(EXTRA_USE_FRONT, false)
            val selector = CameraSelector.Builder()
                .requireLensFacing(if (useFront) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
                .build()

            val preview = Preview.Builder()
                .setTargetResolution(Size(1080, 1920))
                .build().apply { surfaceProvider = binding.previewView.surfaceProvider }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1080, 1920))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            provider.unbindAll()
            provider.bindToLifecycle(this, selector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val capture = imageCapture ?: return
        val file = File(cacheDir, "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg")
        val out = ImageCapture.OutputFileOptions.Builder(file).build()
        capture.takePicture(out, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(this@CameraCaptureActivity, "Photo capture error", Toast.LENGTH_SHORT).show()
                }
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    val uri = Uri.fromFile(file)
                    getSharedPreferences("lab1", 0).edit {
                        putString(
                            "last_photo_uri",
                            uri.toString()
                        )
                    }
                    startActivity(Intent(this@CameraCaptureActivity, ImagePreviewActivity::class.java).setData(uri))
                }
            })
    }
}
