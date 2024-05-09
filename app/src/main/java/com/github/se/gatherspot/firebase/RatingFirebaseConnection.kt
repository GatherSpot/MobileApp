package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Rating
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class RatingFirebaseConnection {
  val COLLECTION = FirebaseCollection.RATINGS.toString().lowercase()
  val TAG = "RatingFirebaseConnection"

  /**
   * Fetches the rating of the user for the event
   *
   * @param eventId the id of the event
   * @param uid the id of the user
   * @return the rating of the user for the event if the user has not rated the event, returns
   *   UNRATED if the event doesn't have any ratings, or that get results in failure, returns null
   */
  suspend fun fetchRating(eventId: String, uid: String): Rating? =
      suspendCancellableCoroutine { continuation ->
        var rating: Rating? = Rating.UNRATED

        Firebase.firestore
            .collection(COLLECTION)
            .document(eventId)
            .get()
            .addOnSuccessListener { document ->
              if (document != null) {
                if (document.getString(uid) != null) {
                  rating = Rating.valueOf(document.getString(uid)!!)
                  Log.d(TAG, "User $uid rated the event $eventId as ${rating}")
                }
              } else {
                Log.d(TAG, "No such rating")
                continuation.resume(null)
              }
              continuation.resume(rating)
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
        Firebase.firestore
            .collection(COLLECTION)
            .document(eventID)
            .get()
            .addOnSuccessListener { document ->
              val ratings: Map<String, Rating>? =
                  document.data?.mapValues { Rating.valueOf(it.value as String) }
              Log.d(TAG, "DocumentSnapshot data: ${document.data}")
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

    var data: Map<String, Any> = mapOf(userID to FieldValue.delete())
    if (rating != Rating.UNRATED) {
      data = mapOf(userID to rating.toString())
    }

    Firebase.firestore
        .collection(COLLECTION)
        .document(eventID)
        .set(data, SetOptions.merge())
        .addOnSuccessListener {
          Log.d(TAG, "added Rating of event $eventID, of $rating for user $userID")
        }
        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
  }

  /**
   * Deletes all the ratings of the event
   *
   * @param eventID the id of the event
   *
   * Meant to be ran :
   * - in test functions for convenience
   * - typically after a passed event is deleted even though atm we don't do that
   */
  fun deleteRatings(eventID: String) {
    Firebase.firestore
        .collection(COLLECTION)
        .document(eventID)
        .delete()
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
  }
}
