package com.github.se.gatherspot

import com.github.se.gatherspot.model.IdList
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.lang.Thread.sleep
import kotlin.random.Random

class IdListFirebaseConnectionTest {
  private val tag = FirebaseCollection.REGISTERED_EVENTS.name
  private val id = "TEST"
  private val randString = Random.nextInt(1000).toString()

  @Test
  fun firebaseTest() {
    //create a new event
    val idList1 = IdList.empty(id, tag)
    //add random value to be sure it isn't stale and we fetch new data
    idList1.add(randString)
    //save to firebase
    IdListFirebaseConnection().saveToFirebase(idList1)
    //wait as a precaution as we don't know how long it will take to save to firebase
    sleep(1000)
    var block = true
    //fetch the data from firebase
    var idList2 = IdListFirebaseConnection().updateFromFirebase(id, tag) { block = false }
    //busy wait until values are updated
    sleep(1000)
    //check if the values are updated and equal
    assertEquals(listOf(randString), idList2.events)
  }
}