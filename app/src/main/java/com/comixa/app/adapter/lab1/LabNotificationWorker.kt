package com.comixa.app.adapter.lab1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.comixa.app.R

class LabNotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "comixa_channel"
        if (Build.VERSION.SDK_INT >= 26) {
            val ch =
                NotificationChannel(channelId, "Comixa", NotificationManager.IMPORTANCE_DEFAULT)
            ch.enableLights(true); ch.lightColor = Color.WHITE
            nm.createNotificationChannel(ch)
        }

        val title = inputData.getString("title") ?: "Comixa"
        val text = inputData.getString("text") ?: "Hello!"
        val n = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        nm.notify(1001, n)
        return Result.success()
    }
}