package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Rating
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RatingFirebaseConnection  {
    val COLLECTION = FirebaseCollection.RATINGS.toString().lowercase()
    val TAG = "RatingFirebaseConnection"
    suspend fun fetchRating(eventId: String, uid : String): Rating? = suspendCancellableCoroutine { continuation ->
        var rating : Rating? = Rating.UNRATED

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
                }
                continuation.resume(rating)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with :", exception)
                continuation.resume(null)
            }
    }



    suspend fun fetchRatings(eventID: String) : Map<String, Rating>? = suspendCancellableCoroutine {  continuation ->
        Firebase.firestore
            .collection(COLLECTION)
            .document(eventID)
            .get()
            .addOnSuccessListener { document ->
                val ratings : Map<String, Rating>? = document.data?.mapValues { Rating.valueOf(it.value as String)}
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                continuation.resume(ratings)
            }
            .addOnFailureListener { exception -> Log.d(TAG, "get failed with :", exception)
                continuation.resume(null)
            }
    }

    fun update(eventID: String, userID: String, rating: Rating) {

        var data : Map<String, Any> = mapOf(
            userID to FieldValue.delete()
        )
        if (rating != Rating.UNRATED){
            data = mapOf(
                userID to rating.toString()
            )
        }

        Firebase.firestore
            .collection(COLLECTION)
            .document(eventID)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "added Rating of event $eventID, of $rating for user $userID")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }



    fun deleteRatings(eventID: String){
        Firebase.firestore
            .collection(COLLECTION)
            .document(eventID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }



}
