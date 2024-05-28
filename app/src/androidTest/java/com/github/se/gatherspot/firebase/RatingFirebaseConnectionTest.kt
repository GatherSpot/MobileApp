package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import com.github.se.gatherspot.model.event.Event
import java.text.DecimalFormat
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RatingFirebaseConnectionTest {
  private val ratingFirebaseConnection = RatingFirebaseConnection()
  private val rating = Rating.FIVE_STARS
  private val eventID = "testRating"
  private val userID = "testRater"
  private val secondRater = "testRater2"
  private val secondRating = Rating.FOUR_STARS
  private val firstRating = Rating.ONE_STAR
  private val firstRater = "testRater1"
  val organizerID = Profile.testOrganizer().id

  private val event1 =
      Event(
          id = eventID,
          title = eventID,
          description = eventID,
          location = null,
          eventStartDate = null,
          eventEndDate = null,
          timeBeginning = null,
          timeEnding = null,
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 0,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          categories = setOf(),
          organizerID = Profile.testOrganizer().id,
          registeredUsers = mutableListOf("testRating"),
          finalAttendees = listOf("testRating"),
          image = "testRating",
          globalRating = null)

  private val eventID2 = "testRating2"

  private val event2 =
      Event(
          id = eventID2,
          title = eventID2,
          description = eventID2,
          location = null,
          eventStartDate = null,
          eventEndDate = null,
          timeBeginning = null,
          timeEnding = null,
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 0,
          inscriptionLimitDate = null,
          inscriptionLimitTime = null,
          categories = setOf(),
          organizerID = Profile.testOrganizer().id,
          registeredUsers = mutableListOf("testRating"),
          finalAttendees = listOf("testRating"),
          image = "testRating",
          globalRating = null)

  @Before
  fun setup() {
    runTest {
      ratingFirebaseConnection.deleteEventRating(eventID)
      ratingFirebaseConnection.deleteEventRating(eventID2)
      ratingFirebaseConnection.deleteOrganizer(event1.organizerID)
      async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      async { ratingFirebaseConnection.fetchEvent(eventID2) }.await()
      async { ratingFirebaseConnection.fetchOrganizer(event1.organizerID) }.await()
    }
  }

  fun tearDown() {
    runTest {
      ratingFirebaseConnection.deleteEventRating(eventID)
      ratingFirebaseConnection.deleteEventRating(eventID2)
      async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      async { ratingFirebaseConnection.fetchEvent(eventID2) }.await()
    }
  }

  fun testRatingUnRatedEvent() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating, event1.organizerID)
      var fetched: Rating? = null
      async { fetched = ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      assertEquals(rating, fetched)
    }
  }

  fun testRatingsMergeCorrectly() {
    runTest {
      ratingFirebaseConnection.update(eventID, firstRater, firstRating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, event1.organizerID)

      var fetched1: Rating? = null
      var fetched2: Rating? = null
      async { fetched1 = ratingFirebaseConnection.fetchRating(eventID, firstRater) }.await()
      async { fetched2 = ratingFirebaseConnection.fetchRating(eventID, secondRater) }.await()

      assertNotNull(fetched1)
      assertNotNull(fetched2)
      // both exist

    }
  }

  fun testFetchAttendeesRatings() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating, event1.organizerID)
      val fetched = async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(rating, fetched?.get(userID))
      assertEquals(secondRating, fetched?.get(secondRater))
      assertEquals(firstRating, fetched?.get(firstRater))
    }
  }

  fun testDeleteARating() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating, event1.organizerID)

      ratingFirebaseConnection.update(eventID, firstRater, Rating.UNRATED, event1.organizerID)
      val fetched = async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(null, fetched?.get(firstRater))
      assertEquals(rating, fetched?.get(userID))
      assertEquals(secondRating, fetched?.get(secondRater))
    }
  }

  fun testRating() {

    runTest {
      val rating = Rating.UNRATED
      val eventID = "testRating"
      val userID = "testRater2"

      ratingFirebaseConnection.update(eventID, userID, rating, event1.organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Rating is $rating")
    }
  }

  fun testDeleteRating() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating, event1.organizerID)

      ratingFirebaseConnection.deleteRating(eventID, firstRater, event1.organizerID)
      val fetched = async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(null, fetched?.get(firstRater))
      assertEquals(rating, fetched?.get(userID))
      assertEquals(secondRating, fetched?.get(secondRater))
    }
  }

  fun testFetchEvent() {
    runBlocking {
      val rating = Rating.FIVE_STARS

      ratingFirebaseConnection.update(eventID, secondRater, rating, event1.organizerID)
      delay(2000)
      val fetched = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Rating is $fetched")
      assertNotNull(fetched)
      assertEquals(eventID, fetched?.get("eventID"))
    }
  }

  fun testDeleteEvent() {
    runBlocking {
      ratingFirebaseConnection.update(eventID, userID, rating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, event1.organizerID)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating, event1.organizerID)

      delay(2000)
      ratingFirebaseConnection.deleteEventRating(eventID)
      delay(1000)
      val fetchedAttendees =
          async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      val fetchedEvent = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetchedAttendees.toString()}")
      assertEquals(null, fetchedAttendees)
      assertEquals(null, fetchedEvent)
    }
  }

  fun testAggregateAttendeeRatings() {
    runBlocking {
      ratingFirebaseConnection.update(
          eventID, userID, rating, event1.organizerID) // testRating testRater 5
      ratingFirebaseConnection.update(
          eventID, secondRater, secondRating, event1.organizerID) // testRating testRater2 3
      ratingFirebaseConnection.update(
          eventID, firstRater, firstRating, event1.organizerID) // testRating testRater1 1

      delay(400)
      ratingFirebaseConnection.aggregateAttendeeRatings(eventID, event1.organizerID)
      delay(1000)
      val fetched = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "fetched Event is ${fetched.toString()}")
      assertNotNull(fetched)
      val df = DecimalFormat("#.##")
      df.roundingMode = java.math.RoundingMode.HALF_UP
      var expectedAverage: Double =
          (Rating.toLong(firstRating) + Rating.toLong(rating) + Rating.toLong(secondRating)) / 3.0
      expectedAverage = df.format(expectedAverage).toDouble()

      assertEquals(expectedAverage, fetched?.get("average"))
      assertEquals(3L, fetched?.get("count"))
    }
  }

  fun testUpdateOrganizerRating() {
    runBlocking {
      ratingFirebaseConnection.update(
          eventID, userID, rating, event1.organizerID) // testRating testRater 5
      ratingFirebaseConnection.update(
          eventID, secondRater, secondRating, event1.organizerID) // testRating testRater2 3
      ratingFirebaseConnection.update(
          eventID, firstRater, firstRating, event1.organizerID) // testRating testRater1 1

      delay(400)
      // ratingFirebaseConnection.aggregateAttendeeRatings(eventID)
      delay(400)
      val fetchedData = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "fetched Event is ${fetchedData.toString()}")
      assertNotNull(fetchedData)
      val df = DecimalFormat("#.##")
      df.roundingMode = java.math.RoundingMode.HALF_UP
      var expectedAverage: Double =
          (Rating.toLong(firstRating) + Rating.toLong(rating) + Rating.toLong(secondRating)) / 3.0
      expectedAverage = df.format(expectedAverage).toDouble()

      assertEquals(expectedAverage, fetchedData?.get("average"))
      assertEquals(3L, fetchedData?.get("count"))

      ratingFirebaseConnection.updateOrganizerRating(eventID, fetchedData!!, event1.organizerID)
      val fetched2 =
          async { ratingFirebaseConnection.fetchOrganizerRatings(event1.organizerID) }.await()
      delay(400)
      Log.d("RatingFirebaseConnectionTest", "fetched2  is ${fetched2.toString()}")
      assertNotNull(fetched2)
      assertEquals(
          fetchedData.map { it -> Pair(it.key, it.value) }.sortedBy { it.first },
          fetched2?.get(eventID)?.sortedBy { it.first })
    }
  }

  fun testAggregateOrganizerRatings() {
    runBlocking {
      ratingFirebaseConnection.update(
          eventID, userID, rating, event1.organizerID) // testRating testRater 5
      ratingFirebaseConnection.update(
          eventID, secondRater, secondRating, event1.organizerID) // testRating testRater2 3
      ratingFirebaseConnection.update(
          eventID, firstRater, firstRating, event1.organizerID) // testRating testRater1 1
      ratingFirebaseConnection.update(
          eventID2, userID, rating, event1.organizerID) // testRating2 testRater 5
      ratingFirebaseConnection.update(
          eventID2, secondRater, secondRating, event1.organizerID) // testRating2 testRater2 3
      delay(1000)

      ratingFirebaseConnection.aggregateAttendeeRatings(eventID, event1.organizerID)
      ratingFirebaseConnection.aggregateAttendeeRatings(eventID2, event1.organizerID)
      delay(1000)

      val data1 = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      val data2 = async { ratingFirebaseConnection.fetchEvent(eventID2) }.await()

      ratingFirebaseConnection.updateOrganizerRating(eventID, data1!!, event1.organizerID)
      ratingFirebaseConnection.updateOrganizerRating(eventID2, data2!!, event1.organizerID)

      delay(1000)

      ratingFirebaseConnection.aggregateOrganizerRatings(event1.organizerID)
      delay(1000)

      Log.d("RatingFirebaseConnectionTest", "fetching Organizer ${event1.organizerID}")
      val fetched = async { ratingFirebaseConnection.fetchOrganizer(event1.organizerID) }.await()
      Log.d("RatingFirebaseConnectionTest", "fetched Organizer is ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(
          3.92, fetched?.get("overallAverage")) // Hard coded change value if you change the vals
      assertEquals(2L, fetched?.get("nEvents"))
      assertEquals(5L, fetched?.get("nRatings"))
    }
  }

  @Test
  fun testEndToEndRating() {

    runBlocking {
      ratingFirebaseConnection.update(
          eventID, userID, rating, event1.organizerID) // testRating testRater 5
      ratingFirebaseConnection.update(
          eventID, secondRater, secondRating, event1.organizerID) // testRating testRater2 3
      ratingFirebaseConnection.update(
          eventID, firstRater, firstRating, event1.organizerID) // testRating testRater1 1
      ratingFirebaseConnection.update(
          eventID2, userID, rating, event1.organizerID) // testRating2 testRater 5
      ratingFirebaseConnection.update(
          eventID2, secondRater, secondRating, event1.organizerID) // testRating2 testRater2 3
      val fetchedRating = async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      assertEquals(rating, fetchedRating)
      ratingFirebaseConnection.deleteRating(eventID, userID, organizerID)
      assertEquals(null, async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await())

      ratingFirebaseConnection.update(eventID, userID, rating, event1.organizerID)

      delay(3000)

      val event1Attendees =
          async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      val event2Attendees =
          async { ratingFirebaseConnection.fetchAttendeesRatings(eventID2) }.await()

      assertEquals(3, event1Attendees?.size)
      assertEquals(rating, event1Attendees?.get(userID))
      assertEquals(firstRating, event1Attendees?.get(firstRater))
      assertEquals(secondRating, event1Attendees?.get(secondRater))
      assertEquals(2, event2Attendees?.size)
      assertEquals(rating, event2Attendees?.get(userID))
      assertEquals(secondRating, event2Attendees?.get(secondRater))

      // Ratings are correctly updated and fetched

      val event1Data = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      val event2Data = async { ratingFirebaseConnection.fetchEvent(eventID2) }.await()
      val event1Average = async { ratingFirebaseConnection.fetchEventGlobalRating(eventID) }.await()

      assertEquals(3.33, event1Data?.get("average"))
      assertEquals(3.33, event1Average)
      assertEquals(3L, event1Data?.get("count"))
      assertEquals(eventID, event1Data?.get("eventID"))
      assertEquals(4.5, event2Data?.get("average"))
      assertEquals(2L, event2Data?.get("count"))
      assertEquals(eventID2, event2Data?.get("eventID"))

      // aggregateAttendeeRatings works and fetchEvent works

      val fetchedOrganizerRatings =
          async { ratingFirebaseConnection.fetchOrganizerRatings(event1.organizerID) }.await()

      assertEquals(2, fetchedOrganizerRatings?.size)
      assertEquals(
          setOf(event1Data?.map { Pair(it.key, it.value) }),
          setOf(fetchedOrganizerRatings?.get(eventID)))
      assertEquals(
          setOf(event2Data?.map { Pair(it.key, it.value) }),
          setOf(fetchedOrganizerRatings?.get(eventID2)))

      // updateOrganizerRating works and fetchOrganizerRatings works

      val fetchedOrganizer =
          async { ratingFirebaseConnection.fetchOrganizer(event1.organizerID) }.await()
      val organizerGlobal =
          async { ratingFirebaseConnection.fetchOrganizerGlobalRating(event1.organizerID) }.await()

      assertNotNull(fetchedOrganizer)
      assertEquals(
          3.92,
          fetchedOrganizer?.get("overallAverage")) // Hard coded change value if you change the vals
      assertEquals(2L, fetchedOrganizer?.get("nEvents"))
      assertEquals(5L, fetchedOrganizer?.get("nRatings"))
      assertEquals(3.92, organizerGlobal)

      ratingFirebaseConnection.deleteOrganizer(event1.organizerID)
      ratingFirebaseConnection.deleteEventRating(eventID)
      delay(1000)

      val fetchOrganizerAfterDelete =
          async { ratingFirebaseConnection.fetchOrganizerGlobalRating(event1.organizerID) }.await()
      val fetchEventAfterDelete =
          async { ratingFirebaseConnection.fetchEventGlobalRating(eventID) }.await()
      assertNull(fetchOrganizerAfterDelete)
      assertNull(fetchEventAfterDelete)
    }
  }
}
