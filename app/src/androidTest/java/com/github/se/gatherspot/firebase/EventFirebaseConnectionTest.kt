package com.github.se.gatherspot.firebase

import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration

class EventFirebaseConnectionTest {

  private val eventFirebaseConnection = EventFirebaseConnection()

  @Test
  fun testGetID() {
    val newId = eventFirebaseConnection.getNewID()
    assertNotNull(newId)
    assertTrue(newId.isNotEmpty())
  }

  @Test
  fun testUniqueGetID() {
    val newId1 = eventFirebaseConnection.getNewID()
    val newId2 = eventFirebaseConnection.getNewID()
    assertNotNull(newId1)
    assertNotNull(newId2)
    assertNotEquals(newId1, newId2)
  }

  @Test
  fun testAddAndFetchEvent() = runTest {
    val event = DefaultEvents.trivialEvent1
    runBlocking {eventFirebaseConnection.add(event)}
    var resultEvent: Event? = null
    runBlocking { resultEvent = eventFirebaseConnection.fetch(event.id) }
    assertEquals(resultEvent, event)
    runBlocking {  eventFirebaseConnection.delete(event.id) }
  }

  @Test
  fun fetchReturnsNull() = runTest {
    // Supposing that id will never equal nonexistent
    var event : Event?
    runBlocking { event = eventFirebaseConnection.fetch("nonexistent")}
    assertEquals(event, null)
  }

  @Test
  fun fetchNextReturnsDistinctEvents() =
      runTest(timeout = Duration.parse("20s")) {
        val round = 5
        lateinit var listOfEvents1 : MutableList<Event>
        runBlocking {
        listOfEvents1 = eventFirebaseConnection.fetchNextEvents(round.toLong())}
        assert(round >= listOfEvents1.size)
        lateinit var listOfEvents2 : MutableList<Event>
        runBlocking {
        listOfEvents2 = eventFirebaseConnection.fetchNextEvents(round.toLong())}
        assert(round >= listOfEvents2.size)
        for (i in 0 until listOfEvents1.size) {
          for (j in 0 until listOfEvents2.size) {
            assertNotEquals(listOfEvents1[i].id, listOfEvents2[j].id)
          }
        }
        eventFirebaseConnection.offset = null
      }

  @Test
  fun fetchNextBasedOnInterestReturnsCorrectEvents() =
      runTest(timeout = Duration.parse("20s")) {
        val events = listOf(
          DefaultEvents.withInterests(Interests.ART,Interests.BASKETBALL, eventId = "1"),
          DefaultEvents.withInterests(Interests.ART,Interests.BASKETBALL, eventId = "2"),
          DefaultEvents.withInterests(Interests.CHESS,Interests.BASKETBALL, eventId = "3"),
          DefaultEvents.withInterests(Interests.ART, eventId = "4"),
          DefaultEvents.withInterests(eventId = "5"),
          DefaultEvents.withInterests(Interests.BOARD_GAMES,Interests.ROLE_PLAY, eventId = "6")
        )
        runBlocking {
          events.forEach{ eventFirebaseConnection.add(it) }
        }
        val round = 2
        val interests = listOf(Interests.CHESS, Interests.BASKETBALL)
        lateinit var listOfEvents1 : MutableList<Event>
        runBlocking {
           listOfEvents1 = eventFirebaseConnection.fetchEventsBasedOnInterests(round.toLong(), interests)
        }
        lateinit var listOfEvents2 : MutableList<Event>
        runBlocking {
           listOfEvents2 = eventFirebaseConnection.fetchEventsBasedOnInterests(round.toLong(), interests)
        }
        //unsafe inside assert is not a problem, since if it is empty the test has indeed failed
        assert(listOfEvents1.all { (interests.union(it.categories!!).isNotEmpty())})
        assert(listOfEvents2.all { interests.union(it.categories!!).isNotEmpty()})
        eventFirebaseConnection.offset = null
        runBlocking {
          testLoginCleanUp()
          events.forEach{eventFirebaseConnection.delete(it.id)} }
      }

