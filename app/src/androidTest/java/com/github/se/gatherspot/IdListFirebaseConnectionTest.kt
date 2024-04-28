package com.github.se.gatherspot

import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.model.IdList
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Test
import kotlin.random.Random

class IdListFirebaseConnectionTest {
  private val tag = FirebaseCollection.REGISTERED_EVENTS
  private val id = "TEST"
  private val randString1 = Random.nextInt(1000).toString()
  private val randString2 = Random.nextInt(1000).toString()

  @After
  fun cleanUp() {
    // clean up the data
    val a = IdListFirebaseConnection().deleteElement(id, tag, randString1)
    val b = IdListFirebaseConnection().deleteElement(id, tag, randString2)
    //wait for tasks to end before continuing
    while (!a.isComplete || b.isComplete) {
      {}
    }
  }

  @Test
  fun firebaseTest() {
    // create a new event
    val idList1 = IdList.empty(id, tag)
    // add random value to be sure it isn't stale and we fetch new data
    idList1.add(randString1)
    idList1.add(randString2)
    // save to firebase
    // wait as a precaution as we don't know how long it will take to save to firebase
    var block = true
    // fetch the data from firebase
    val idList2 = IdListFirebaseConnection().fetch(id, tag) { block = false }
    // busy wait until values are updated
    while (block) {
      {}
    }
    // check if the values are updated and equal
    assertEquals(idList1.events.toSet(), idList2.events.toSet())
  }
}
