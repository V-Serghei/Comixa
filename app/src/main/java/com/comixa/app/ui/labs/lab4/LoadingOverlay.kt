package com.comixa.app.ui.labs.lab4

import android.view.ViewGroup

object LoadingOverlay {
    fun attach(parent: ViewGroup): LoadingOverlayView {
        for (i in 0 until parent.childCount) {
            (parent.getChildAt(i) as? LoadingOverlayView)?.let { return it }
        }
        val v = LoadingOverlayView(parent.context)
        parent.addView(
            v,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        v.bringToFront()
        parent.post { v.bringToFront(); parent.invalidate(); parent.requestLayout() }
        return v
    }
}
