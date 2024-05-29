package com.github.se.gatherspot.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class NotificationService : FirebaseMessagingService() {
  companion object {
    const val NOTIFICATION_CHANNEL_ID: String = "my_channel_id"
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)

    if (remoteMessage.notification != null) {
      val title = remoteMessage.notification!!.title
      val message = remoteMessage.notification!!.body
      val scheduledTime = remoteMessage.data["scheduledTime"]
      scheduleNotification(this, title!!, message!!, scheduledTime!!)
    }
  }

  fun scheduleNotification(
      context: Context,
      title: String,
      message: String,
      scheduledTimeString: String
  ) {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("Europe/Zurich")
    val scheduledTime = dateFormat.parse(scheduledTimeString)
    val currentTime = System.currentTimeMillis()
    val delay = scheduledTime!!.time - currentTime

    if (delay > 0) {
      val data = Data.Builder().putString("title", title).putString("message", message).build()

      val workRequest =
          OneTimeWorkRequestBuilder<NotificationWorker>()
              .setInputData(data)
              .setInitialDelay(delay, TimeUnit.MILLISECONDS)
              .build()

      WorkManager.getInstance(context).enqueue(workRequest)
    }
  }
}
