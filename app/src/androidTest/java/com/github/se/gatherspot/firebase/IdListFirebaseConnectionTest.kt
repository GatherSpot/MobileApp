package com.github.se.gatherspot.firebase

import com.github.se.gatherspot.model.IdList
import junit.framework.Assert.assertEquals
import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class IdListFirebaseConnectionTest {
  private val tag = FirebaseCollection.REGISTERED_EVENTS
  private val id = "TEST"
  private lateinit var randString1: String
  private lateinit var randString2: String

  @Before
  fun setUp() {
    randString1 = Random.nextInt(1000).toString()
    randString2 = Random.nextInt(1000).toString()
  }

  @After fun cleanUp() = runBlocking { IdListFirebaseConnection().delete(id, tag) }

  @Test
  fun addElementTest() = runBlocking {
    // create a new list
    val idList1 = IdList.new(id, tag, listOf(randString1, randString2))
    // add random value to be sure it isn't stale and we fetch new data
    idList1.add(randString1)
    idList1.add(randString2)
    // save to firebase
    // fetch the data from firebase
    val idList2 = IdListFirebaseConnection().fetch(id, tag)
    // busy wait until values are updated
    assertEquals(idList1.elements.toSet(), idList2.elements.toSet())
  }

  @Test
  fun addTest() = runBlocking {
    val idList1 = IdList.new(id, tag, listOf(randString1, randString2))
    val idList2 = IdList.fromFirebase(id, tag)
    assert(idList1.elements.toSet().intersect(idList2.elements.toSet()) == idList1.elements.toSet())
  }
}
