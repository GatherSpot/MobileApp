package com.github.se.gatherspot.firebase

import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EventFirebaseConnectionTest {

  val eventFirebaseConnection = EventFirebaseConnection()

  val event1 =
      Event(
          id = "efcTest1",
          title = "efcTest1Title",
          description = "",
          location = null,
          eventStartDate = null,
          eventEndDate = null,
          timeBeginning = null,
          timeEnding = null,
          attendanceMaxCapacity = null,
          attendanceMinCapacity = 3013,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          eventStatus = EventStatus.CREATED,
          categories = setOf(),
          organizerID = "efcTestOrganizerNOTID",
          registeredUsers = mutableListOf(),
          finalAttendees = mutableListOf(),
          image = "",
          globalRating = null)

  val event2 =
      Event(
          id = "efcTest2",
          title = "efcTest2Title",
          description = "",
          location = null,
          eventStartDate = null,
          eventEndDate = null,
          timeBeginning = null,
          timeEnding = null,
          attendanceMaxCapacity = null,
          attendanceMinCapacity = 3013,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          eventStatus = EventStatus.CREATED,
          categories = setOf(),
          organizerID = "efcTestOrganizerNOTID",
          registeredUsers = mutableListOf(),
          finalAttendees = mutableListOf(),
          image = "",
          globalRating = null)

  @Test
  fun testgetID() {
    val newId = eventFirebaseConnection.getNewID()
    assertNotNull(newId)
    assertTrue(newId.isNotEmpty())
  }

  @Test
  fun testUniquegetID() {
    val newId1 = eventFirebaseConnection.getNewID()
    val newId2 = eventFirebaseConnection.getNewID()
    assertNotNull(newId1)
    assertNotNull(newId2)
    assertNotEquals(newId1, newId2)
  }

  @Test
  fun testAddAndFetchEvent() = runTest {
    val eventID = eventFirebaseConnection.getNewID()
    val event =
        Event(
            id = eventID,
            title = "Test Event",
            description = "This is a test event",
            location = Location(0.0, 0.0, "Test Location"),
            eventStartDate =
                LocalDate.parse(
                    "12/04/2026",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            eventEndDate =
                LocalDate.parse(
                    "12/05/2026",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            timeBeginning =
                LocalTime.parse(
                    "10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            timeEnding =
                LocalTime.parse(
                    "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            attendanceMaxCapacity = 100,
            attendanceMinCapacity = 10,
            inscriptionLimitDate =
                LocalDate.parse(
                    "10/04/2025",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            inscriptionLimitTime =
                LocalTime.parse(
                    "09:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            eventStatus = EventStatus.CREATED,
            categories = setOf(Interests.CHESS),
            registeredUsers = mutableListOf(),
            finalAttendees = mutableListOf(),
            image = "",
            globalRating = null)

    eventFirebaseConnection.add(event)
    var resultEvent: Event? = null
    async { resultEvent = eventFirebaseConnection.fetch(eventID) as Event? }.await()
    assertNotNull(resultEvent)
    assertEquals(resultEvent!!.id, eventID)
    assertEquals(resultEvent!!.title, "Test Event")
    assertEquals(resultEvent!!.description, "This is a test event")
    assertNotNull(resultEvent!!.location)
    assertEquals(resultEvent!!.location!!.latitude, 0.0, 0.000001)
    assertEquals(resultEvent!!.location!!.longitude, 0.0, 0.000001)
    assertEquals(resultEvent!!.location!!.name, "Test Location")
    assertEquals(
        resultEvent!!.eventStartDate,
        LocalDate.parse(
            "12/04/2026",
            DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)))
    assertEquals(
        resultEvent!!.eventEndDate,
        LocalDate.parse(
            "12/05/2026",
            DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)))
    assertEquals(
        resultEvent!!.timeBeginning,
        LocalTime.parse("10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)))
    assertEquals(
        resultEvent!!.timeEnding,
        LocalTime.parse("12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)))
    assertEquals(resultEvent!!.attendanceMaxCapacity, 100)
    assertEquals(resultEvent!!.attendanceMinCapacity, 10)
    assertEquals(
        resultEvent!!.inscriptionLimitDate,
        LocalDate.parse(
            "10/04/2025",
            DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)))
    assertEquals(
        resultEvent!!.inscriptionLimitTime,
        LocalTime.parse("09:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)))
    assertEquals(resultEvent!!.eventStatus, EventStatus.CREATED)
    assertEquals(resultEvent!!.categories, setOf(Interests.CHESS))
    assertEquals(resultEvent!!.registeredUsers!!.size, 0)
    assertEquals(resultEvent!!.finalAttendees!!.size, 0)
    assertEquals(resultEvent!!.image, "")
    eventFirebaseConnection.delete(eventID)
  }

  @Test
  fun fetchReturnsNull() = runTest {
    // Supposing that id will never equal nonexistent
    val event = eventFirebaseConnection.fetch("nonexistent")
    assertEquals(event, null)
  }

  @Test
  fun fetchNextReturnsDistinctEvents() =
      runTest(timeout = Duration.parse("20s")) {
        val round = 5
        val listOfEvents1 = eventFirebaseConnection.fetchNextEvents(round.toLong())
        assert(round >= listOfEvents1.size)
        val listOfEvents2 = eventFirebaseConnection.fetchNextEvents(round.toLong())
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
        testLogin()
        val round = 5
        val interests = listOf(Interests.CHESS, Interests.BASKETBALL)
        val listOfEvents1 =
            eventFirebaseConnection.fetchEventsBasedOnInterests(round.toLong(), interests)
        val listOfEvents2 =
            eventFirebaseConnection.fetchEventsBasedOnInterests(round.toLong(), interests)
        for (i in 0 until listOfEvents1.size) {
          assertNotNull(listOfEvents1[i].categories)
          assertTrue(
              listOfEvents1[i].categories!!.contains(interests[0]) ||
                  listOfEvents1[i].categories!!.contains(interests[1]))
        }
        for (j in 0 until listOfEvents2.size) {
          assertNotNull(listOfEvents2[j].categories)
          assertTrue(
              listOfEvents2[j].categories!!.contains(interests[0]) ||
                  listOfEvents2[j].categories!!.contains(interests[1]))
        }

        testLoginCleanUp()
        eventFirebaseConnection.offset = null
      }

  @Test
  fun fetchNextBasedOnInterestReturnsDistinctEvents() =
      runTest(timeout = Duration.parse("20s")) {
        testLogin()
        val round = 5
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
        testLoginCleanUp()
        eventFirebaseConnection.offset = null
      }

  //    @Test
  //    fun fetchInPerimeter() =
  //
  //        runTest {
  //            val events = eventFirebaseConnection.fetchAllInPerimeter(0.0, 0.0, 1.0)
  //            assert(events.isNotEmpty())
  //        }

  @Test
  fun fetchMyEventsWorks() =
      runTest(timeout = Duration.parse("20s")) {
        testLogin()
        Thread.sleep(10000)
        val events = eventFirebaseConnection.fetchMyEvents()
        assert(
            events.all { event ->
              event.organizerID == FirebaseAuth.getInstance().currentUser!!.uid
            })
        testLoginCleanUp()
      }

  @Test
  fun fetchUpComingWorks() =
      runTest(timeout = Duration.parse("20s")) {
        testLogin()

        Thread.sleep(3000)
        val events = eventFirebaseConnection.fetchUpComing()
        assert(
            events.all { event ->
              event.registeredUsers.contains(FirebaseAuth.getInstance().currentUser!!.uid)
            })
        testLoginCleanUp()
      }

  @Test
  fun fetchAttendedWorks() =
      runTest(timeout = Duration.parse("20s")) {
        testLogin()

        Thread.sleep(3000)
        val events = eventFirebaseConnection.fetchAttended()
        assert(
            events.all { event ->
              (event.finalAttendees!!.contains(FirebaseAuth.getInstance().currentUser!!.uid))
            })
        testLoginCleanUp()
      }

  @Test
  fun fetchEventsFromFollowedWorks() =
      runTest(timeout = Duration.parse("20s")) {
        testLogin()
        val idList = FollowList.following(uid = FirebaseAuth.getInstance().currentUser!!.uid)
        val events = eventFirebaseConnection.fetchEventsFromFollowedUsers(idList.elements)
        assert(events.all { event -> idList.elements.contains(event.organizerID) })
        testLoginCleanUp()
      }

  @Test
  fun testAddRegisteredWorks() {
    runBlocking {
      eventFirebaseConnection.add(event1)
      eventFirebaseConnection.add(event2)
      eventFirebaseConnection.addRegisteredUser(event1.id, "efctestUser")

      delay(400)

      val fetch1 = async { eventFirebaseConnection.fetch(event1.id) as Event }.await()
      val fetch2 = async { eventFirebaseConnection.fetch(event2.id) as Event }.await()

      assert(fetch1.registeredUsers.contains("efctestUser"))
      assert(!fetch2.registeredUsers.contains("efctestUser"))

      eventFirebaseConnection.delete(event1.id)
      eventFirebaseConnection.delete(event2.id)

      delay(400)
    }
  }

  @Test
  fun testAddFinalAttendeeWorks() {
    runBlocking {
      eventFirebaseConnection.add(event1)
      eventFirebaseConnection.add(event2)
      eventFirebaseConnection.addFinalAttendee(event1.id, "efctestUser")

      delay(400)

      val fetch1 = async { eventFirebaseConnection.fetch(event1.id) as Event }.await()
      val fetch2 = async { eventFirebaseConnection.fetch(event2.id) as Event }.await()

      assert(fetch1.finalAttendees?.contains("efctestUser") == true)
      assert(fetch2.finalAttendees?.contains("efctestUser") != true)

      // eventFirebaseConnection.delete(event1.id)
      // eventFirebaseConnection.delete(event2.id)

      delay(400)
    }
  }

  @Test
  fun deleteEvent() = runTest {
    val eventID = eventFirebaseConnection.getNewID()
    val event =
        Event(
            id = eventID,
            title = "Test Event",
            description = "This is a test event",
            location = Location(0.0, 0.0, "Test Location"),
            eventStartDate =
                LocalDate.parse(
                    "12/04/2026",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            eventEndDate =
                LocalDate.parse(
                    "12/05/2026",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            timeBeginning =
                LocalTime.parse(
                    "10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            timeEnding =
                LocalTime.parse(
                    "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            attendanceMaxCapacity = 100,
            attendanceMinCapacity = 10,
            inscriptionLimitDate =
                LocalDate.parse(
                    "10/04/2025",
                    DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED)),
            inscriptionLimitTime =
                LocalTime.parse(
                    "09:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
            eventStatus = EventStatus.CREATED,
            categories = setOf(Interests.CHESS),
            registeredUsers = mutableListOf(),
            finalAttendees = mutableListOf(),
            image = "",
            globalRating = null)

    eventFirebaseConnection.add(event)
    var resultEvent: Event? = null
    async { resultEvent = eventFirebaseConnection.fetch(eventID) }.await()
    assertNotNull(resultEvent)
    assertEquals(resultEvent!!.id, eventID)
    eventFirebaseConnection.delete(eventID)
    async { resultEvent = eventFirebaseConnection.fetch(eventID) }.await()
    assertEquals(resultEvent, null)
  }

  @Test
  fun nullCasesTest() = runTest {
    val eventID = eventFirebaseConnection.getNewID()
    val event =
        Event(
            id = eventID,
            title = "Test Event",
            description = "This is a test event",
            location = null,
            eventStartDate = null,
            eventEndDate = null,
            timeBeginning = null,
            timeEnding = null,
            attendanceMaxCapacity = null,
            attendanceMinCapacity = 10,
            inscriptionLimitDate = null,
            inscriptionLimitTime = null,
            eventStatus = EventStatus.CREATED,
            categories = setOf(Interests.CHESS),
            registeredUsers = mutableListOf(),
            finalAttendees = mutableListOf(),
            globalRating = null,
            image = "")

    eventFirebaseConnection.add(event)
    var resultEvent: Event? = null
    async { resultEvent = eventFirebaseConnection.fetch(eventID) }.await()
    assertNotNull(resultEvent)
    assertEquals(resultEvent!!.id, eventID)
    assertEquals(resultEvent!!.title, "Test Event")
    assertEquals(resultEvent!!.description, "This is a test event")
    assertEquals(resultEvent!!.location, null)
    assertEquals(
        resultEvent!!.eventStartDate, eventFirebaseConnection.EVENT_START_DATE_DEFAULT_VALUE)
    assertEquals(resultEvent!!.eventEndDate, eventFirebaseConnection.EVENT_END_DATE_DEFAULT_VALUE)
    assertEquals(
        resultEvent!!.timeBeginning, eventFirebaseConnection.EVENT_START_TIME_DEFAULT_VALUE)
    assertEquals(resultEvent!!.timeEnding, eventFirebaseConnection.EVENT_END_TIME_DEFAULT_VALUE)
    assertEquals(resultEvent!!.attendanceMaxCapacity, null)
    assertEquals(resultEvent!!.attendanceMinCapacity, 10)
    assertEquals(
        resultEvent!!.inscriptionLimitDate, eventFirebaseConnection.EVENT_END_DATE_DEFAULT_VALUE)
    assertEquals(
        resultEvent!!.inscriptionLimitTime, eventFirebaseConnection.EVENT_END_TIME_DEFAULT_VALUE)
    assertEquals(resultEvent!!.eventStatus, EventStatus.CREATED)
    assertEquals(resultEvent!!.categories, setOf(Interests.CHESS))
    assertEquals(resultEvent!!.registeredUsers!!.size, 0)
    assertEquals(resultEvent!!.finalAttendees!!.size, 0)
    assertEquals(resultEvent!!.image, "")
    eventFirebaseConnection.delete(eventID)
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
