package com.github.se.gatherspot.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
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
}
