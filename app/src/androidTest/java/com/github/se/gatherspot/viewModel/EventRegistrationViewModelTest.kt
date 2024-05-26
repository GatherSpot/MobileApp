package com.github.se.gatherspot.viewModel

import android.util.Log
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.eventUI.EventRegistrationViewModel
import com.github.se.gatherspot.ui.eventUI.RegistrationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class EventRegistrationViewModelTest {

  init {
    testLogin()
  }

  @Test
  fun testRegisterForEventChangeEventListRegistered() = runBlocking {
    // Set global uid

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
            image = "")
    val viewModel = EventRegistrationViewModel(event)
    val eventFirebaseConnection = EventFirebaseConnection()
    eventFirebaseConnection.add(event)
    viewModel.changeStatus()
    delay(2000)
    assert(viewModel.registrationState.value is RegistrationState.Registered)
    assertEquals(event.registeredUsers.size, 1)
    EventUtils().deleteEvent(event)
  }

  @Test
  fun testRegisterAndUnregister(): Unit = runBlocking {
    if (Firebase.auth.currentUser == null) Log.d("testAlreadyRegistered", "User is null")
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
            registeredUsers = mutableListOf(),
            timeBeginning = LocalTime.of(10, 0),
            timeEnding = LocalTime.of(12, 0),
            globalRating = null,
            inscriptionLimitDate = null,
            inscriptionLimitTime = null,
            image = "")
    val viewModel = EventRegistrationViewModel(event)
    val eventFirebaseConnection = EventFirebaseConnection()
    eventFirebaseConnection.add(event)
    viewModel.changeStatus()
    delay(2000)
    assert(viewModel.registrationState.value is RegistrationState.Registered)
    viewModel.changeStatus()
    delay(2000)
    assert(viewModel.registrationState.value is RegistrationState.Unregistered)
    // To keep a clean database delete the test event
    EventUtils().deleteEvent(event)
    testLoginCleanUp()
  }
}
