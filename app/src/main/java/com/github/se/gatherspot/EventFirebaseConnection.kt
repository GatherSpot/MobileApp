package com.github.se.gatherspot

import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * Class to handle the connection to the Firebase database for events
 */
class EventFirebaseConnection {
    companion object {
        private val TAG = "FirebaseConnection" //Used for debugging/logs
        private val EVENTS = "events" //Collection name for events
        val dateFormat = "dd/MM/yyyy"
        val timeFormat = "H:mm"

        /**
         * Creates a unique new identifier
         * This function can be used for both Event IDs and User IDs
         *
         * @return A unique identifier
         */
        fun getNewEventID(): String {
            return FirebaseDatabase.getInstance().getReference().child(EVENTS).push().key!!
        }

        /**
         * Fetches an event from the database
         *
         * @param eventID: the unique identifier of the event
         * @return The Event object
         */
        suspend fun fetchEvent(eventID: String): Event? =
            suspendCancellableCoroutine { continuation ->
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
        private fun mapDocumentToEvent(document: DocumentSnapshot): Event? {
            if(document.getString("eventID") == null) {
                return null
            }
            val eventID = document.getString("eventID") as String
            val title = document.getString("title") as String
            val description = document.getString("description") as String
            val location: Location?
            val locationName = document.getString("locationName")
            location = if (locationName == "") {
                null
            } else {
                Location(
                    latitude = document.get("locationLatitude") as Double,
                    longitude = document.get("locationLongitude") as Double,
                    name = document.getString("locationName") as String
                )
            }
            var date = document.getString("eventStartDate") as String
            val eventStartDate =
                when (date) {
                    "null" -> null
                    else ->
                        try {
                            LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat))
                        } catch (e: Exception) {
                            null
                        }
                }
            date = document.getString("eventEndDate") as String
            val eventEndDate =
                when (date) {
                    "null" -> null
                    else ->
                        try {
                            LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat))
                        } catch (e: Exception) {
                            null
                        }
                }
            var time = document.getString("timeBeginning") as String
            val timeBeginning = when (time) {
                "null" -> null
                else -> LocalTime.parse(time, DateTimeFormatter.ofPattern(timeFormat))
            }
            time = document.getString("timeEnding") as String
            val timeEnding = when (time) {
                "null" -> null
                else -> LocalTime.parse(time, DateTimeFormatter.ofPattern(timeFormat))
            }
            var capacity = document.getString("attendanceMaxCapacity")
            val attendanceMaxCapacity = when (capacity) {
                "Unlimited" -> null
                else -> capacity?.toInt()
            }
            capacity = document.getString("attendanceMinCapacity")
            val attendanceMinCapacity =
                capacity?.toInt() //Min will be 0 by default if min is not mentioned
            date = document.getString("inscriptionLimitDate") as String
            val inscriptionLimitDate =
                when (date) {
                    "null" -> null
                    else ->
                        try {
                            LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat))
                        } catch (e: Exception) {
                            null
                        }
                }
            time = document.getString("inscriptionLimitTime") as String
            val inscriptionLimitTime = when (time) {
                "null" -> null
                else -> LocalTime.parse(time, DateTimeFormatter.ofPattern(timeFormat))
            }
            val status = document.getString("eventStatus") as String
            val eventStatus: EventStatus =
                when (status) {
                    "CREATED" -> EventStatus.CREATED
                    "DRAFT" -> EventStatus.DRAFT
                    "ON_GOING" -> EventStatus.ON_GOING
                    "COMPLETED" -> EventStatus.COMPLETED
                    else -> EventStatus.DRAFT
                }
            val categories = document.get("categories") as List<String>
            val registeredUsers = document.get("finalAttendee") as List<String>
            val finalAttendee = document.get("finalAttendee") as List<String>
            val images = null //TODO: Retrieve images from database
            val globalRating = when (val rating = document.getString("globalRating") as String) {
                "null" -> null
                else -> rating.toInt()
            }
            return Event(
                eventID = eventID,
                title = title,
                description = description,
                location = location,
                eventStartDate = eventStartDate,
                eventEndDate = eventEndDate,
                timeBeginning = timeBeginning,
                timeEnding = timeEnding,
                attendanceMaxCapacity = attendanceMaxCapacity,
                attendanceMinCapacity = attendanceMinCapacity!!,
                inscriptionLimitDate = inscriptionLimitDate,
                inscriptionLimitTime = inscriptionLimitTime,
                eventStatus = eventStatus,
                categories = categories,
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
                    "locationLatitude" to
                            when (event.location) {
                                null -> 200.0
                                else -> event.location.latitude
                            },
                    "locationLongitude" to
                            when (event.location) {
                                null -> 200.0
                                else -> event.location.longitude
                            },
                    "locationName" to
                            when (event.location) {
                                null -> ""
                                else -> event.location.name
                            },
                    "eventStartDate" to
                            when (event.eventStartDate) {
                                null -> "null"
                                else -> event.eventStartDate.format(
                                    DateTimeFormatter.ofPattern(
                                        dateFormat
                                    )
                                )
                            },
                    "eventEndDate" to
                            when (event.eventEndDate) {
                                null -> "null"
                                else -> event.eventEndDate.format(
                                    DateTimeFormatter.ofPattern(
                                        dateFormat
                                    )
                                )
                            },
                    "timeBeginning" to
                            when (event.timeBeginning) {
                                null -> "null"
                                else -> event.timeBeginning.format(
                                    DateTimeFormatter.ofPattern(
                                        timeFormat
                                    )
                                )
                            },
                    "timeEnding" to
                            when (event.timeEnding) {
                                null -> "null"
                                else -> event.timeEnding.format(
                                    DateTimeFormatter.ofPattern(
                                        timeFormat
                                    )
                                )
                            },
                    "attendanceMaxCapacity" to
                            when (event.attendanceMaxCapacity) {
                                null -> "Unlimited"
                                else -> event.attendanceMaxCapacity.toString()
                            },
                    "attendanceMinCapacity" to event.attendanceMinCapacity.toString(),
                    "inscriptionLimitDate" to
                            when (event.inscriptionLimitDate) {
                                null -> "null"
                                else -> event.inscriptionLimitDate.format(
                                    DateTimeFormatter.ofPattern(
                                        dateFormat
                                    )
                                )
                            },
                    "inscriptionLimitTime" to
                            when (event.inscriptionLimitTime) {
                                null -> "null"
                                else -> event.inscriptionLimitTime.format(
                                    DateTimeFormatter.ofPattern(
                                        timeFormat
                                    )
                                )
                            },
                    "categories" to event.categories,
                    "registeredUsers" to event.registeredUsers,
                    "finalAttendee" to event.finalAttendees,
                    "globalRating" to
                            when (event.globalRating) {
                                null -> "null"
                                else -> event.globalRating.toString()
                            },
                    "images" to null, //TODO: ADD IMAGES
                    "eventStatus" to event.eventStatus
                )

            Firebase.firestore
                .collection(EVENTS)
                .document(event.eventID)
                .set(eventItem)
                .addOnFailureListener { exception ->
                    Log.e(
                        TAG,
                        "Error adding new Event",
                        exception
                    )
                }
        }

        /**
         * Delete an event in the database
         *
         * @param eventID: The eventID of the event to delete
         */
        fun deleteEvent(eventID: String) {
            Firebase.firestore
                .collection(EVENTS)
                .document(eventID)
                .delete()
                .addOnFailureListener { exception ->
                    Log.e(
                        TAG,
                        "Error deleting Event",
                        exception
                    )
                }
        }
    }
}