package com.github.se.gatherspot.model

import androidx.compose.runtime.getValue
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class EventRegistrationViewModelTest {

  @Test
  fun testRegisterForEventChangeEventListRegistered() {
    // Set global uid
    MainActivity.uid = "test"
    val viewModel = EventRegistrationViewModel()

    val event =
        Event(
            id = "1",
            title = "Event Title",
            description =
                "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
            attendanceMaxCapacity = 10,
            attendanceMinCapacity = 1,
            categories = setOf(Interests.BASKETBALL),
            eventEndDate = LocalDate.of(2024, 4, 15),
            eventStartDate = LocalDate.of(2024, 4, 14),
            globalRating = 4,
            inscriptionLimitDate = LocalDate.of(2024, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            location = null,
            registeredUsers = mutableListOf(),
            timeBeginning = LocalTime.of(13, 0),
            timeEnding = LocalTime.of(16, 0),
        )

    runBlocking { viewModel.registerForEvent(event) }
    assertEquals(event.registeredUsers.size, 1)
  }
}
