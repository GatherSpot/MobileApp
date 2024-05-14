package com.github.se.gatherspot.model

import android.util.Log
import com.github.se.gatherspot.EnvironmentSetter
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.RatingFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.event.EventUIViewModel
import com.github.se.gatherspot.model.event.RegistrationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EventUIViewModelTest {
  private val ratingFirebaseConnection = RatingFirebaseConnection()

  private val organizer =
      Profile(
          _userName = "organizer",
          _bio = "bio",
          _image = "image",
          id = "eventUIViewModelTest",
          _interests = setOf())
  private val event =
      Event(
          id = "eventUIViewModelTest",
          title = "",
          description = null,
          location = null,
          eventStartDate = null,
          eventEndDate = null,
          timeBeginning = null,
          timeEnding = null,
          attendanceMaxCapacity = null,
          attendanceMinCapacity = 0,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          eventStatus = EventStatus.COMPLETED,
          categories = setOf(),
          organizerID = "T1qNNU05QeeqB2OqIBb7GAtQd093",
          registeredUsers = mutableListOf(),
          finalAttendees = listOf(),
          image = "",
          globalRating = null)

  private val organizedEvent =
      Event(
          id = "eventUIViewModelTest",
          title = "",
          description = null,
          location = null,
          eventStartDate = null,
          eventEndDate = null,
          timeBeginning = null,
          timeEnding = null,
          attendanceMaxCapacity = null,
          attendanceMinCapacity = 0,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          eventStatus = EventStatus.COMPLETED,
          categories = setOf(),
          organizerID = testLoginUID, // uid of testLogin()
          registeredUsers = mutableListOf(),
          finalAttendees = listOf(),
          image = "",
          globalRating = null)

  @Before
  fun setUp() {
    // Set up the test environment
    EnvironmentSetter.testLogin()
    // Unrate events
    ratingFirebaseConnection.update(event.id, testLoginUID, Rating.UNRATED)
    ratingFirebaseConnection.update(organizedEvent.id, testLoginUID, Rating.UNRATED)
  }

  @After
  fun tearDown() {
    // Clean up the test environment
    EnvironmentSetter.testLoginCleanUp()
  }

  @Test
  fun testRateEvent() {
    runBlocking {
      val uid = Firebase.auth.currentUser?.uid

      val viewModel = EventUIViewModel(event)
      delay(1000)
      assertEquals(Rating.UNRATED, viewModel.rating.value)

      viewModel.rateEvent(Rating.FIVE_STARS) // Rate event he didn't attend

      delay(1000)
      assertEquals(Rating.UNRATED, viewModel.rating.value)

      // register for the event
      event.registeredUsers.add(uid!!)
      viewModel.rateEvent(Rating.FIVE_STARS) // rate event he registered to
      delay(1000)
      assertEquals(Rating.FIVE_STARS, viewModel.rating.value)

      var fetched: Rating? = Rating.ONE_STAR
      async { fetched = ratingFirebaseConnection.fetchRating(event.id, uid) }.await()
      assertEquals(Rating.FIVE_STARS, fetched)
    }
  }

  @Test
  fun testRateEventAsOrganizer() {
    runBlocking {
      // rate event he organized
      val viewModel2 = EventUIViewModel(organizedEvent)
      delay(1000)
      assertEquals(Rating.UNRATED, viewModel2.rating.value)
      viewModel2.rateEvent(Rating.FIVE_STARS) // rate event he organized
      delay(1000)
      assertEquals(Rating.UNRATED, viewModel2.rating.value)
    }
  }

  @Test
  fun testIsOrganizer() {
    runBlocking {
      val viewModel = EventUIViewModel(event)
      delay(1000)
      assertEquals(false, viewModel.isOrganizer())
      val viewModel2 = EventUIViewModel(organizedEvent)
      delay(1000)
      assertEquals(true, viewModel2.isOrganizer())
    }
  }

  @Test
  fun testCanRate() {
    runBlocking {
      val viewModel = EventUIViewModel(event)
      val uid = Firebase.auth.currentUser?.uid
      delay(1000)
      assertEquals(false, viewModel.canRate())
      event.registeredUsers.add(uid!!)
      val viewModel2 = EventUIViewModel(event)
      delay(1000)
      assertEquals(true, viewModel2.canRate())
      val viewModel3 = EventUIViewModel(organizedEvent)
      delay(1000)
      assertEquals(false, viewModel3.canRate())
    }
  }
  // Copy pasted from EventRegistrationViewModelTest.kt (cannot have viewModel as attribute because
  // it must be initialized in the test)
  @Test
  fun testRegisterForEventChangeEventListRegistered() = runBlocking {
    // Set global uid

    val viewModel = EventUIViewModel(event)

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
    val eventFirebaseConnection = EventFirebaseConnection()
    eventFirebaseConnection.add(event)
    viewModel.registerForEvent(event)
    delay(2000)
    TestCase.assertEquals(event.registeredUsers.size, 1)
    EventFirebaseConnection().delete("idTestEvent")
  }

  @Test
  fun testAlreadyRegistered(): Unit = runBlocking {
    if (Firebase.auth.currentUser == null) Log.d("testAlreadyRegistered", "User is null")
    val viewModel = EventUIViewModel(event)
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

    val eventFirebaseConnection = EventFirebaseConnection()
    eventFirebaseConnection.add(event)
    viewModel.registerForEvent(event)
    delay(5000)
    viewModel.registerForEvent(event)
    runBlocking {
      delay(1000)
      val error = viewModel.registrationState.value
      TestCase.assertEquals(RegistrationState.Error("Already registered for this event"), error)
    }

    // To keep a clean database delete the test event
    EventFirebaseConnection().delete("idTestEvent")
    EnvironmentSetter.testLoginCleanUp()
  }
}
