package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Rating
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class RatingFirebaseConnection {
  private val COLLECTION = FirebaseCollection.EVENT_RATINGS.toString().lowercase()
  private val TAG = "RatingFirebaseConnection"

  private fun attendeesRatingsCollection(eventID: String) =
      Firebase.firestore.collection(COLLECTION).document(eventID).collection("attendees_ratings")
  /**
   * Fetches the rating of the user for the event
   *
   * @param eventID the id of the event
   * @param userID the id of the user
   * @return the rating of the user for the event if the user has not rated the event, returns
   *   UNRATED if the event doesn't have any ratings, or that get results in failure, returns null
   */
  suspend fun fetchRating(eventID: String, userID: String): Rating? =
      suspendCancellableCoroutine { continuation ->
        var rating: Rating?

          attendeesRatingsCollection(eventID)
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
              if (document != null) {
                if (document.get("rating") != null) {
                  rating = Rating.fromLong(document.get("rating")!! as Long)
                  Log.d(TAG, "User $userID rated the event $eventID as $rating")
                  continuation.resume(rating)
                } else {
                  continuation.resume(null)
                }
              } else {
                Log.d(TAG, "No such rating")
                continuation.resume(null)
              }
            }
            .addOnFailureListener { exception ->
              Log.d(TAG, "get failed with :", exception)
              continuation.resume(null)
            }
      }

  /**
   * Fetches all the ratings given to the event
   *
   * @param eventID the id of the event
   * @return a map of user id to rating
   */
  suspend fun fetchRatings(eventID: String): Map<String, Rating>? =
      suspendCancellableCoroutine { continuation ->
        attendeesRatingsCollection(eventID)
            .get()
            .addOnSuccessListener { documents ->
              val ratings = mutableMapOf<String, Rating>()
              for (document in documents) {
                if (document.get("rating") != null) {
                  ratings[document.id] = Rating.fromLong(document.get("rating")!! as Long)
                }
              }
              continuation.resume(ratings)
            }
            .addOnFailureListener { exception ->
              Log.d(TAG, "get failed with :", exception)
              continuation.resume(null)
            }
      }

  /**
   * Updates the rating given by the user for the event
   *
   * @param eventID the id of the event
   * @param userID the id of the user
   * @param rating the new value for the rating of the event by the user if the rating is UNRATED,
   *   the rating is deleted
   */
  fun update(eventID: String, userID: String, rating: Rating) {
    if (rating != Rating.UNRATED) {
      Firebase.firestore.collection(COLLECTION).document(eventID).set(mapOf("eventID" to eventID))

      val data: Map<String, Any> = mapOf("rating" to Rating.toLong(rating))

      attendeesRatingsCollection(eventID)
          .document(userID)
          .set(data)
          .addOnSuccessListener {
            Log.d(TAG, "added Rating of event $eventID, of $rating for user $userID")
          }
          .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    } else {
      attendeesRatingsCollection(eventID)
          .document(userID)
          .delete()
          .addOnSuccessListener { Log.d(TAG, "Deleted rating of user $userID for event $eventID") }
          .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
  }
}
