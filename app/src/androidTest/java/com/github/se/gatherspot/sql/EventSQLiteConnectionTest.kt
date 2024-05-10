package com.github.se.gatherspot.sql
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.model.event.Event
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

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
    assertEquals(event1,getVal1)
  }
  @Test
  @Throws(Exception::class)
  fun delete(){
    eventDao.insert(event1, event2)
    eventDao.delete(event1)
    val getVal1 = eventDao.get("1")
    val getVal2 = eventDao.get("2")
    assertNull(getVal1)
    assertNotNull(getVal2)
  }
}
