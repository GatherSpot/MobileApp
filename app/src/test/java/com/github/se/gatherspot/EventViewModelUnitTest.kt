import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.model.location.Location
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class EventViewModelTest {
    val eventViewModel = EventViewModel()
    @Test
    fun createEvent_withValidData_returnsEvent() {
        val event = eventViewModel.createEvent(
            "Test Event",
            "This is a test event",
            Location(0.0, 0.0, "Test Location"),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2),
            LocalTime.of(10, 0),
            LocalTime.of(12, 0),
            100,
            10,
            LocalDate.now().plusDays(1),
            LocalTime.of(9, 0)
        )
        Assert.assertEquals("Test Event", event.title)
    }

    // Write tests for validateParseEventData
    @Test
    fun validateEventData_withValidData_returnsTrue() {
        //validate data parse strings
        val result = eventViewModel.validateParseEventData(
            "Test Event",
            "This is a test event",
            Location(0.0, 0.0, "Test Location"),
            "12/04/2026",
            "12/05/2026",
            "10:00",
            "12:00",
            "100",
            "10",
            "10/04/2025",
            "09:00"

        )

        Assert.assertTrue(result)
    }

    @Test
    fun validateEventData_withEventDateAfterStartDate_returnsFalse() {
        //validate data parse strings
        try{
            val result = eventViewModel.validateParseEventData(
                "Test Event",
                "This is a test event",
                Location(0.0, 0.0, "Test Location"),
                "12/04/2026",
                "12/03/2026",
                "10:00",
                "12:00",
                "100",
                "10",
                "10/04/2025",
                "09:00"

            )
        }catch (e: Exception) {
            Assert.assertEquals("Event end date must be after start date", e.message)
        }
    }

    @Test
    fun validateEventData_withInvalidDate_returnsFalse() {
        //validate data parse strings
        try{
        val result = eventViewModel.validateParseEventData(
            "Test Event",
            "This is a test event",
            Location(0.0, 0.0, "Test Location"),
            "12022020",
            "12/03/2026",
            "10:00",
            "12:00",
            "100",
            "10",
            "10/04/2025",
            "09:00"

        )
        }catch (e: Exception) {
            Assert.assertEquals("Invalid date format", e.message)
        }

    }

    @Test
    fun validateEventData_OnlyMandatoryFields_returnsTrue() {
        //validate data parse strings
        try {
            val result = eventViewModel.validateParseEventData(
                "Test Event",
                "This is a test event",
                Location(0.0, 0.0, "Test Location"),
                "12/04/2026",
                "",
                "10:00",
                "12:00",
                "",
                "",
                "",
                ""

            )
        }catch (e: Exception) {
            Assert.assertEquals("Event end date must be after start date", e.message)
        }
    }

}