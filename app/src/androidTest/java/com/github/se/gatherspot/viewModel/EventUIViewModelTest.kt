package com.github.se.gatherspot.viewModel

import android.util.Log
import com.github.se.gatherspot.EnvironmentSetter
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.firebase.RatingFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.ui.eventUI.EventRegistrationViewModel
import com.github.se.gatherspot.ui.eventUI.EventUIViewModel
import com.github.se.gatherspot.ui.eventUI.RegistrationState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EventUIViewModelTest {
  private val ratingFirebaseConnection = RatingFirebaseConnection()
  private val eventFirebaseConnection = EventFirebaseConnection()

  private val event = // isOver
      Event(
          id = "eventUIViewModelTest",
          title = "",
          description = "null",
          location = null,
          eventStartDate = LocalDate.of(2024, 5, 10),
          eventEndDate = LocalDate.of(2024, 5, 11),
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

  private val event2 = // isStarted but is not Over
      Event(
          id = "eventUIViewModelTest2",
          title = "",
          description = "null",
          location = null,
          eventStartDate = LocalDate.of(2024, 5, 10),
          eventEndDate = LocalDate.of(2026, 5, 11),
          timeBeginning = null,
          timeEnding = null,
          attendanceMaxCapacity = null,
          attendanceMinCapacity = 0,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          eventStatus = EventStatus.CREATED,
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
  fun setUp() = runBlocking {
    // Set up the test environment
    EnvironmentSetter.testLogin()
    eventFirebaseConnection.add(event)
    eventFirebaseConnection.add(event2)
    // Unrate events
    ratingFirebaseConnection.update(
        event.id, testLoginUID, Rating.UNRATED, "T1qNNU05QeeqB2OqIBb7GAtQd093")
    ratingFirebaseConnection.update(organizedEvent.id, testLoginUID, Rating.UNRATED, testLoginUID)

    // Add profile to database
    val profile1 = Profile("Melvin", "bio", "image", "T1qNNU05QeeqB2OqIBb7GAtQd093", setOf())
    val profileTestOrganiser = Profile("testOrganiser", "bio", "image", testLoginUID, setOf())
    ProfileFirebaseConnection().add(profile1)
    ProfileFirebaseConnection().add(profileTestOrganiser)
  }

  @After
  fun tearDown() = runTest {
    eventFirebaseConnection.delete(event.id)
    eventFirebaseConnection.delete(event2.id)
  }

  @Test
  fun testRateEvent() {
    runBlocking {
      val uid = Firebase.auth.currentUser?.uid

      val viewModel = EventUIViewModel(event)
      delay(1000)
      assertEquals(Rating.UNRATED, viewModel.ownRating.value)

      viewModel.rateEvent(Rating.FIVE_STARS) // Rate event he didn't attend

      delay(1000)
      assertEquals(Rating.UNRATED, viewModel.ownRating.value)

      // register for the event
      event.registeredUsers.add(uid!!)
      viewModel.rateEvent(Rating.FIVE_STARS) // rate event he registered to
      delay(1000)
      assertEquals(Rating.FIVE_STARS, viewModel.ownRating.value)

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
      assertEquals(Rating.UNRATED, viewModel2.ownRating.value)
      viewModel2.rateEvent(Rating.FIVE_STARS) // rate event he organized
      delay(1000)
      assertEquals(Rating.UNRATED, viewModel2.ownRating.value)
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
      delay(1000)
      assertEquals(false, viewModel.canRate()) // not registered
      viewModel.toggleRegistrationStatus()
      delay(400)
      assertEquals(false, viewModel.canRate()) // registered but not attended
      viewModel.attendEvent()
      delay(1000)
      assertEquals(true, viewModel.attended.value)
      assertEquals(true, viewModel.canRate()) // attended
      val viewModel3 = EventUIViewModel(organizedEvent) // organizer
      delay(1000)
      assertEquals(false, viewModel3.canRate())
    }
  }

  @Test
  fun testCanAttend() {
    runBlocking {
      val viewModel = EventUIViewModel(event)
      delay(1000)
      assertEquals(false, viewModel.canAttend()) // isn't registered
      viewModel.toggleRegistrationStatus()
      delay(1000)
      assertEquals(false, viewModel.canAttend()) // registered and event is over
      val viewModel2 = EventUIViewModel(event2)
      delay(1000)
      assertEquals(false, viewModel2.canAttend()) // isn't registered
      viewModel2.toggleRegistrationStatus()
      delay(1000)
      assertEquals(true, viewModel2.canAttend()) // registered and event is started and not over
      val viewModel3 = EventUIViewModel(organizedEvent)
      delay(1000)
      assertEquals(false, viewModel3.canAttend())
    }
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
    viewModel.toggleRegistrationStatus()
    delay(2000)
    assert(viewModel.registrationState.value is RegistrationState.Registered)
    TestCase.assertEquals(event.registeredUsers.size, 1)
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
    viewModel.toggleRegistrationStatus()
    delay(2000)
    assert(viewModel.registrationState.value is RegistrationState.Registered)
    viewModel.toggleRegistrationStatus()
    delay(2000)
    assert(viewModel.registrationState.value is RegistrationState.Unregistered)
    // To keep a clean database delete the test event
    EventUtils().deleteEvent(event)
  }

  @Test
  fun testAttendAsOrganizer() {
    runBlocking {
      val viewModel = EventUIViewModel(organizedEvent)
      delay(1000)
      assertEquals(false, (viewModel.attended.value == true))
      viewModel.attendEvent()
      delay(1000)
      assertEquals(false, (viewModel.attended.value == true))
    }
  }

  @Test
  fun testAttendEvent() {
    runBlocking {
      eventFirebaseConnection.add(event)
      val viewModel = EventUIViewModel(event)
      delay(1000)
      assertEquals(false, (viewModel.attended.value == true))
      viewModel.attendEvent() // not registered so attempt to attend should fail
      delay(1000)
      assertEquals(false, (viewModel.attended.value == true))

      viewModel.toggleRegistrationStatus()
      delay(400)
      viewModel.attendEvent()
      delay(1000)
      assertEquals(
          true,
          (viewModel.attended.value == true)) // is attending as far as the viewmodel is concerned

      val fetched = async { eventFirebaseConnection.fetch(event.id) }.await()
      assertEquals(true, fetched?.finalAttendees?.contains(testLoginUID) == true)
    }
  }
}
