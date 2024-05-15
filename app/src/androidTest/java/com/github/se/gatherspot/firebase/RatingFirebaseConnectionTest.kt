package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import com.github.se.gatherspot.model.event.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.text.DecimalFormat

class RatingFirebaseConnectionTest {
  private val ratingFirebaseConnection = RatingFirebaseConnection()
  private val rating = Rating.FIVE_STARS
  private val eventID = "testRating"
  private val userID = "testRater"
  private val secondRater = "testRater2"
  private val secondRating = Rating.FOUR_STARS
  private val firstRating = Rating.ONE_STAR
  private val firstRater = "testRater1"
  private val event = Event(
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
      globalRating = null
  )

  @Before
  fun setup() {
    runTest {
      ratingFirebaseConnection.deleteRating(eventID, userID)
      async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      ratingFirebaseConnection.deleteRating(eventID, firstRater)
      async { ratingFirebaseConnection.fetchRating(eventID, firstRater) }.await()
      ratingFirebaseConnection.deleteRating(eventID, secondRater)
      async { ratingFirebaseConnection.fetchRating(eventID, secondRater) }.await()
    }
  }

  fun tearDown() {
    runTest {
      ratingFirebaseConnection.deleteRating(eventID, userID)
      async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      ratingFirebaseConnection.deleteRating(eventID, firstRater)
      async { ratingFirebaseConnection.fetchRating(eventID, firstRater) }.await()
      ratingFirebaseConnection.deleteRating(eventID, secondRater)
      async { ratingFirebaseConnection.fetchRating(eventID, secondRater) }.await()
    }
  }

  @Test
  fun testRatingUnRatedEvent() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating)
      var fetched: Rating? = null
      async { fetched = ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      assertEquals(rating, fetched)
    }
  }

  @Test
  fun testRatingsMergeCorrectly() {
    runTest {
      ratingFirebaseConnection.update(eventID, firstRater, firstRating)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating)

      var fetched1: Rating? = null
      var fetched2: Rating? = null
      async { fetched1 = ratingFirebaseConnection.fetchRating(eventID, firstRater) }.await()
      async { fetched2 = ratingFirebaseConnection.fetchRating(eventID, secondRater) }.await()

      assertNotNull(fetched1)
      assertNotNull(fetched2)
      // both exist

    }
  }

  @Test
  fun testFetchAttendeesRatings() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating)
      val fetched = async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(rating, fetched?.get(userID))
      assertEquals(secondRating, fetched?.get(secondRater))
      assertEquals(firstRating, fetched?.get(firstRater))
    }
  }

  @Test
  fun testDeleteARating() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating)

      ratingFirebaseConnection.update(eventID, firstRater, Rating.UNRATED)
      val fetched = async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(null, fetched?.get(firstRater))
      assertEquals(rating, fetched?.get(userID))
      assertEquals(secondRating, fetched?.get(secondRater))
    }
  }

  @Test
  fun testRating() {

    runTest {
      val rating = Rating.UNRATED
      val eventID = "testRating"
      val userID = "testRater2"

      ratingFirebaseConnection.update(eventID, userID, rating)
      async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Rating is $rating")
    }
  }

  @Test
  fun testDeleteRating() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating)

      ratingFirebaseConnection.deleteRating(eventID, firstRater)
      val fetched = async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(null, fetched?.get(firstRater))
      assertEquals(rating, fetched?.get(userID))
      assertEquals(secondRating, fetched?.get(secondRater))
    }
  }

  @Test
  fun testFetchEvent(){
    runTest {
      val rating = Rating.FIVE_STARS

      ratingFirebaseConnection.update(eventID, secondRater, rating)
      val fetched = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Rating is $fetched")
      assertNotNull(fetched)
      assertEquals(eventID, fetched?.get("eventID"))
    }
  }

  @Test
  fun testDeleteEvent() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating)

      ratingFirebaseConnection.deleteEvent(eventID)
      val fetchedAttendees = async { ratingFirebaseConnection.fetchAttendeesRatings(eventID) }.await()
      val fetchedEvent = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetchedAttendees.toString()}")
      assertEquals(null, fetchedAttendees)
        assertEquals(null, fetchedEvent)
    }
  }

  @Test
  fun testAggregateAttendeeRatings() {
    runBlocking {
      ratingFirebaseConnection.update(eventID, userID, rating) // testRating testRater 5
      ratingFirebaseConnection.update(eventID, secondRater, secondRating) // testRating testRater2 3
      ratingFirebaseConnection.update(eventID, firstRater, firstRating) // testRating testRater1 1

      delay(400)
      ratingFirebaseConnection.aggregateAttendeeRatings(eventID)
      delay(400)
      val fetched = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "fetched Event is ${fetched.toString()}")
      assertNotNull(fetched)
      val df = DecimalFormat("#.##")
      df.roundingMode = java.math.RoundingMode.HALF_UP
      var expectedAverage : Double = (Rating.toLong(firstRating) + Rating.toLong(rating) + Rating.toLong(secondRating)) / 3.0
      expectedAverage= df.format(expectedAverage).toDouble()

      assertEquals(expectedAverage, fetched?.get("average"))
      assertEquals(3L, fetched?.get("count"))

    }
  }



@Test
fun testUpdateOrganizerRating(){
    runBlocking {
        ratingFirebaseConnection.update(eventID, userID, rating) // testRating testRater 5
        ratingFirebaseConnection.update(eventID, secondRater, secondRating) // testRating testRater2 3
        ratingFirebaseConnection.update(eventID, firstRater, firstRating) // testRating testRater1 1

        delay(400)
        ratingFirebaseConnection.aggregateAttendeeRatings(eventID)
        delay(400)
        val fetchedData = async { ratingFirebaseConnection.fetchEvent(eventID) }.await()
        Log.d("RatingFirebaseConnectionTest", "fetched Event is ${fetchedData.toString()}")
        assertNotNull(fetchedData)
        val df = DecimalFormat("#.##")
        df.roundingMode = java.math.RoundingMode.HALF_UP
        var expectedAverage : Double = (Rating.toLong(firstRating) + Rating.toLong(rating) + Rating.toLong(secondRating)) / 3.0
        expectedAverage= df.format(expectedAverage).toDouble()

        assertEquals(expectedAverage, fetchedData?.get("average"))
        assertEquals(3L, fetchedData?.get("count"))

        ratingFirebaseConnection.updateOrganizerRating(eventID, fetchedData!!)
        val fetched2 = async {ratingFirebaseConnection.fetchOrganizerRatings(event.organizerID)}.await()
        delay(400)
        Log.d("RatingFirebaseConnectionTest", "fetched2  is ${fetched2.toString()}")
        assertNotNull(fetched2)
        assertNotNull(fetched2?.get(eventID))
    }
}


}
