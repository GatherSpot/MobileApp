package com.github.se.gatherspot.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationServiceTest {

  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
  }

  @Test
  fun scheduleNotification_schedulesWorkRequest() {
    val notificationService = NotificationService()
    val title = "Test Title"
    val message = "Test Message"
    val scheduledTime = "12/31/2024, 12:00"

    notificationService.scheduleNotification(context, title, message, scheduledTime)

    val workManager = WorkManager.getInstance(context)
    val workRequests = workManager.getWorkInfosByTag(NotificationWorker::class.java.name).get()
    assert(workRequests.isNotEmpty())

    val workInfo = workRequests[0]
    assert(workInfo.tags.contains(NotificationWorker::class.java.name))
    assert(workInfo.state == androidx.work.WorkInfo.State.ENQUEUED)
  }

  private fun createRemoteMessage(
      title: String,
      message: String,
      scheduledTime: String
  ): RemoteMessage {
    val notificationMap = mapOf("title" to title, "body" to message)
    val dataMap = mapOf("scheduledTime" to scheduledTime)

    val builder = RemoteMessage.Builder("testSender").setMessageId("1").setTtl(3600)

    notificationMap.forEach { builder.addData(it.key, it.value) }
    dataMap.forEach { builder.addData(it.key, it.value) }

    return builder.build()
  }
}
