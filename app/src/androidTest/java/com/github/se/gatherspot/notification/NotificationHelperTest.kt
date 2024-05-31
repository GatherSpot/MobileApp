import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.notification.NotificationHelper
import com.github.se.gatherspot.notification.NotificationService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationHelperTest {

  private lateinit var context: Context
  private lateinit var notificationManager: NotificationManager

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  }

  @After
  fun tearDown() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.deleteNotificationChannel(NotificationService.NOTIFICATION_CHANNEL_ID)
    }
  }

  @Test
  fun createNotificationChannel_createsChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationHelper = NotificationHelper()
      notificationHelper.createNotificationChannel(context)

      val channel: NotificationChannel? =
          notificationManager.getNotificationChannel(NotificationService.NOTIFICATION_CHANNEL_ID)
      assert(channel != null)
      assert(channel?.name == "Your Channel Name")
      assert(channel?.importance == NotificationManager.IMPORTANCE_HIGH)
      assert(channel?.description == "Your Channel Description")
    }
  }
}
