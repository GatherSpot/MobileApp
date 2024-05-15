package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Rating
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.DecimalFormat

class RatingFirebaseConnection {
  private val EVENT_COLLECTION = FirebaseCollection.EVENT_RATINGS.toString().lowercase()
    private val ORGANIZER_COLLECTION = FirebaseCollection.ORGANIZER_RATINGS.toString().lowercase()
  private val TAG = "RatingFirebaseConnection"

  private fun attendeesRatingsCollection(eventID: String) =
      Firebase.firestore.collection(EVENT_COLLECTION).document(eventID).collection("attendees_ratings")

    private fun organizedEventsCollection(organizerID: String) =
        Firebase.firestore.collection(ORGANIZER_COLLECTION).document(organizerID).collection("organized_events")
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
  suspend fun fetchAttendeesRatings(eventID: String): Map<String, Rating>? =
      suspendCancellableCoroutine { continuation ->
        attendeesRatingsCollection(eventID)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    continuation.resume(null)
                }
                else {
                    val ratings = mutableMapOf<String, Rating>()
                    for (document in documents) {
                        if (document.get("rating") != null) {
                            ratings[document.id] = Rating.fromLong(document.get("rating")!! as Long)
                        }
                    }
                    continuation.resume(ratings)
                }
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
      Firebase.firestore.collection(EVENT_COLLECTION).document(eventID).set(mapOf("eventID" to eventID), SetOptions.merge() )

      val data: Map<String, Any> = mapOf("rating" to Rating.toLong(rating))