  @Test
  fun fetchNextBasedOnInterestReturnsDistinctEvents() =
      runTest(timeout = Duration.parse("20s")) {
        val events = listOf(
          DefaultEvents.withInterests(Interests.ART,Interests.BASKETBALL, eventId = "1"),
          DefaultEvents.withInterests(Interests.ART,Interests.BASKETBALL, eventId = "2"),
          DefaultEvents.withInterests(Interests.CHESS,Interests.BASKETBALL, eventId = "3"),
          DefaultEvents.withInterests(Interests.ART, eventId = "4"),
          DefaultEvents.withInterests(eventId = "5"),
          DefaultEvents.withInterests(Interests.BOARD_GAMES,Interests.ROLE_PLAY, eventId = "6")
        )
        runBlocking {
          events.forEach{ eventFirebaseConnection.add(it) }
        }
        val round = 2
        val interests = listOf(Interests.CHESS, Interests.BASKETBALL)
        val listOfEvents1 =
            eventFirebaseConnection.fetchEventsBasedOnInterests(round.toLong(), interests)
        val listOfEvents2 =
            eventFirebaseConnection.fetchEventsBasedOnInterests(round.toLong(), interests)
        for (i in 0 until listOfEvents1.size) {
          for (j in 0 until listOfEvents2.size) {
            assertNotEquals(listOfEvents1[i].id, listOfEvents2[j].id)
          }
        }
        eventFirebaseConnection.offset = null
        runBlocking {
          events.forEach{eventFirebaseConnection.delete(it.id)} }
      }

  @Test
  fun fetchMyEventsWorks() =
      runTest(timeout = Duration.parse("20s")) {
        runBlocking {testLogin()}
        val myID = FirebaseAuth.getInstance().currentUser!!.uid
        val events = listOf(
          DefaultEvents.withAuthor(myID, eventId = "1"),
          DefaultEvents.withAuthor(myID, eventId = "2"),
          DefaultEvents.withAuthor(myID, eventId = "3"),
          DefaultEvents.withAuthor("2", eventId = "4"),
          DefaultEvents.withAuthor("2",eventId = "5"),
          )
        runBlocking {
          events.forEach{ eventFirebaseConnection.add(it) }
        }
        val resultEvents = eventFirebaseConnection.fetchMyEvents()
        assert(
            resultEvents.all { event ->
              event.organizerID == myID
            })

        runBlocking {
          testLoginCleanUp()
          events.forEach{eventFirebaseConnection.delete(it.id)} }
      }

  @Test
  fun fetchRegisteredToWorks() = runTest{
    runBlocking {testLogin()}
    val myID = FirebaseAuth.getInstance().currentUser!!.uid
    val events = listOf(
      DefaultEvents.withRegistered(myID, eventId = "1"),
      DefaultEvents.withRegistered(myID, eventId = "2"),
      DefaultEvents.withRegistered(myID,"1","2", eventId = "3"),
      DefaultEvents.withRegistered("1","2", eventId = "4"),
      DefaultEvents.withRegistered(eventId = "5"),
    )
      runBlocking {
        events.forEach{ eventFirebaseConnection.add(it) }
      }
    lateinit var resultEvents : MutableList<Event>
    async {
    resultEvents = eventFirebaseConnection.fetchRegisteredTo()}.await()

    assert(resultEvents.all{ it.registeredUsers.contains(myID)})

    runBlocking {
      testLoginCleanUp()
      events.forEach{eventFirebaseConnection.delete(it.id)} }
  }

  @Test
  fun deleteEvent() = runTest {
    val event = DefaultEvents.trivialEvent1
    eventFirebaseConnection.add(event)
    var resultEvent: Event? = null
    runBlocking { resultEvent = eventFirebaseConnection.fetch(event.id) }
    assertNotNull(resultEvent)
    assertEquals(resultEvent!!.id, event.id)
    eventFirebaseConnection.delete(event.id)
    runBlocking { resultEvent = eventFirebaseConnection.fetch(event.id) }
    assertEquals(resultEvent, null)
  }

  @Test
  fun nullCasesTest() = runTest {
    val event = DefaultEvents.nullEvent
    eventFirebaseConnection.add(event)
    var resultEvent: Event? = null
    runBlocking { resultEvent = eventFirebaseConnection.fetch(event.id) }
    assertNotNull(resultEvent)
    assertEquals(resultEvent, event)
    runBlocking { eventFirebaseConnection.delete(event.id) }
  }

  @Test
  fun mapStringToTimeTest() = runTest {
    val time = eventFirebaseConnection.mapTimeStringToTime("Not good format")
    assertEquals(time, null)
  }

  @Test
  fun mapStringToDateTest() = runTest {
    val date = eventFirebaseConnection.mapDateStringToDate("Not good format")
    assertEquals(date, null)
  }
}
