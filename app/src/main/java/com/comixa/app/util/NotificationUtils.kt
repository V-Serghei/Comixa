package com.comixa.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationUtils {
    const val CHANNEL_ID = "comixa.general"

    fun createChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID, "General", NotificationManager.IMPORTANCE_DEFAULT
            )
            val nm = ctx.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(ch)
        }
    }
}
