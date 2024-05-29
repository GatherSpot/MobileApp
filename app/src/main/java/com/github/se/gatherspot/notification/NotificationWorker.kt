package com.github.se.gatherspot.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.se.gatherspot.R

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
  override fun doWork(): Result {
    val title = inputData.getString("title")
    val message = inputData.getString("message")
    val notification =
        NotificationCompat.Builder(applicationContext, NotificationService.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.community) // Make sure you have an icon in your resources
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

    if (ActivityCompat.checkSelfPermission(
        this.applicationContext, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED) {
      return Result.failure()
    }
    NotificationManagerCompat.from(applicationContext).notify(1, notification)

    return Result.success()
  }
}
