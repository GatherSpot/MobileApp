package com.github.se.gatherspot.model

import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.model.event.RegistrationState
import com.github.se.gatherspot.utils.MockEventFirebaseConnection
import com.github.se.gatherspot.utils.MockIdListFirebaseConnection
import com.github.se.gatherspot.utils.MockProfileFirebaseConnection
import java.lang.Thread.sleep
import junit.framework.TestCase.assertEquals
import org.junit.Test

class EventRegistrationViewModelTest {

  @Test
  fun testRegisterForEventChangeEventListRegistered() {
    // Set global uid
    val mockEventFirebaseConnection = MockEventFirebaseConnection()
    val viewModel =
        EventRegistrationViewModel(
            listOf(),
            MockProfileFirebaseConnection(),
            mockEventFirebaseConnection,
            MockIdListFirebaseConnection())

    val event = DefaultEvents.withRegistered(eventId = "1")

    viewModel.registerForEvent(event)
    // wait for coroutine to end (need to wait even without network calls, but it should not be
    // flaky)
    sleep(1000)

    // we only check that the register function got called the right amount of time. If we wanna
    // check the firebase function, we do it there.
    assertEquals(mockEventFirebaseConnection.getRegistered(), 1)
  }

  @Test
  fun testAlreadyRegistered(): Unit {
    val viewModel =
        EventRegistrationViewModel(
            listOf(),
            MockProfileFirebaseConnection(),
            MockEventFirebaseConnection(),
            MockIdListFirebaseConnection())
    val event = DefaultEvents.withRegistered("MC", eventId = "1")

    viewModel.registerForEvent(event)
    val error = viewModel.registrationState
    // wait for coroutine to end (need to wait even without network calls, but it should not be
    // flaky)
    sleep(1000)
    assertEquals(RegistrationState.Error("Already registered for this event"), error.value)
  }
}
