package com.github.se.gatherspot.sql

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList
import java.io.IOException
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test

class IdListSQLiteConnectionTest {
  private lateinit var idListDao: IdListDao
  private lateinit var db: AppDatabase
  private val idList1 = IdList("TEST", listOf("a", "b"), FirebaseCollection.EVENTS)
  private val idList2 = IdList("TEST2", listOf("c", "d"), FirebaseCollection.REGISTERED_EVENTS)

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    idListDao = db.IdListDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun writeAndRead() {
    idListDao.insert(idList1)
    val getVal = idListDao.get(FirebaseCollection.EVENTS, "TEST")
    assertEquals(idList1.elements.toSet(), getVal.elements.toSet())
  }

  @Test
  @Throws(Exception::class)
  fun delete() {
    idListDao.insert(idList1, idList2)
    idListDao.delete(idList1)
    val getVal1 = idListDao.get(FirebaseCollection.EVENTS, "TEST")
    val getVal2 = idListDao.get(FirebaseCollection.REGISTERED_EVENTS, "TEST2")
    assertNull(getVal1)
    assertNotNull(getVal2)
  }
}
