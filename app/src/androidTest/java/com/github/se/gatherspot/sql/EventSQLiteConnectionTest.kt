package com.github.se.gatherspot.sql

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.model.event.Event
import java.io.IOException
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventSQLiteConnectionTest {
  private lateinit var eventDao: EventDao
  private lateinit var db: AppDatabase
  private val event1 = Event.testEvent1
  private val event2 = Event.testEvent2

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
  fun writeAndRead() {
    eventDao.insert(event1)
    val getVal1 = eventDao.get("1")
    assertEquals(event1.title, getVal1.title)
    assertEquals(event1.description, getVal1.description)
    assertEquals(event1.location, getVal1.location)
    assertEquals(event1.eventStartDate, getVal1.eventStartDate)
    assertEquals(event1.eventEndDate, getVal1.eventEndDate)
    assertEquals(event1.timeBeginning, getVal1.timeBeginning)
    assertEquals(event1.timeEnding, getVal1.timeEnding)
    assertEquals(event1.attendanceMaxCapacity, getVal1.attendanceMaxCapacity)
    assertEquals(event1.attendanceMinCapacity, getVal1.attendanceMinCapacity)
    assertEquals(event1.inscriptionLimitDate, getVal1.inscriptionLimitDate)
    assertEquals(event1.inscriptionLimitTime, getVal1.inscriptionLimitTime)
    assertEquals(event1.eventStatus, getVal1.eventStatus)
    assertEquals(event1.categories, getVal1.categories)
    assertEquals(event1.organizerID, getVal1.organizerID)
    assertEquals(event1.registeredUsers, getVal1.registeredUsers)
    assertEquals(event1.finalAttendees, getVal1.finalAttendees)
    assertEquals(event1.globalRating, getVal1.globalRating)
    assertEquals(event1.image, getVal1.image)
    assertEquals(event1.id, getVal1.id)
  }

  @Test
  @Throws(Exception::class)
  fun delete() {
    eventDao.insert(event1, event2)
    eventDao.delete(event1)
    val getVal1 = eventDao.get("1")
    val getVal2 = eventDao.get("2")
    assertNull(getVal1)
    assertNotNull(getVal2)
  }
}
