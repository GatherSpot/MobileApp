import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.EventAction
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class EventUtilsTest {
  private val EventFirebaseConnection = EventFirebaseConnection()
  private val testEvent =
      Event(
          id = "testID",
          title = "Test Event",
          description = "This is a test event",
          location = Location(0.0, 0.0, "Test Location"),
          eventStartDate =
              LocalDate.parse(
                  "12/04/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          eventEndDate =
              LocalDate.parse(
                  "12/05/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
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
                  "10/04/2025", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          inscriptionLimitTime =
              LocalTime.parse(
                  "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
          eventStatus = EventStatus.CREATED,
          globalRating = null)
  private val eventUtils = EventUtils()

  // Write tests for validateParseEventData
  @Test
  fun validateEventData_withValidData_returnsValidEvent() {
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
  fun validateEventDataForUpdate_withValidData_returnsValidEvent() {
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
  fun validateEventData_withEventStartDateAfterEndDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withInvalidStartDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withInvalidEndDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_OnlyMandatoryFields_returnsTrue() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withInvalidTime_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventDataForUpdate_withInvalidMaxCapacity_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withInvalidMinCapacity_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withEventDateBeforeToday_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withMinAttendeesGreaterThanMaxAttendees_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withInvalidInscriptionLimitDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withInvalidInscriptionLimitTime_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
  fun validateEventData_withEndTimeBeforeStartTime_returnsFalse() {
    // validate data parse strings
    try {
      val result =
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
      val date = eventUtils.validateDate("12042026", "Invalid date format")
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
      val time = eventUtils.validateTime("1000", "Invalid time format")
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
      val number = eventUtils.validateNumber("one", "Invalid number format")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid number format", e.message)
    }
  }

  @Test
  fun deleteEventTest() {
    // create an event, add it to the database, then delete it
    val event =
        Event(
            id = "myEventToDelete",
            title = "Event Title",
            description = "Hello: I am a description",
            attendanceMaxCapacity = 10,
            attendanceMinCapacity = 1,
            categories = setOf(Interests.BASKETBALL),
            organizer = Profile("", "", "", "organizer", setOf()),
            eventEndDate = LocalDate.of(2024, 4, 15),
            eventStartDate = LocalDate.of(2024, 4, 14),
            globalRating = 4,
            inscriptionLimitDate = LocalDate.of(2024, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            location = null,
            registeredUsers = mutableListOf("test"),
            timeBeginning = LocalTime.of(13, 0),
            timeEnding = LocalTime.of(16, 0),
        )
    val eventUtils = EventUtils()
    EventFirebaseConnection.add(event)
    val eventFromDB = runBlocking { EventFirebaseConnection.fetch("myEventToDelete") }
    Assert.assertEquals(event.id, eventFromDB?.id)

    eventUtils.deleteEvent(event)
    val eventFromDBAfterDelete = runBlocking { EventFirebaseConnection.fetch("myEventToDelete") }
    Assert.assertNull(eventFromDBAfterDelete)
  }
}
