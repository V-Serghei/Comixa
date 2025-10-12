package com.comixa.app.ui.labs.lab4

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.comixa.app.R
import com.comixa.app.databinding.ViewLoadingOverlayBinding
import com.comixa.app.model.LoadingEvent

class LoadingOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = ViewLoadingOverlayBinding.inflate(LayoutInflater.from(context), this, true)

    private val frames = arrayOf(
        R.drawable.spinner_0,
        R.drawable.spinner_22_5,
        R.drawable.spinner_45,
        R.drawable.spinner_67_5,
        R.drawable.spinner_90,
        R.drawable.spinner_112_5,
        R.drawable.spinner_135,
        R.drawable.spinner_157_5,
        R.drawable.spinner_180,
        R.drawable.spinner_202_5,
        R.drawable.spinner_225,
        R.drawable.spinner_247_5,
        R.drawable.spinner_270,
        R.drawable.spinner_292_5,
        R.drawable.spinner_315,
        R.drawable.spinner_337_5
    )

    private var currentFrame = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isAnimating = false

    private val frameRunnable = object : Runnable {
        override fun run() {
            if (isAnimating) {
                binding.ivSpinner.setImageResource(frames[currentFrame])
                currentFrame = (currentFrame + 1) % frames.size
                handler.postDelayed(this, 10)
            }
        }
    }

    var onStopClick: (() -> Unit)? = null
    var listener: ((LoadingEvent) -> Unit)? = null

    init {
        isClickable = true
        isFocusable = true
        ViewCompat.setElevation(this, 10000f)
        this.z = 10000f
        visibility = View.GONE
        setOnClickListener { /* consume */ }
        binding.btnStop.setOnClickListener {
            stop()
            onStopClick?.invoke()
        }
    }

    fun start() {


        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
            bringToFront()
            requestLayout(); invalidate()
            listener?.invoke(LoadingEvent.Start)
        }

        if (!isAnimating) {
            isAnimating = true
            currentFrame = 0
            handler.removeCallbacks(frameRunnable)
            handler.post(frameRunnable)
        }
    }

    fun setProgress(percent: Int) {
        val p = percent.coerceIn(0, 100)
        binding.progressBar.progress = p
        binding.tvPercent.text = "$p%"
        listener?.invoke(LoadingEvent.Progress(p))
    }

    fun stop() {

        isAnimating = false
        handler.removeCallbacks(frameRunnable)
        visibility = View.GONE
        listener?.invoke(LoadingEvent.Stop)
    }

    override fun onDetachedFromWindow() {
        stop()
        super.onDetachedFromWindow()
    }
}