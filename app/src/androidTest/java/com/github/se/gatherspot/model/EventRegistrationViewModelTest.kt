package com.github.se.gatherspot.model

import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.model.event.RegistrationState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class EventRegistrationViewModelTest {

    private lateinit var viewModel: EventRegistrationViewModel

    @Mock
    private lateinit var event: Event

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        // Initialize the general uid for the MainActivity
        MainActivity.uid = "123"
        viewModel = EventRegistrationViewModel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
            /*
             * registerForEvent should return Success when event is not full and user is not already registered`
             */
    fun testSucessWhenNotfull () = runBlockingTest {
        // Arrange
        Mockito.`when`(event.attendanceMaxCapacity).thenReturn(10)
        Mockito.`when`(event.registeredUsers.size).thenReturn(5)
        Mockito.`when`(event.registeredUsers.contains(Mockito.any())).thenReturn(false)

        // Act
        viewModel.registerForEvent(event)

        // Assert
        assertEquals(RegistrationState.Success, viewModel.registrationState.value)
    }

    @Test
    fun errorEventFull() = runBlockingTest {
        // Arrange
        Mockito.`when`(event.attendanceMaxCapacity).thenReturn(10)
        Mockito.`when`(event.registeredUsers.size).thenReturn(10)

        // Act
        viewModel.registerForEvent(event)

        // Assert
        assertEquals(RegistrationState.Error("Event is full"), viewModel.registrationState.value)
    }

    @Test
    fun errorUserAlreadyRegistered() = runBlockingTest {
        // Arrange
        Mockito.`when`(event.attendanceMaxCapacity).thenReturn(10)
        Mockito.`when`(event.registeredUsers.size).thenReturn(5)
        Mockito.`when`(event.registeredUsers.contains(Mockito.any())).thenReturn(true)

        // Act
        viewModel.registerForEvent(event)

        // Assert
        assertEquals(
            RegistrationState.Error("Already registered for this event"),
            viewModel.registrationState.value
        )
    }
}
