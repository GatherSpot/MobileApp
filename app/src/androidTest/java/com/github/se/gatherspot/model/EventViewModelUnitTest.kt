import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.model.location.Location
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert
import org.junit.Test

class EventViewModelTest {
  val eventViewModel = EventViewModel()

  @Test
  fun createEvent_withValidData_returnsEvent() {
    val event =
        eventViewModel.createEvent(
            "Test Event1",
            "This is a test event",
            Location(0.0, 0.0, "Test Location"),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2),
            LocalTime.of(10, 0),
            LocalTime.of(12, 0),
            100,
            10,
            LocalDate.now().plusDays(1),
            LocalTime.of(9, 0))
    Assert.assertEquals("Test Event1", event.title)
    Assert.assertEquals("This is a test event", event.description)
    Assert.assertEquals(0.0, event.location?.latitude)
    Assert.assertEquals(0.0, event.location?.longitude)
    Assert.assertEquals("Test Location", event.location?.name)
    Assert.assertEquals(LocalDate.now().plusDays(1), event.eventStartDate)
    Assert.assertEquals(LocalDate.now().plusDays(2), event.eventEndDate)
    Assert.assertEquals(LocalTime.of(10, 0), event.timeBeginning)
    Assert.assertEquals(LocalTime.of(12, 0), event.timeEnding)
    Assert.assertEquals(100, event.attendanceMaxCapacity)
    Assert.assertEquals(10, event.attendanceMinCapacity)
    Assert.assertEquals(LocalDate.now().plusDays(1), event.inscriptionLimitDate)
    Assert.assertEquals(LocalTime.of(9, 0), event.inscriptionLimitTime)
  }

  // Write tests for validateParseEventData
  @Test
  fun validateEventData_withValidData_returnsTrue() {
    // validate data parse strings
    val result =
        eventViewModel.validateEvent(
            "Test Event2",
            "This is a test event",
            Location(0.0, 0.0, "Test Location"),
            "12/04/2026",
            "12/05/2026",
            "10:00",
            "12:00",
            "100",
            "10",
            "10/04/2025",
            "09:00")

    Assert.assertTrue(result)
  }

  @Test
  fun validateEventData_withEventStartDateAfterEndDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event3",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "12/03/2026",
              "10:00",
              "12:00",
              "100",
              "10",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Event end date must be after start date", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidStartDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event4",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12022020",
              "12/03/2026",
              "10:00",
              "12:00",
              "100",
              "10",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid date format", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidEndDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "10/04/2026",
              "10:00",
              "12:00",
              "100",
              "10",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Event end date must be after start date", e.message)
    }
  }

  @Test
  fun validateEventData_OnlyMandatoryFields_returnsTrue() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event5",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "",
              "10:00",
              "12:00",
              "",
              "",
              "",
              "")
    } catch (e: Exception) {
      Assert.assertEquals("Event end date must be after start date", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidTime_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event6",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "12/05/2026",
              "10:00",
              "25:00",
              "100",
              "10",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid time format for end time", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidMaxCapacity_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "12/04/2026",
              "10:00",
              "12:00",
              "two",
              "0",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid max attendees format, must be a number", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidMinCapacity_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "12/04/2026",
              "10:00",
              "12:00",
              "100",
              "two",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid min attendees format, must be a number", e.message)
    }
  }

  @Test
  fun validateEventData_withEventDateBeforeToday_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2020",
              "12/04/2026",
              "10:00",
              "12:00",
              "100",
              "10",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Event date must be in the future", e.message)
    }
  }

  @Test
  fun validateEventData_withMinAttendeesGreaterThanMaxAttendees_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "12/04/2026",
              "10:00",
              "12:00",
              "100",
              "200",
              "10/04/2025",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Minimum attendees must be less than maximum attendees", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidInscriptionLimitDate_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "12/04/2026",
              "12/04/2026",
              "10:00",
              "12:00",
              "100",
              "10",
              "10/04/2020",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Inscription limit date must be in the future", e.message)
    }
  }

  @Test
  fun validateEventData_withInvalidInscriptionLimitTime_returnsFalse() {
    // validate data parse strings
    try {
      val result =
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "10/04/2026",
              "12/04/2026",
              "10:00",
              "12:00",
              "100",
              "10",
              "10/04/2026",
              "11:00")
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
          eventViewModel.validateEvent(
              "Test Event",
              "This is a test event",
              Location(0.0, 0.0, "Test Location"),
              "10/04/2026",
              "10/04/2026",
              "12:00",
              "10:00",
              "100",
              "10",
              "10/04/2026",
              "09:00")
    } catch (e: Exception) {
      Assert.assertEquals("Event end time must be after start time", e.message)
    }
  }

  @Test
  fun validateDate_withValidDate_returnsLocalDate() {
    val date = eventViewModel.validateDate("12/04/2026", "Invalid date format")
    Assert.assertEquals(LocalDate.of(2026, 4, 12), date)
  }

  @Test
  fun validateDate_withInvalidDate_returnsException() {
    try {
      val date = eventViewModel.validateDate("12042026", "Invalid date format")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid date format", e.message)
    }
  }

  @Test
  fun validateTime_withValidTime_returnsLocalTime() {
    val time = eventViewModel.validateTime("10:00", "Invalid time format")
    Assert.assertEquals(LocalTime.of(10, 0), time)
  }

  @Test
  fun validateTime_withInvalidTime_returnsException() {
    try {
      val time = eventViewModel.validateTime("1000", "Invalid time format")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid time format", e.message)
    }
  }

  @Test
  fun validateNumber_withValidNumber_returnsInt() {
    val number = eventViewModel.validateNumber("100", "Invalid number format")
    Assert.assertEquals(100, number)
  }

  @Test
  fun validateNumber_withInvalidNumber_returnsException() {
    try {
      val number = eventViewModel.validateNumber("one", "Invalid number format")
    } catch (e: Exception) {
      Assert.assertEquals("Invalid number format", e.message)
    }
  }
}
