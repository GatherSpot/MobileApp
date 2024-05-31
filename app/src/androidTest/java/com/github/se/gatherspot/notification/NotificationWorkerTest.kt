package com.github.se.gatherspot.notification

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import androidx.work.Data
import androidx.work.testing.TestWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import java.util.concurrent.Executor
import org.junit.Rule
import org.junit.Test

class NotificationWorkerTest {

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

  @Test
  fun testNotificationWorker() {
    // Initialize WorkManager for testing
    val context = ApplicationProvider.getApplicationContext<Context>()
    WorkManagerTestInitHelper.initializeTestWorkManager(context)

    // Create test data
    val inputData =
        Data.Builder().putString("title", "Test Title").putString("message", "Test Message").build()

    // Create worker
    val worker =
        TestWorkerBuilder<NotificationWorker>(
                context = context, executor = Executor { it.run() }, inputData = inputData)
            .build()

    // Run the worker
    val result = worker.doWork()

    // Verify result
    assert(result is androidx.work.ListenableWorker.Result.Success)
  }
}
