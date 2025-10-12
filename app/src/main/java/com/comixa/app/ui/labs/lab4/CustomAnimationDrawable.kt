package com.comixa.app.ui.labs.lab4

import android.graphics.drawable.AnimationDrawable

class CustomAnimationDrawable(private val originalDrawable: AnimationDrawable) : AnimationDrawable() {

    interface AnimationFinishListener {
        fun onAnimationFinished()
    }

    var animationFinishListener: AnimationFinishListener? = null
    private var finished = false

    init {
        for (i in 0 until originalDrawable.numberOfFrames) {
            addFrame(originalDrawable.getFrame(i), originalDrawable.getDuration(i))
        }
        isOneShot = originalDrawable.isOneShot
    }

    override fun selectDrawable(idx: Int): Boolean {
        val ret = super.selectDrawable(idx)

        if ((idx != 0) && (idx == numberOfFrames - 1)) {
            if (!finished) {
                finished = true
                animationFinishListener?.onAnimationFinished()
            }
        }

        return ret
    }
}