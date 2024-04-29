package com.github.se.gatherspot.firebase

import com.github.se.gatherspot.model.IdList
import junit.framework.Assert.assertEquals
import kotlin.random.Random
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

  @After
  fun cleanUp() {
    var done = false
    IdListFirebaseConnection().delete(id, tag) { done = true }
    while (!done) {
      {}
    }
  }

  @Test
  fun addElementTest() {
    // create a new list
    val idList1 = IdList.new(id, tag, listOf(randString1, randString2))
    while (!idList1.isInitialized) {
      {}
    }
    // add random value to be sure it isn't stale and we fetch new data
    idList1.value!!.add(randString1)
    idList1.value!!.add(randString2)
    // save to firebase
    // fetch the data from firebase
    val idList2 = IdListFirebaseConnection().fetch(id, tag) {}
    // busy wait until values are updated
    while (!idList1.isInitialized || !idList2.isInitialized) {
      {}
    }
    assertEquals(idList1.value!!.events.toSet(), idList2.value!!.events.toSet())
  }

  @Test
  fun addTest() {
    val idList1 = IdList.new(id, tag, listOf(randString1, randString2))
    val idList2 = IdList.fromFirebase(id, tag) {}
    while (!idList1.isInitialized || !idList2.isInitialized) {
      {}
    }
    assertEquals(idList1.value!!.events.toSet(), idList2.value!!.events.toSet())
  }
}
