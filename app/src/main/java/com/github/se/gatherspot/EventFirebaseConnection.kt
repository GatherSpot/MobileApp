package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/** Class to handle the connection to the Firebase database for events */
class EventFirebaseConnection {
  companion object {
    private const val TAG = "FirebaseConnection" // Used for debugging/logs
    private const val EVENTS = "events" // Collection name for events
    const val DATE_FORMAT = "dd/MM/yyyy"
    const val TIME_FORMAT = "H:mm"

    /**
     * Creates a unique new identifier This function can be used for both Event IDs and User IDs
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
    suspend fun fetchEvent(eventID: String): Event? = suspendCancellableCoroutine { continuation ->
      Firebase.firestore
          .collection(EVENTS)
          .document(eventID)
          .get()
          .addOnSuccessListener { result ->
            val event = mapDocToEvent(result)
            continuation.resume(event)
          }
          .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
    }

    /**
     * Maps a document to an Event object
     *
     * @param document: The document to map
     * @return The Event object
     */
    private fun mapDocToEvent(document: DocumentSnapshot): Event? {
      if (document.getString("eventID") == null) {
        return null
      }
      val eventID = document.getString("eventID")!!
      val title = document.getString("title")!!
      val description = document.getString("description")!!
      val location: Location?
      val locationName = document.getString("locationName")!!
      location =
          if (locationName == "") {
            null
          } else {
            Location(
                latitude = document.get("locationLatitude") as Double,
                longitude = document.get("locationLongitude") as Double,
                name = document.getString("locationName")!!)
          }
      var date = document.getString("eventStartDate")!!
      val eventStartDate = mapDateStringToDate(date)
      date = document.getString("eventEndDate")!!
      val eventEndDate = mapDateStringToDate(date)
      var time = document.getString("timeBeginning")!!
      val timeBeginning = mapTimeStringToTime(time)
      time = document.getString("timeEnding")!!
      val timeEnding = mapTimeStringToTime(time)
      var capacity = document.getString("attendanceMaxCapacity")!!
      val attendanceMaxCapacity =
          when (capacity) {
            "Unlimited" -> null
            else -> capacity.toInt()
          }
      capacity = document.getString("attendanceMinCapacity")!!
      val attendanceMinCapacity =
          capacity.toInt() // Min will be 0 by default if min is not mentioned
      date = document.getString("inscriptionLimitDate")!!
      val inscriptionLimitDate = mapDateStringToDate(date)
      time = document.getString("inscriptionLimitTime")!!
      val inscriptionLimitTime = mapTimeStringToTime(time)
      val status = document.getString("eventStatus")!!
      val eventStatus: EventStatus =
          when (status) {
            "CREATED" -> EventStatus.CREATED
            "DRAFT" -> EventStatus.DRAFT
            "ON_GOING" -> EventStatus.ON_GOING
            "COMPLETED" -> EventStatus.COMPLETED
            else -> EventStatus.DRAFT
          }
      val categoriesList = document.get("categories") as List<String>
      val categories = categoriesList.map { Interests.valueOf(it) }.toSet()
      val registeredUsers = document.get("finalAttendee") as List<Profile>
      val finalAttendee = document.get("finalAttendee") as List<Profile>
      val images = null // TODO: Retrieve images from database
      val globalRating =
          when (val rating = document.getString("globalRating")!!) {
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
          attendanceMinCapacity = attendanceMinCapacity,
          inscriptionLimitDate = inscriptionLimitDate,
          inscriptionLimitTime = inscriptionLimitTime,
          eventStatus = eventStatus,
          categories = categories,
          registeredUsers = registeredUsers,
          finalAttendees = finalAttendee,
          images = images,
          globalRating = globalRating,
          // TODO: Add organizer
          organizer = Profile("null", "null", "null", "null", emptySet()))
    }
    /**
     * Maps a string to a LocalTime object
     *
     * @param timeString: The time string to map
     * @return The LocalTime object
     */
    fun mapTimeStringToTime(timeString: String): LocalTime? {
      return when (timeString) {
        "null" -> null
        else ->
            try {
              LocalTime.parse(timeString, DateTimeFormatter.ofPattern(TIME_FORMAT))
            } catch (e: Exception) {
              null
            }
      }
    }
    /**
     * Maps a string to a LocalDate object
     *
     * @param dateString: The date string to map
     * @return The LocalDate object
     */
    fun mapDateStringToDate(dateString: String): LocalDate? {
      return when (dateString) {
        "null" -> null
        else ->
            try {
              LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT))
            } catch (e: Exception) {
              null
            }
      }
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
                    else -> event.eventStartDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                  },
              "eventEndDate" to
                  when (event.eventEndDate) {
                    null -> "null"
                    else -> event.eventEndDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                  },
              "timeBeginning" to
                  when (event.timeBeginning) {
                    null -> "null"
                    else -> event.timeBeginning.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                  },
              "timeEnding" to
                  when (event.timeEnding) {
                    null -> "null"
                    else -> event.timeEnding.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
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
                    else ->
                        event.inscriptionLimitDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                  },
              "inscriptionLimitTime" to
                  when (event.inscriptionLimitTime) {
                    null -> "null"
                    else ->
                        event.inscriptionLimitTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                  },
              "categories" to event.categories?.toList(),
              "registeredUsers" to event.registeredUsers,
              "finalAttendee" to event.finalAttendees,
              "globalRating" to
                  when (event.globalRating) {
                    null -> "null"
                    else -> event.globalRating.toString()
                  },
              "images" to null, // TODO: ADD IMAGES
              "eventStatus" to event.eventStatus)

      Firebase.firestore
          .collection(EVENTS)
          .document(event.eventID)
          .set(eventItem)
          .addOnFailureListener { exception -> Log.e(TAG, "Error adding new Event", exception) }
    }

    /**
     * Delete an event in the database
     *
     * @param eventID: The eventID of the event to delete
     */
    fun deleteEvent(eventID: String) {
      Firebase.firestore.collection(EVENTS).document(eventID).delete().addOnFailureListener {
          exception ->
        Log.e(TAG, "Error deleting Event", exception)
      }
    }
  }
}
