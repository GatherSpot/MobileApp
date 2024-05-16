package com.github.se.gatherspot.utils

import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event

/** Class to handle the connection to the Firebase database for events */
class MockEventFirebaseConnection : EventFirebaseConnection() {

  override suspend fun fetchNextEvents(number: Long): MutableList<Event> {
    return List(number.toInt()) { DefaultEvents.trivialEvent1 }.toMutableList()
  }

  override suspend fun fetchNextEvents(idlist: IdList?, number: Long): MutableList<Event> {
    TODO()
}

  override suspend fun fetchEventsBasedOnInterests(number: Long, l: List<Interests>): MutableList<Event> {
    return List(number.toInt()) {DefaultEvents.withInterests(l[0], eventId = "1")}.toMutableList()
  }

  override suspend fun fetchMyEvents(): MutableList<Event> {
    return List(5){DefaultEvents.withAuthor("TEST","1")}.toMutableList()
  }

  override suspend fun fetchRegisteredTo(): MutableList<Event> {
    return List(5){DefaultEvents.withRegistered("TEST", eventId = "1")}.toMutableList()
  }

  override suspend fun addRegisteredUser(eventID: String, uid: String) {
    return
  }

  override suspend fun add(element: Event) {
    return
  }
}
