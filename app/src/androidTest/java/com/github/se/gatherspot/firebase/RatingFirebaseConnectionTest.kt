package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class RatingFirebaseConnectionTest {
  private val ratingFirebaseConnection = RatingFirebaseConnection()
  private val rating = Rating.FIVE_STARS
  private val eventID = "testRating"
  private val userID = "testRater"
  private val secondRater = "testRater2"
  private val secondRating = Rating.FOUR_STARS
  private val firstRating = Rating.FIVE_STARS
  private val firstRater = "testRater1"
  val organizerID = Profile.testOrganizer().id

  @Before
  fun setup() {
    runTest {
      val unrated = Rating.UNRATED
      ratingFirebaseConnection.update(eventID, userID, unrated, organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      ratingFirebaseConnection.update(eventID, firstRater, unrated, organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, firstRater) }.await()
      ratingFirebaseConnection.update(eventID, secondRater, unrated, organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, secondRater) }.await()
    }
  }

  fun tearDown() {
    runTest {
      val unrated = Rating.UNRATED
      ratingFirebaseConnection.update(eventID, userID, unrated, organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      ratingFirebaseConnection.update(eventID, firstRater, unrated, organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, firstRater) }.await()
      ratingFirebaseConnection.update(eventID, secondRater, unrated, organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, secondRater) }.await()
    }
  }

  @Test
  fun testRatingUnRatedEvent() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating, organizerID)
      var fetched: Rating? = null
      async { fetched = ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      assertEquals(rating, fetched)
    }
  }

  @Test
  fun testRatingsMergeCorrectly() {
    runTest {
      ratingFirebaseConnection.update(eventID, firstRater, firstRating, organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, organizerID)

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
  fun testFetchRatings() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating, organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, organizerID)
      val fetched = async { ratingFirebaseConnection.fetchRatings(eventID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
      assertNotNull(fetched)
      assertEquals(rating, fetched?.get(userID))
      assertEquals(secondRating, fetched?.get(secondRater))
    }
  }

  @Test
  fun testDeleteARating() {
    runTest {
      ratingFirebaseConnection.update(eventID, userID, rating, organizerID)
      ratingFirebaseConnection.update(eventID, secondRater, secondRating, organizerID)
      ratingFirebaseConnection.update(eventID, firstRater, firstRating, organizerID)

      ratingFirebaseConnection.update(eventID, firstRater, Rating.UNRATED, organizerID)
      val fetched = async { ratingFirebaseConnection.fetchRatings(eventID) }.await()
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

      ratingFirebaseConnection.update(eventID, userID, rating, organizerID)
      async { ratingFirebaseConnection.fetchRating(eventID, userID) }.await()
      Log.d("RatingFirebaseConnectionTest", "Rating is $rating")
    }
  }
}
