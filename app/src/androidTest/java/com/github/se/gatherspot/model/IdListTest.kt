package com.github.se.gatherspot.model

import com.github.se.gatherspot.firebase.FirebaseCollection
import junit.framework.TestCase.assertEquals
import org.junit.Test

class IdListTest {
  @Test
  fun addTest() {
    val idList = IdList.empty("id", FirebaseCollection.REGISTERED_EVENTS)
    idList.add("event1")
    idList.add("event2")
    assertEquals(listOf("event1", "event2"), idList.events)
  }

  @Test
  fun removeTest() {
    val idList = IdList.empty("id", FirebaseCollection.REGISTERED_EVENTS)
    idList.add("event1")
    idList.add("event2")
    idList.remove("event1")
    assertEquals(listOf("event2"), idList.events)
  }
}
