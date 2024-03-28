import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.model.location.Location
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class EventViewModelTest {

    @Test
    fun createEvent_withValidData_returnsEvent() {
        val event = EventViewModel.createEvent(
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
        val result = EventViewModel.validateParseEventData(
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
}