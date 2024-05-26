package com.github.se.gatherspot.sql

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.CountDownLatch

class EventSQLiteConnectionTest {
  private lateinit var eventDao: EventDao
  private lateinit var db: AppDatabase
  private val event1 =
      Event(
          id = "1",
          title = "Testing toJson()",
          description = "I have to be converted to Json",
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 1,
          organizerID = Profile.testParticipant().id,
          categories = setOf(Interests.BOWLING),
          eventEndDate = LocalDate.of(2024, 4, 15),
          eventStartDate = LocalDate.of(2024, 4, 14),
          globalRating = 4,
          inscriptionLimitDate = LocalDate.of(2024, 4, 11),
          inscriptionLimitTime = LocalTime.of(23, 59),
          location = null,
          registeredUsers = mutableListOf(),
          timeBeginning = LocalTime.of(13, 0),
          timeEnding = LocalTime.of(16, 0),
          image = "")
  private val event2 =
      Event(
          id = "2",
          title = "Testing toJson()",
          description = "I have to be converted to Json",
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 1,
          organizerID = Profile.testParticipant().id,
          categories = setOf(Interests.BOWLING),
          eventEndDate = LocalDate.of(2024, 4, 15),
          eventStartDate = LocalDate.of(2024, 4, 14),
          globalRating = 4,
          inscriptionLimitDate = LocalDate.of(2024, 4, 11),
          inscriptionLimitTime = LocalTime.of(23, 59),
          location = null,
          registeredUsers = mutableListOf(),
          timeBeginning = LocalTime.of(13, 0),
          timeEnding = LocalTime.of(16, 0),
          image = "")

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    eventDao = db.EventDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun writeAndRead() = runTest {
    eventDao.insert(event1)
    val getVal1 = eventDao.get("1").await()
    assertEquals(event1, getVal1)
  }

  @Test
  @Throws(Exception::class)
  fun delete()  {
    runBlocking {
      eventDao.insert(event1, event2)
      eventDao.delete(event1)
      val getVal1 = eventDao.get("1").await()
      val getVal2 = eventDao.get("2").await()
      assertNull(getVal1)
      assertNotNull(getVal2)
    }
  }

  @Test
  @Throws(Exception::class)
  fun update() {
    eventDao.insert(event1)
    val modifiedEvent =
        Event(
            id = "1",
            title = "New Title",
            description = "New Description",
            attendanceMaxCapacity = 10,
            attendanceMinCapacity = 5,
            organizerID = Profile.testParticipant().id,
            categories = setOf(Interests.BOWLING),
            eventEndDate = LocalDate.of(2024, 4, 15),
            eventStartDate = LocalDate.of(2024, 4, 14),
            globalRating = 4,
            inscriptionLimitDate = LocalDate.of(2024, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            location = null,
            registeredUsers = mutableListOf(),
            timeBeginning = LocalTime.of(13, 0),
            timeEnding = LocalTime.of(16, 0),
            image = "")
    runBlocking {
      eventDao.update(modifiedEvent)
      val modifiedEventFromDB = eventDao.get("1").await()
      assertEquals(modifiedEventFromDB, modifiedEvent)
    }
  }
}
// used to force waiting for value, since LiveData is asynchronous
suspend fun <T> LiveData<T>.await(): T? {
  var t: T? = null
  val latch = CountDownLatch(1)
  val observer = object : Observer<T> {
    override fun onChanged(value: T) {
      t = value
      latch.countDown()
      this@await.removeObserver(this)
    }
  }
  withContext(Dispatchers.Main) {
    observeForever(observer)
  }
  withContext(Dispatchers.IO) {
    latch.await()
  }
  return value
}