package com.github.se.gatherspot.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationHelper {
  /** Method to create a notification channel for the application */
  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Your Channel Name"
      val descriptionText = "Your Channel Description"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel =
          NotificationChannel(NotificationService.NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
          }
      val notificationManager: NotificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }
}
