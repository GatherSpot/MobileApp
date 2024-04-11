package com.github.se.gatherspot

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EventFirebaseConnectionTest {
  @Test
  fun testGetNewEventID() {
    val newId = EventFirebaseConnection.getNewEventID()
    assertNotNull(newId)
    assertTrue(newId.isNotEmpty())
  }
  //
  //  @Test
  //  fun testUniqueGetNewEventID() {
  //    val newId1 = EventFirebaseConnection.getNewEventID()
  //    val newId2 = EventFirebaseConnection.getNewEventID()
  //    assertNotNull(newId1)
  //    assertNotNull(newId2)
  //    assertNotEquals(newId1, newId2)
  //  }
  //
  //  @Test
  //  fun testAddAndFetchEvent() = runTest {
  //    val eventID = EventFirebaseConnection.getNewEventID()
  //    val event =
  //        Event(
  //            eventID = eventID,
  //            title = "Test Event",
  //            description = "This is a test event",
  //            location = Location(0.0, 0.0, "Test Location"),
  //            eventStartDate =
  //                LocalDate.parse(
  //                    "12/04/2026",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
  //            eventEndDate =
  //                LocalDate.parse(
  //                    "12/05/2026",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
  //            timeBeginning =
  //                LocalTime.parse(
  //                    "10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
  //            timeEnding =
  //                LocalTime.parse(
  //                    "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
  //            attendanceMaxCapacity = 100,
  //            attendanceMinCapacity = 10,
  //            inscriptionLimitDate =
  //                LocalDate.parse(
  //                    "10/04/2025",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
  //            inscriptionLimitTime =
  //                LocalTime.parse(
  //                    "09:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
  //            eventStatus = EventStatus.DRAFT,
  //            categories = listOf(Interests.CHESS),
  //            registeredUsers = emptyList(),
  //            finalAttendees = emptyList(),
  //            images = null,
  //            globalRating = null)
  //
  //    EventFirebaseConnection.addNewEvent(event)
  //    var resultEvent: Event? = null
  //    async { resultEvent = EventFirebaseConnection.fetchEvent(eventID) }.await()
  //    assertNotNull(resultEvent)
  //    assertEquals(resultEvent!!.eventID, eventID)
  //    assertEquals(resultEvent!!.title, "Test Event")
  //    assertEquals(resultEvent!!.description, "This is a test event")
  //    assertNotNull(resultEvent!!.location)
  //    assertEquals(resultEvent!!.location!!.latitude, 0.0, 0.000001)
  //    assertEquals(resultEvent!!.location!!.longitude, 0.0, 0.000001)
  //    assertEquals(resultEvent!!.location!!.name, "Test Location")
  //    assertEquals(
  //        resultEvent!!.eventStartDate,
  //        LocalDate.parse(
  //            "12/04/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)))
  //    assertEquals(
  //        resultEvent!!.eventEndDate,
  //        LocalDate.parse(
  //            "12/05/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)))
  //    assertEquals(
  //        resultEvent!!.timeBeginning,
  //        LocalTime.parse("10:00",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)))
  //    assertEquals(
  //        resultEvent!!.timeEnding,
  //        LocalTime.parse("12:00",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)))
  //    assertEquals(resultEvent!!.attendanceMaxCapacity, 100)
  //    assertEquals(resultEvent!!.attendanceMinCapacity, 10)
  //    assertEquals(
  //        resultEvent!!.inscriptionLimitDate,
  //        LocalDate.parse(
  //            "10/04/2025", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)))
  //    assertEquals(
  //        resultEvent!!.inscriptionLimitTime,
  //        LocalTime.parse("09:00",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)))
  //    assertEquals(resultEvent!!.eventStatus, EventStatus.DRAFT)
  //    assertEquals(resultEvent!!.categories, listOf(Interests.CHESS))
  //    assertEquals(resultEvent!!.registeredUsers!!.size, 0)
  //    assertEquals(resultEvent!!.finalAttendees!!.size, 0)
  //    assertEquals(resultEvent!!.images, null)
  //  }
  //
  //  @Test
  //  fun fetchReturnsNull() = runTest {
  //    // Supposing that id will never equal nonexistent
  //    val event = EventFirebaseConnection.fetchEvent("nonexistent")
  //    assertEquals(event, null)
  //  }
  //
  //  @Test
  //  fun deleteEvent() = runTest {
  //    val eventID = EventFirebaseConnection.getNewEventID()
  //    val event =
  //        Event(
  //            eventID = eventID,
  //            title = "Test Event",
  //            description = "This is a test event",
  //            location = Location(0.0, 0.0, "Test Location"),
  //            eventStartDate =
  //                LocalDate.parse(
  //                    "12/04/2026",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
  //            eventEndDate =
  //                LocalDate.parse(
  //                    "12/05/2026",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
  //            timeBeginning =
  //                LocalTime.parse(
  //                    "10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
  //            timeEnding =
  //                LocalTime.parse(
  //                    "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
  //            attendanceMaxCapacity = 100,
  //            attendanceMinCapacity = 10,
  //            inscriptionLimitDate =
  //                LocalDate.parse(
  //                    "10/04/2025",
  // DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
  //            inscriptionLimitTime =
  //                LocalTime.parse(
  //                    "09:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
  //            eventStatus = EventStatus.DRAFT,
  //            categories = listOf(Interests.CHESS),
  //            registeredUsers = emptyList(),
  //            finalAttendees = emptyList(),
  //            images = null,
  //            globalRating = null)
  //
  //    EventFirebaseConnection.addNewEvent(event)
  //    var resultEvent: Event? = null
  //    async { resultEvent = EventFirebaseConnection.fetchEvent(eventID) }.await()
  //    assertNotNull(resultEvent)
  //    assertEquals(resultEvent!!.eventID, eventID)
  //    EventFirebaseConnection.deleteEvent(eventID)
  //    async { resultEvent = EventFirebaseConnection.fetchEvent(eventID) }.await()
  //    assertEquals(resultEvent, null)
  //  }
  //
  //  @Test
  //  fun nullCasesTest() = runTest {
  //    val eventID = EventFirebaseConnection.getNewEventID()
  //    val event =
  //        Event(
  //            eventID = eventID,
  //            title = "Test Event",
  //            description = "This is a test event",
  //            location = null,
  //            eventStartDate = null,
  //            eventEndDate = null,
  //            timeBeginning = null,
  //            timeEnding = null,
  //            attendanceMaxCapacity = null,
  //            attendanceMinCapacity = 10,
  //            inscriptionLimitDate = null,
  //            inscriptionLimitTime = null,
  //            eventStatus = EventStatus.CREATED,
  //            categories = listOf(Interests.CHESS),
  //            registeredUsers = emptyList(),
  //            finalAttendees = emptyList(),
  //            images = null,
  //            globalRating = null)
  //
  //    EventFirebaseConnection.addNewEvent(event)
  //    var resultEvent: Event? = null
  //    async { resultEvent = EventFirebaseConnection.fetchEvent(eventID) }.await()
  //    assertNotNull(resultEvent)
  //    assertEquals(resultEvent!!.eventID, eventID)
  //    assertEquals(resultEvent!!.title, "Test Event")
  //    assertEquals(resultEvent!!.description, "This is a test event")
  //    assertEquals(resultEvent!!.location, null)
  //    assertEquals(resultEvent!!.eventStartDate, null)
  //    assertEquals(resultEvent!!.eventEndDate, null)
  //    assertEquals(resultEvent!!.timeBeginning, null)
  //    assertEquals(resultEvent!!.timeEnding, null)
  //    assertEquals(resultEvent!!.attendanceMaxCapacity, null)
  //    assertEquals(resultEvent!!.attendanceMinCapacity, 10)
  //    assertEquals(resultEvent!!.inscriptionLimitDate, null)
  //    assertEquals(resultEvent!!.inscriptionLimitTime, null)
  //    assertEquals(resultEvent!!.eventStatus, EventStatus.CREATED)
  //    assertEquals(resultEvent!!.categories, listOf(Interests.CHESS))
  //    assertEquals(resultEvent!!.registeredUsers!!.size, 0)
  //    assertEquals(resultEvent!!.finalAttendees!!.size, 0)
  //    assertEquals(resultEvent!!.images, null)
  //    EventFirebaseConnection.deleteEvent(eventID)
  //  }
  //
  //  @Test
  //  fun mapStringToTimeTest() = runTest {
  //    val time = EventFirebaseConnection.mapTimeStringToTime("Not good format")
  //    assertEquals(time, null)
  //  }
  //
  //  @Test
  //  fun mapStringToDateTest() = runTest {
  //    val date = EventFirebaseConnection.mapDateStringToDate("Not good format")
  //    assertEquals(date, null)
  //  }
}
