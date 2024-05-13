package com.github.se.gatherspot.model

import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.model.event.Event
import org.junit.Assert.assertEquals
import org.junit.Test

class EventTest {

  @Test
  fun testToJson() {
    val event = DefaultEvents.trivialEvent1

    val json = event.toJson()
    val resultEvent = Event.fromJson(json)
    assertEquals(event, resultEvent)
  }

  @Test
  fun withEmptyEvent() {
    val event = DefaultEvents.nullEvent

    val json = event.toJson()
    val resultEvent = Event.fromJson(json)
    assertEquals(event, resultEvent)
  }
}
