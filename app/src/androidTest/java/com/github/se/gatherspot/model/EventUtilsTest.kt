package com.github.se.gatherspot.model

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.EventAction
import com.github.se.gatherspot.utils.MockEventFirebaseConnection
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class EventUtilsTest {
  private val eventFirebaseConnection = EventFirebaseConnection()
  private val testEvent = DefaultEvents.trivialEvent1
  private val eventUtils = EventUtils(MockEventFirebaseConnection())

  // Write tests for validateParseEventData
  @Test
  fun validateEventData_withValidData_returnsValidEvent() = runTest {
    // validate data parse strings
    val event =
        eventUtils.validateAndCreateOrUpdateEvent(
            "Test Event2",
            "This is a test event",
            Location(0.0, 0.0, "Test Location"),
            "12/04/2026",
            "12/05/2026",
            "10:00",
            "12:00",
            listOf(Interests.ART, Interests.SPORT),
            "100",
            "10",
            "10/04/2025",
            "09:00",
            EventAction.CREATE)

    Assert.assertEquals("Test Event2", event.title)
    Assert.assertEquals("This is a test event", event.description)
    Assert.assertEquals(0.0, event.location?.latitude)
    Assert.assertEquals(0.0, event.location?.longitude)
    Assert.assertEquals("Test Location", event.location?.name)
    Assert.assertEquals(LocalDate.of(2026, 4, 12), event.eventStartDate)
    Assert.assertEquals(LocalDate.of(2026, 5, 12), event.eventEndDate)
    Assert.assertEquals(LocalTime.of(10, 0), event.timeBeginning)
    Assert.assertEquals(LocalTime.of(12, 0), event.timeEnding)
    Assert.assertEquals(100, event.attendanceMaxCapacity)
    Assert.assertEquals(10, event.attendanceMinCapacity)
    Assert.assertEquals(LocalDate.of(2025, 4, 10), event.inscriptionLimitDate)
    Assert.assertEquals(LocalTime.of(9, 0), event.inscriptionLimitTime)
  }

  @Test
  fun validateEventDataForUpdate_withValidData_returnsValidEvent() = runTest {
    // validate data parse strings
    val event =
        eventUtils.validateAndCreateOrUpdateEvent(
            "Test Event2",
            "This is a test event",
            Location(0.0, 0.0, "Test Location"),
            "12/04/2026",
            "12/05/2026",
            "10:00",
            "12:00",
            listOf(Interests.ART, Interests.SPORT),
            "100",
            "10",
            "10/04/2025",
            "09:00",
            EventAction.EDIT,
            testEvent)

    Assert.assertEquals("Test Event2", event.title)
    Assert.assertEquals("This is a test event", event.description)
    Assert.assertEquals(0.0, event.location?.latitude)
    Assert.assertEquals(0.0, event.location?.longitude)
    Assert.assertEquals("Test Location", event.location?.name)
    Assert.assertEquals(LocalDate.of(2026, 4, 12), event.eventStartDate)
    Assert.assertEquals(LocalDate.of(2026, 5, 12), event.eventEndDate)
    Assert.assertEquals(LocalTime.of(10, 0), event.timeBeginning)
    Assert.assertEquals(LocalTime.of(12, 0), event.timeEnding)
    Assert.assertEquals(100, event.attendanceMaxCapacity)
    Assert.assertEquals(10, event.attendanceMinCapacity)
    Assert.assertEquals(LocalDate.of(2025, 4, 10), event.inscriptionLimitDate)
    Assert.assertEquals(LocalTime.of(9, 0), event.inscriptionLimitTime)
  }

  @Test
  fun validateEventData_withEventStartDateAfterEndDate_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event3",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "12/03/2026",
          "10:00",
          "12:00",
          listOf(Interests.ART, Interests.SPORT),
          "100",
          "10",
          "10/04/2025",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Event end date must be after start date", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidStartDate_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event4",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12022020",
          "12/03/2026",
          "10:00",
          "12:00",
          listOf(Interests.ART, Interests.SPORT),
          "100",
          "10",
          "10/04/2025",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Invalid date format", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidEndDate_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "10/04/2026",
          "10:00",
          "12:00",
          listOf(Interests.ART, Interests.SPORT),
          "100",
          "10",
          "10/04/2025",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Event end date must be after start date", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidEndTime_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event5",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "",
          "10:00",
          "12:00",
          emptyList(),
          "",
          "",
          "",
          "",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Event end date must be after start date", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidTime_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event6",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "12/05/2026",
          "10:00",
          "25:00",
          emptyList(),
          "100",
          "10",
          "10/04/2025",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Invalid time format for end time", e.message)
    }
  }

  @Test
  fun validateEventDataForUpdate_withInvalidMaxCapacity_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "12/04/2026",
          "10:00",
          "12:00",
          emptyList(),
          "two",
          "0",
          "10/04/2025",
          "09:00",
          EventAction.EDIT,
          testEvent)
    } catch (e: Exception) {
      Assert.assertEquals("Invalid max attendees format, must be a number", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidMinCapacity_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "12/04/2026",
          "10:00",
          "12:00",
          emptyList(),
          "100",
          "two",
          "10/04/2025",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Invalid min attendees format, must be a number", e.message)
    }
  }

  @Test
  fun validateEventData_withEventDateBeforeToday_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2020",
          "12/04/2026",
          "10:00",
          "12:00",
          emptyList(),
          "100",
          "10",
          "10/04/2025",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Event date must be in the future", e.message)
    }
  }

  @Test
  fun validateEventData_withMinAttendeesGreaterThanMaxAttendees_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "12/04/2026",
          "10:00",
          "12:00",
          listOf(Interests.BASKETBALL),
          "100",
          "200",
          "10/04/2025",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Minimum attendees must be less than maximum attendees", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidInscriptionLimitDate_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "12/04/2026",
          "12/04/2026",
          "10:00",
          "12:00",
          listOf(Interests.BASKETBALL),
          "100",
          "10",
          "10/04/2020",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Inscription limit date must be in the future", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidInscriptionLimitTime_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "10/04/2026",
          "12/04/2026",
          "10:00",
          "12:00",
          listOf(Interests.BASKETBALL),
          "100",
          "10",
          "10/04/2026",
          "11:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals(
          "Inscription limit time must be before event start time on the same day", e.message)
    }
  }

  @Test
  fun validateEventData_withEndTimeBeforeStartTime_returnsFalse() = runTest {
    // validate data parse strings
    try {
      eventUtils.validateAndCreateOrUpdateEvent(
          "Test Event",
          "This is a test event",
          Location(0.0, 0.0, "Test Location"),
          "10/04/2026",
          "10/04/2026",
          "12:00",
          "10:00",
          listOf(Interests.BASKETBALL),
          "100",
          "10",
          "10/04/2026",
          "09:00",
          EventAction.CREATE)
    } catch (e: Exception) {
      Assert.assertEquals("Event end time must be after start time", e.message)
    }
  }

  @Test
  fun validateDate_withValidDate_returnsLocalDate() {
    val date = eventUtils.validateDate("12/04/2026", "Invalid date format")
    Assert.assertEquals(LocalDate.of(2026, 4, 12), date)
  }

  @Test
  fun validateDate_withInvalidDate_returnsException() {
    try {
      eventUtils.validateDate("12042026", "Invalid date format")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid date format", e.message)
    }
  }

  @Test
  fun validateTime_withValidTime_returnsLocalTime() {
    val time = eventUtils.validateTime("10:00", "Invalid time format")
    Assert.assertEquals(LocalTime.of(10, 0), time)
  }

  @Test
  fun validateTime_withInvalidTime_returnsException() {
    try {
      eventUtils.validateTime("1000", "Invalid time format")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid time format", e.message)
    }
  }

  @Test
  fun validateNumber_withValidNumber_returnsInt() {
    val number = eventUtils.validateNumber("100", "Invalid number format")
    Assert.assertEquals(100, number)
  }

  @Test
  fun validateNumber_withInvalidNumber_returnsException() {
    try {
      eventUtils.validateNumber("one", "Invalid number format")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid number format", e.message)
    }
  }

  @Test
  fun testSaveDraftEvent() {
    val eventUtils = EventUtils()
    val context = ApplicationProvider.getApplicationContext<Context>()
    eventUtils.saveDraftEvent(
        "title",
        "description",
        Location(0.0, 0.0, "Malibu"),
        "eventStartDate",
        "eventEndDate",
        "timeBeginning",
        "timeEnding",
        "attendanceMaxCapacity",
        "attendanceMinCapacity",
        "inscriptionLimitDate",
        "inscriptionLimitTime",
        setOf(Interests.SPORT, Interests.FOOTBALL, Interests.BASKETBALL, Interests.TENNIS),
        null,
        context)
    val draftEvent = eventUtils.retrieveFromDraft(context)
    Assert.assertEquals("title", draftEvent?.title)
    Assert.assertEquals("description", draftEvent?.description)
    Assert.assertEquals(0.0, draftEvent?.location?.latitude)
    Assert.assertEquals(0.0, draftEvent?.location?.longitude)
    Assert.assertEquals("Malibu", draftEvent?.location?.name)
    Assert.assertEquals("eventStartDate", draftEvent?.eventStartDate)
    Assert.assertEquals("eventEndDate", draftEvent?.eventEndDate)
    Assert.assertEquals("timeBeginning", draftEvent?.timeBeginning)
    Assert.assertEquals("timeEnding", draftEvent?.timeEnding)
    Assert.assertEquals("attendanceMaxCapacity", draftEvent?.attendanceMaxCapacity)
    Assert.assertEquals("attendanceMinCapacity", draftEvent?.attendanceMinCapacity)
    Assert.assertEquals("inscriptionLimitDate", draftEvent?.inscriptionLimitDate)
    Assert.assertEquals("inscriptionLimitTime", draftEvent?.inscriptionLimitTime)
    Assert.assertEquals(
        setOf(Interests.SPORT, Interests.FOOTBALL, Interests.BASKETBALL, Interests.TENNIS),
        draftEvent?.categories)
    Assert.assertNull(draftEvent?.image)
    eventUtils.deleteDraft(context)
  }

  @Test
  fun deleteDraftEventTest() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val eventUtils = EventUtils()
    eventUtils.saveDraftEvent(
        "title",
        "description",
        Location(0.0, 0.0, "Malibu"),
        "eventStartDate",
        "eventEndDate",
        "timeBeginning",
        "timeEnding",
        "attendanceMaxCapacity",
        "attendanceMinCapacity",
        "inscriptionLimitDate",
        "inscriptionLimitTime",
        setOf(Interests.SPORT, Interests.FOOTBALL, Interests.BASKETBALL, Interests.TENNIS),
        null,
        context)
    eventUtils.retrieveFromDraft(context)!!
    eventUtils.deleteDraft(context)
    val draftEvent = eventUtils.retrieveFromDraft(context)
    Assert.assertNull(draftEvent)
  }

  @Test
  fun eventIsOverTestNotOver() {
    assert(!eventUtils.isEventOver(testEvent))
  }

  @Test
  fun eventIsOverReturnTrue() {
    val event =
        Event(
            id = "testID",
            title = "Test Event",
            description = "This is a test event",
            location = null,
            eventStartDate = LocalDate.of(2020, 4, 12),
            eventEndDate = LocalDate.of(2020, 4, 12),
            timeBeginning = LocalTime.of(10, 0),
            timeEnding = LocalTime.of(12, 0),
            attendanceMaxCapacity = 100,
            attendanceMinCapacity = 10,
            inscriptionLimitDate = LocalDate.of(2020, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            eventStatus = EventStatus.COMPLETED,
            globalRating = null,
            image = "")
    assert(eventUtils.isEventOver(event))
  }
}
