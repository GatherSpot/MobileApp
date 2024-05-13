package com.github.se.gatherspot.model

import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.model.event.RegistrationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.lang.Thread.sleep
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventRegistrationViewModelTest {

  private val eventFirebaseConnection = EventFirebaseConnection()

  @Before fun setUp() = runBlocking { testLogin() }

  @After fun cleanUp() = runBlocking { testLoginCleanUp() }

  @Test
  fun testRegisterForEventChangeEventListRegistered() = runTest {
    // Set global uid

    val viewModel = EventRegistrationViewModel(listOf())

    val event = DefaultEvents.withRegistered(eventId = "1")

    runBlocking { eventFirebaseConnection.add(event) }

    viewModel.registerForEvent(event)

    // sadly did not find a way to do without a sleep and I don't want to waste too much time here
    sleep(4000)
    val result = viewModel.registrationState

    assertEquals(event.registeredUsers.size, 1)

    runBlocking { EventFirebaseConnection().delete(event.id) }
  }

  @Test
  fun testAlreadyRegistered(): Unit = runTest {
    val viewModel = EventRegistrationViewModel(listOf())
    val event = DefaultEvents.withRegistered(Firebase.auth.uid!!, eventId = "1")
    runBlocking { eventFirebaseConnection.add(event) }

    viewModel.registerForEvent(event)
    val error = viewModel.registrationState
    // sadly did not find a way to do without a sleep and I don't want to waste too much time here
    sleep(4000)
    assertEquals(RegistrationState.Error("Already registered for this event"), error.value)

    // To keep a clean database delete the test event
    runBlocking { EventFirebaseConnection().delete("idTestEvent") }
  }
}
