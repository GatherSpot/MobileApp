package com.github.se.gatherspot.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

public class IdListTest {
  @Test
  fun addTest() {
    val idList = IdList.empty("id", "tag")
    idList.add("event1")
    idList.add("event2")
    assertEquals(listOf("event1", "event2"), idList.events)
  }

  @Test
  fun removeTest() {
    val idList = IdList.empty("id", "tag")
    idList.add("event1")
    idList.add("event2")
    idList.remove("event1")
    assertEquals(listOf("event2"), idList.events)
  }
}
