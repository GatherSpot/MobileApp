package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Rating
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
class RatingFirebaseConnectionTest {
    private val ratingFirebaseConnection = RatingFirebaseConnection()
    private val rating = Rating.FIVE_STARS
    private val eventID = "testRating"
    private val userID="testRater"
    private val secondRater = "testRater2"
    private val secondRating = Rating.FOUR_STARS
    private val firstRating = Rating.FIVE_STARS
    private val firstRater = "testRater1"

    @Test
    fun testRatingUnRatedEvent(){
        runTest {

            ratingFirebaseConnection.deleteRatings(eventID)

            ratingFirebaseConnection.update(eventID, userID, rating)
            var fetched : Rating? = null
            async { fetched = ratingFirebaseConnection.fetchRating(eventID, userID)  }.await()
            assertEquals(rating, fetched)
            ratingFirebaseConnection.deleteRatings(eventID)
        }

    }



    @Test
    fun testRatingsMergeCorrectly(){
        runTest {


            ratingFirebaseConnection.deleteRatings(eventID)

            ratingFirebaseConnection.update(eventID, firstRater, firstRating)
            ratingFirebaseConnection.update(eventID, secondRater, secondRating)

            var fetched1 : Rating? = null
            var fetched2 : Rating? = null
            async { fetched1 = ratingFirebaseConnection.fetchRating(eventID, firstRater)  }.await()
            async { fetched2 = ratingFirebaseConnection.fetchRating(eventID, secondRater)  }.await()

            assertNotNull(fetched1)
            assertNotNull(fetched2)
            //both exist

            ratingFirebaseConnection.deleteRatings(eventID)

        }
    }

    @Test
    fun testFetchRatings() {
        runTest {


            ratingFirebaseConnection.deleteRatings(eventID)

            ratingFirebaseConnection.update(eventID, userID, rating)
            ratingFirebaseConnection.update(eventID, secondRater, secondRating)
            val fetched = async { ratingFirebaseConnection.fetchRatings(eventID)  }.await()
            Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
            assertNotNull(fetched)
            assertEquals(rating, fetched?.get(userID))
            assertEquals(secondRating , fetched?.get(secondRater))
            ratingFirebaseConnection.deleteRatings(eventID)
        }
    }


    @Test
    fun testDeleteARating() {
        runTest {
            ratingFirebaseConnection.deleteRatings(eventID)

            ratingFirebaseConnection.update(eventID, userID, rating)
            ratingFirebaseConnection.update(eventID, secondRater, secondRating)
            ratingFirebaseConnection.update(eventID, firstRater, firstRating)

            ratingFirebaseConnection.update(eventID, firstRater, Rating.UNRATED)
            val fetched = async { ratingFirebaseConnection.fetchRatings(eventID)  }.await()
            Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
            assertNotNull(fetched)
            assertEquals(null, fetched?.get(firstRater))
            assertEquals(rating, fetched?.get(userID))
            assertEquals(secondRating , fetched?.get(secondRater))
            ratingFirebaseConnection.deleteRatings(eventID)


        }
    }
    @Test
    fun testDeleteRatings(){
        runTest {
            val rating = Rating.FIVE_STARS
            val eventID = "testRating"
            val userID="testRater1"

            ratingFirebaseConnection.update(eventID, userID, rating)
            ratingFirebaseConnection.deleteRatings(eventID)
            val fetched = async { ratingFirebaseConnection.fetchRatings(eventID)  }.await()
            Log.d("RatingFirebaseConnectionTest", "Ratings are ${fetched.toString()}")
            assertNull(fetched)
            assertEquals(null, fetched?.get(userID))
        }
    }

    @Test
    fun testRating() {

        runTest {
            val rating = Rating.UNRATED
            val eventID = "testRating"
            val userID="testRater2"

            ratingFirebaseConnection.update(eventID, userID, rating)
            async { ratingFirebaseConnection.fetchRating(eventID, userID)  }.await()
            Log.d("RatingFirebaseConnectionTest", "Rating is $rating")

       }

    }
}