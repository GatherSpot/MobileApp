package com.github.se.gatherspot.utils

import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event

/** Class to handle the connection to the Firebase database for events */
class MockEventFirebaseConnection : EventFirebaseConnection() {
  val events =
      listOf<Event>(
          DefaultEvents.trivialEvent1,
          DefaultEvents.withAuthor("MC", eventId = "1"),
          DefaultEvents.withRegistered("MC", eventId = "2"),
          DefaultEvents.withInterests(Interests.CHESS, eventId = "3"),
          DefaultEvents.withInterests(Interests.FOOTBALL, eventId = "4"),
          DefaultEvents.withInterests(Interests.BOWLING, eventId = "5"),
      )

  private var registered = 0
  private var fetchedNextCounter = 0

  fun getRegistered(): Int {
    return registered
  }

  fun getFetchedNext(): Int {
    return fetchedNextCounter
  }

  override suspend fun fetchNextEvents(number: Long): MutableList<Event> {
    fetchedNextCounter++
    return events.take(number.toInt()).toMutableList()
  }

  override suspend fun fetchNextEvents(idlist: IdList?, number: Long): MutableList<Event> {
    TODO()
  }

  override suspend fun fetchEventsBasedOnInterests(
      number: Long,
      l: List<Interests>
  ): MutableList<Event> {
    return List(number.toInt()) { DefaultEvents.withInterests(l[0], eventId = "1") }.toMutableList()
  }

  override suspend fun fetchMyEvents(): MutableList<Event> {
    return List(5) { DefaultEvents.withAuthor("MC", "1") }.toMutableList()
  }

  override suspend fun fetchRegisteredTo(): MutableList<Event> {
    return List(5) { DefaultEvents.withRegistered("TEST", eventId = "1") }.toMutableList()
  }

  override suspend fun addRegisteredUser(eventID: String, uid: String) {
    registered++
    return
  }

  override suspend fun add(element: Event) {
    return
  }
}
