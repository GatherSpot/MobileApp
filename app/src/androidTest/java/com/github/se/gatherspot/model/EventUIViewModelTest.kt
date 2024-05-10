package com.github.se.gatherspot.model

import com.github.se.gatherspot.EnvironmentSetter
import com.github.se.gatherspot.firebase.RatingFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.event.EventUIViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
          images = null,
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
            organizerID = "TEST",
            registeredUsers = mutableListOf(),
            finalAttendees = listOf(),
            images = null,
            globalRating = null)

  @Before
  fun setUp() {
    // Set up the test environment
    EnvironmentSetter.testLogin()
      ratingFirebaseConnection.deleteRatings(event.id)
      ratingFirebaseConnection.deleteRatings(organizedEvent.id)

  }

  @After
  fun tearDown() {
    // Clean up the test environment
    EnvironmentSetter.testLoginCleanUp()
      ratingFirebaseConnection.deleteRatings(event.id)
      ratingFirebaseConnection.deleteRatings(organizedEvent.id)
  }

  @Test
  fun testRateEvent() {
    runBlocking {
      val uid = Firebase.auth.currentUser?.uid

      val viewModel = EventUIViewModel(event)
      delay(1000)
      assertEquals(Rating.UNRATED, viewModel.rating.value)

      viewModel.rateEvent(Rating.FIVE_STARS) //Rate event he didn't attend


      delay(1000)
      assertEquals(Rating.UNRATED, viewModel.rating.value)

        //register for the event
        event.registeredUsers.add(uid!!)
        viewModel.rateEvent(Rating.FIVE_STARS) //rate event he registered to
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
        //rate event he organized
        val viewModel2 = EventUIViewModel(organizedEvent)
        delay(1000)
        assertEquals(Rating.UNRATED, viewModel2.rating.value)
        viewModel2.rateEvent(Rating.FIVE_STARS) //rate event he organized
        delay(1000)
        assertEquals(Rating.UNRATED, viewModel2.rating.value)
        }
    }


}
