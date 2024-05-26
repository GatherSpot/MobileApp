package com.github.se.gatherspot.viewModel

import com.github.se.gatherspot.EnvironmentSetter
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.firebase.RatingFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.ui.eventUI.EventUIViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalDate
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
          userName = "organizer",
          bio = "bio",
          image = "image",
          id = "eventUIViewModelTest",
          interests = setOf())
  private val event =
      Event(
          id = "eventUIViewModelTest",
          title = "",
          description = null,
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
    ratingFirebaseConnection.update(
        event.id, testLoginUID, Rating.UNRATED, "T1qNNU05QeeqB2OqIBb7GAtQd093")
    ratingFirebaseConnection.update(organizedEvent.id, testLoginUID, Rating.UNRATED, testLoginUID)

    // Add profile to database
    val profile1 = Profile("organizer", "bio", "image", "T1qNNU05QeeqB2OqIBb7GAtQd093", setOf())
    val profileTestOrganiser = Profile("testOrganiser", "bio", "image", testLoginUID, setOf())
    ProfileFirebaseConnection().add(profile1)
    ProfileFirebaseConnection().add(profileTestOrganiser)
  }

  @After
  fun tearDown() {
    // Clean up the test environment
    EnvironmentSetter.testLoginCleanUp()
    ProfileFirebaseConnection().delete(testLoginUID)
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
}