      attendeesRatingsCollection(eventID)
          .document(userID)
          .set(data)
          .addOnSuccessListener {
            Log.d(TAG, "added Rating of event $eventID, of $rating for user $userID")
            aggregateAttendeeRatings(eventID)
          }
          .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    } else {
      attendeesRatingsCollection(eventID)
          .document(userID)
          .delete()
          .addOnSuccessListener {
              Log.d(TAG, "Deleted rating of user $userID for event $eventID")
              aggregateAttendeeRatings(eventID)
          }
          .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
  }

    /**
     * Deletes the rating of the user for the event
     * calls update(eventID, userID, Rating.UNRATED)
     * @param eventID the id of the event
     * @param userID the id of the user
     *
     */
    fun deleteRating(eventID: String, userID: String){
        update(eventID, userID, Rating.UNRATED)
    }

    /**
     * Deletes the attendees ratings of the event as well as the event rating document
     * @param eventID the id of the event
     *
     * !! Does not update the organizer rating documents !!
     * This is mostly meant for clean up during test
     */
    fun deleteEventRating(eventID: String){
        runBlocking {
            suspend fun deleteAttendeesRating(eventID: String) : Boolean
                = suspendCancellableCoroutine { continuation ->
                attendeesRatingsCollection(eventID).get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                    continuation.resume(true)
                }
            }
            async {deleteAttendeesRating(eventID)}.await()
            Firebase.firestore.collection(EVENT_COLLECTION).document(eventID).delete()
        }
    }

    /**
     * Fetches the event's ratings base document not the individual ratings
     * @param eventID the id of the event
     * @return the document of the event
     */
    suspend fun fetchEvent(eventID: String) : Map<String, Any>? =
        suspendCancellableCoroutine { continuation ->
            Firebase.firestore.collection(EVENT_COLLECTION).document(eventID).get().addOnSuccessListener { document ->
            if (document.data !=null && document.data!!.isNotEmpty()){
                continuation.resume(document.data!!)
            } else {
                Log.d(TAG, "No such document")
                continuation.resume(null)
            }
        }
    }


    /**
     * Aggregates the attendee ratings of the event and updates the event document with the average rating and the count of ratings
     * @param eventID the id of the event
     * @return the average rating and the count of ratings
     */
    fun aggregateAttendeeRatings(eventID: String){
        val aggregateQuery = attendeesRatingsCollection(eventID).aggregate(
            AggregateField.count(),
            AggregateField.average("rating")
        )
        aggregateQuery.get(AggregateSource.SERVER).addOnSuccessListener { result ->
            Log.d(TAG, "Aggregate query get succeeded")
            val count = result.get(AggregateField.count())
            if (count == 0L) {
                Log.d(TAG, "No ratings for event $eventID")
                return@addOnSuccessListener
            }
            val df = DecimalFormat("#.##")
            df.roundingMode = java.math.RoundingMode.HALF_UP
            var avg = result.get(AggregateField.average("rating"))
            try {
                avg = df.format(avg).toDouble()
            } catch (e: Exception) {
               Log.e(TAG, "Error formatting average rating average is $avg "
                   + "count is $count ", e)
                return@addOnSuccessListener
            }
            Log.d(TAG, "Average rating of event $eventID is $avg")
            Log.d(TAG, "count of event $eventID is $count")
            val data = mapOf("average" to avg, "count" to count)
            Firebase.firestore.collection(EVENT_COLLECTION).document(eventID).set(data, SetOptions.merge())
                .addOnSuccessListener {
                     Log.d(TAG, "Event $eventID updated with average rating $avg and count $count")
                    //updateOrganizerRating(eventID, data)
                }


        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Aggregate query get failed with :", exception)}

    }

    /**
     * Updates the organizer rating document with the average rating and the count of ratings of the event
     * @param eventID the id of the event
     * @param data the data to update the organizer rating document with
     *
     */
    fun updateOrganizerRating(eventID: String, data: Map<String, Any>) {
        runBlocking {
            val eventFirebaseConnection = EventFirebaseConnection()
            val event = async { eventFirebaseConnection.fetch(eventID) }.await() ?: return@runBlocking

            Firebase.firestore.collection(ORGANIZER_COLLECTION).document(event.organizerID).set(mapOf("organizerID" to event.organizerID), SetOptions.merge() )



            organizedEventsCollection(event.organizerID)
                .document(eventID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Organizer ${event.organizerID} updated with average rating ${data["average"]} and count ${data["count"]} for event $eventID")
                    //aggregateOrganizerRatings(event.organizerID)
                }
        }
    }


    /**
     * Fetches the ratings of the the events organized by the organizer
     * @param organizerID the id of the organizer
     * @return a map of event id to the fields of the event document as pairs
     */
    suspend fun fetchOrganizerRatings(organizerID : String) : Map<String, List<Pair<String, Any>>>? =
        suspendCancellableCoroutine { continuation ->
                runBlocking {

                organizedEventsCollection(organizerID)
                    .get()
                    .addOnSuccessListener {
                        documents ->
                        if (documents.isEmpty) {
                            continuation.resume(null)
                        } else {
                            val ratings = mutableMapOf<String, List<Pair<String, Any>>>()
                            for (document in documents) {
                                if (document.data.isNotEmpty()) {
                                    ratings[document.id] = document.data.map{ Pair(it.key, it.value) }
                                }
                            }
                            continuation.resume(ratings)
                        }

                    }
            }
    }

    /*
     THis would be the more appropriate way to aggregate the ratings of the organizer
     however this requires an index on the organized_events collection
     Since this collection is created at runtime and that the index creation is not supported in the emulator
     another solution is created below.

     However, should there be a way to instantiate an index through code relatively quickly this would be the way to go
    fun aggregateOrganizerRatings(organizerID: String) {
        val aggregateQuery = organizedEventsCollection(organizerID).aggregate(
            AggregateField.count(),
            AggregateField.sum("count"),
            AggregateField.average("average")
        )
        val task = aggregateQuery.get(AggregateSource.SERVER)
            task
            .addOnSuccessListener { result ->
            Log.d(TAG, "Aggregate query get succeeded")
            val nEvents = result.get(AggregateField.count())
            val nRatings = result.get(AggregateField.sum("count"))
            val df = DecimalFormat("#.##")
            df.roundingMode = java.math.RoundingMode.HALF_UP
            var avg = result.get(AggregateField.average("average"))
            avg= df.format(avg).toDouble()
            Log.d(TAG, "Average rating of organizer $organizerID is $avg")
            Log.d(TAG, "number of Events by $organizerID is $nEvents")
            Log.d(TAG, "total number of ratings of $organizerID is $nRatings")
            val data = mapOf("overallAverage" to avg, "nEvents" to nEvents, "nRatings" to nRatings)
            Firebase.firestore.collection(ORGANIZER_COLLECTION).document(organizerID).set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Organizer $organizerID updated with average rating $avg and rating count $nRatings")
                }
        }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Aggregate query get failed with :" + exception.localizedMessage)}
    }

     */

    /**
     * Aggregates the ratings of the organizer
     * @param organizerID the id of the organizer
     */
    fun aggregateOrganizerRatings(organizerID: String) {
        runBlocking {
            val ratings = async { fetchOrganizerRatings(organizerID) }.await() ?: return@runBlocking
            val nEvents : Long = ratings.size.toLong()
            var nRatings = 0L
            var avg = 0.0
            Log.d(TAG, "Ratings of organizer $organizerID are $ratings")
            for (rating in ratings.values) {
                for (field in rating){
                    if (field.first == "average") {
                        avg += field.second as Double
                    }
                    if (field.first == "count") {
                        nRatings += field.second as Long
                    }
                }
            }
            avg /= nEvents
            val df = DecimalFormat("#.##")
            df.roundingMode = java.math.RoundingMode.HALF_UP
            avg = df.format(avg).toDouble()
            Log.d(TAG, "Average rating of organizer $organizerID is $avg")
            Log.d(TAG, "number of Events by $organizerID is $nEvents")
            Log.d(TAG, "total number of ratings of $organizerID is $nRatings")
            val data = mapOf("overallAverage" to avg, "nEvents" to nEvents, "nRatings" to nRatings)
            Firebase.firestore.collection(ORGANIZER_COLLECTION).document(organizerID).set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Organizer $organizerID updated with average rating $avg and rating count $nRatings")
                }
        }
    }


    /**
     * Fetches the organizer's overall rating document
     * @param organizerID the id of the organizer
     * @return the document of the organizer
     */
    suspend fun fetchOrganizer(organizerID: String) : Map<String, Any>? =
        suspendCancellableCoroutine { continuation ->
            Firebase.firestore.collection(ORGANIZER_COLLECTION)
                .document(organizerID)
                .get()
                .addOnSuccessListener { document ->
                    if (document.data !=null && document.data!!.isNotEmpty()){
                        continuation.resume(document.data!!)
                    } else {
                        Log.d(TAG, "No such document")
                        continuation.resume(null)
                    }
                }
        }




}
