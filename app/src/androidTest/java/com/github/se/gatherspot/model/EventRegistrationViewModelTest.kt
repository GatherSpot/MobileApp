package com.github.se.gatherspot.model

import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.model.event.RegistrationState
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class EventRegistrationViewModelTest {
  // This test is working locally but not on the CI. For now, it is commented out.

  @Test
  fun testRegisterForEventChangeEventListRegistered() {
    // Set global uid
    testLogin()

    val viewModel = EventRegistrationViewModel()

    val event =
        Event(
            id = "idTestEvent",
            title = "Event Title",
            description =
                "Hello: I am a description of the event just saying that I would love to say" +
                    "that Messi is not the best player in the world, but I can't. I am sorry.",
            attendanceMaxCapacity = 5,
            attendanceMinCapacity = 1,
            categories = setOf(Interests.BASKETBALL),
            eventEndDate = LocalDate.of(2024, 4, 15),
            eventStartDate = LocalDate.of(2024, 4, 14),
            globalRating = 4,
            inscriptionLimitDate = LocalDate.of(2024, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            location = null,
            registeredUsers = mutableListOf(),
            timeBeginning = LocalTime.of(10, 0),
            timeEnding = LocalTime.of(12, 0),
        )

    runBlocking {
      viewModel.registerForEvent(event)
      delay(2000)
      assertEquals(event.registeredUsers.size, 1)
    }
  }

  @Test
  fun testAlreadyRegistered() {
    testLogin()
    val viewModel = EventRegistrationViewModel()
    val event =
        Event(
            id = "idTestEvent",
            title = "Event Title",
            description =
                "Hello: I am a description of the event just saying that I would love to say" +
                    "that Messi is not the best player in the world, but I can't. I am sorry.",
            attendanceMaxCapacity = 5,
            attendanceMinCapacity = 1,
            categories = setOf(Interests.BASKETBALL),
            eventEndDate = LocalDate.of(2024, 4, 15),
            eventStartDate = LocalDate.of(2024, 4, 14),
            location = null,
            registeredUsers = mutableListOf(FirebaseAuth.getInstance().currentUser!!.uid),

            timeBeginning = LocalTime.of(10, 0),
            timeEnding = LocalTime.of(12, 0),
            globalRating = null,
            inscriptionLimitDate = null,
            inscriptionLimitTime = null,
        )

    viewModel.registerForEvent(event)
    runBlocking {
      delay(1000)
      val error = viewModel.registrationState.value
      assertEquals(error, RegistrationState.Error("Already registered for this event"))
    }
  }
}
