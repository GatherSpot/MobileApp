package com.github.se.gatherspot

import android.annotation.SuppressLint
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * Class to handle the connection to the Firebase database //TODO maybe 1 firebase for event and 1 for user etc
 */
class EventFirebaseConnection {
    @SuppressLint("StaticFieldLeak")
    companion object {
        private val TAG = "FirebaseConnection" //Used for debugging/logs
        private val EVENTS = "events" //Collection name for events
        private val firebase = FirebaseDatabase.getInstance().getReference()

        /**
        * Creates a unique new identifier
        * This function can be used for both Event IDs and User IDs
         *
        * @return A unique identifier
        */
        fun getNewEventID(): String {
            return firebase.child(EVENTS).push().key!!
        }

        /**
         * Fetches an event from the database
         *
         * @param eventID: the unique identifier of the event
         * @return The Event object
         */
        suspend fun fetchEvent(eventID: String): Event = suspendCancellableCoroutine { continuation ->
            Firebase.firestore
                .collection(EVENTS)
                .document(eventID)
                .get()
                .addOnSuccessListener { result ->
                    val event = mapDocumentToEvent(result)
                    continuation.resume(event)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

        /**
         * Maps a document to an Event object
         *
         * @param document: The document to map
         * @return The Event object
         */
        private fun mapDocumentToEvent(document: DocumentSnapshot): Event{
            val eventID = document.getString("eventID") as String
            val title = document.getString("title") as String
            val description = document.getString("description") as String
            val location: Location?
            val location_name = document.getString("location_name")
            location = if (location_name == "") {
                null
            } else {
                Location(
                    latitude = document.get("location_latitude") as Double,
                    longitude = document.get("location_latitude") as Double,
                    name = document.getString("location_name") as String)
            }
            var date = document.getString("eventDate") as String
            val eventDate =
                when (date) {
                "null" -> null
                else ->
                    try {
                        LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } catch (e: Exception) {
                        null
                    }
                }
            val timeBeginning = null //TODO: Question whether an event can last longer than 1 day, If yes implement startEventDate and endEventDate
            val timeEnding = null //TODO same as above
            val capacity = document.getString("attendanceCapacity")
            val attendanceCapacity = when(capacity) {
                "Unlimited" -> null
                else -> capacity?.toInt()
            }//TODO: MIN ATTENDIES?
            date = document.getString("inscriptionLimitDate") as String
            val inscriptionLimitDate =
                when (date) {
                    "null" -> null
                    else ->
                        try {
                            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        } catch (e: Exception) {
                            null
                        }
                } //TODO inscription date time
            val status = document.getString("eventStatus") as String
            val eventStatus: EventStatus? =
                when (status) {
                    "CREATED" -> EventStatus.CREATED
                    "DRAFT" -> EventStatus.DRAFT
                    "ON_GOING" -> EventStatus.ON_GOING
                    "COMPLETED" -> EventStatus.COMPLETED
                    else -> null
                }
            val category = document.get("category") as List<String>
            val registeredUsers = document.get("finalAttendee") as List<String>
            val finalAttendee = document.get("finalAttendee") as List<String>
            val images = null //TODO: Retrieve images from database
            val rating = document.getString("globalRating") as String
            val globalRating = when(rating) {
                "" -> null
                else -> rating.toInt()
            }
            return Event(
                            eventID = eventID,
                            title = title,
                            description = description,
                            location = location,
                            eventDate = eventDate,
                            timeBeginning = timeBeginning,
                            timeEnding = timeEnding,
                            attendanceCapacity = attendanceCapacity,
                            inscriptionLimitDate = inscriptionLimitDate,
                            eventStatus = eventStatus!!,
                            category = category,
                            registeredUsers = registeredUsers,
                            finalAttendees = finalAttendee,
                            images = images,
                            globalRating = globalRating
                        )
        }

        /**
         * Adds an event to the database
         *
         * @param event: The event to add
         */
        fun addNewEvent(event: Event) {
            val eventItem =
                hashMapOf(
                    "eventID" to event.eventID,
                    "title" to event.title,
                    "description" to event.description,
                    "location_latitude" to
                            when (event.location) {
                                null -> 200.0
                                else -> event.location.latitude
                            },
                    "location_longitude" to
                            when (event.location) {
                                null -> 200.0
                                else -> event.location.longitude
                            },
                    "location_name" to
                            when (event.location) {
                                null -> ""
                                else -> event.location.name
                            },
                    "eventDate" to
                            when (event.eventDate) {
                                null -> "null"
                                else -> event.eventDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            },//TODO: ADD IF WE NEED END DATE
                    "attendanceCapacity" to
                            when (event.attendanceCapacity) {
                                null -> "Unlimited"
                                else -> event.attendanceCapacity.toString()
                            },
                    "inscriptionLimitDate" to
                            when (event.inscriptionLimitDate) {
                                null -> "null"
                                else -> event.inscriptionLimitDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            },
                    "category" to event.category,
                    "registeredUsers" to event.registeredUsers,
                    "finalAttendee" to event.finalAttendees,
                    "globalRating" to
                            when (event.globalRating) {
                                null -> ""
                                else -> event.globalRating.toString()
                            },
                    "images" to null, //TODO: ADD IMAGES
                    "eventStatus" to event.eventStatus)

            Firebase.firestore
                .collection(EVENTS)
                .document(event.eventID)
                .set(eventItem)
                .addOnFailureListener { exception -> Log.e(TAG, "Error adding new Event", exception) }
        }

    }
}