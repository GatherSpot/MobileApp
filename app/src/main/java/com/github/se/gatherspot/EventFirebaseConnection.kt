package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.tasks.await

/** Class to handle the connection to the Firebase database for events */
class EventFirebaseConnection : FirebaseConnectionInterface {

  override val COLLECTION = FirebaseCollection.EVENTS.toString()

  /**
   * Maps a document to an Event object
   *
   * @param d: The document to map
   * @return The Event object
   */
  override fun getFromDocument(d: DocumentSnapshot): Event? {
    if (d.getString("eventID") == null) {
      return null
    }
    val eventID = d.getString("eventID")!!
    val title = d.getString("title")!!
    val description = d.getString("description")!!
    val location: Location?
    val locationName = d.getString("locationName")!!
    location =
        if (locationName == "") {
          null
        } else {
          Location(
              latitude = d.get("locationLatitude") as Double,
              longitude = d.get("locationLongitude") as Double,
              name = d.getString("locationName")!!)
        }
    var date = d.getString("eventStartDate")!!
    val eventStartDate = mapDateStringToDate(date)
    date = d.getString("eventEndDate")!!
    val eventEndDate = mapDateStringToDate(date)
    var time = d.getString("timeBeginning")!!
    val timeBeginning = mapTimeStringToTime(time)
    time = d.getString("timeEnding")!!
    val timeEnding = mapTimeStringToTime(time)
    var capacity = d.getString("attendanceMaxCapacity")!!
    val attendanceMaxCapacity =
        when (capacity) {
          "Unlimited" -> null
          else -> capacity.toInt()
        }
    capacity = d.getString("attendanceMinCapacity")!!
    val attendanceMinCapacity = capacity.toInt() // Min will be 0 by default if min is not mentioned
    date = d.getString("inscriptionLimitDate")!!
    val inscriptionLimitDate = mapDateStringToDate(date)
    time = d.getString("inscriptionLimitTime")!!
    val inscriptionLimitTime = mapTimeStringToTime(time)
    val status = d.getString("eventStatus")!!
    val eventStatus: EventStatus =
        when (status) {
          "CREATED" -> EventStatus.CREATED
          "DRAFT" -> EventStatus.DRAFT
          "ON_GOING" -> EventStatus.ON_GOING
          "COMPLETED" -> EventStatus.COMPLETED
          else -> EventStatus.DRAFT
        }
    val categoriesList = d.get("categories") as List<String>
    val categories = categoriesList.map { Interests.valueOf(it) }.toSet()
    val registeredUsers = d.get("finalAttendee") as MutableList<String>
    val finalAttendee = d.get("finalAttendee") as List<String>
    val images = null // TODO: Retrieve images from database
    val globalRating =
        when (val rating = d.getString("globalRating")!!) {
          "null" -> null
          else -> rating.toInt()
        }
    return Event(
        id = eventID,
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
        organizer = Profile("null", "null", "null", "null", setOf()))
  }

  override val TAG = "FirebaseConnection" // Used for debugging/logs
  val EVENTS = "events" // Collection name for events
  val DATE_FORMAT = "dd/MM/yyyy"
  val TIME_FORMAT = "H:mm"
  var offset: DocumentSnapshot? = null

  /**
   * Fetch the next number events stating from the offset
   *
   * @param number: the number of events to fetch
   * @return list of events
   */
  suspend fun fetchNextEvents(number: Long): MutableList<Event> {
    val querySnapshot: QuerySnapshot =
        if (offset == null) {
          Firebase.firestore.collection(EVENTS).orderBy("eventID").limit(number).get().await()
        } else {
          Firebase.firestore
              .collection(EVENTS)
              .orderBy("eventID")
              .startAfter(offset!!.get("eventID"))
              .limit(number)
              .get()
              .await()
        }

    if (querySnapshot.documents.isNotEmpty()) {
      offset = querySnapshot.documents.last()
    }

    val listOfMaps = querySnapshot.documents.map { it.data!! }
    val listOfEvents = mutableListOf<Event>()

    listOfMaps.forEach { map ->
      val uid = map["eventID"] as String
      val event = super.fetch(uid)
      event?.let { listOfEvents.add(it as Event) }
    }

    return listOfEvents
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
  override fun add(event: Event) {
    val eventItem =
        hashMapOf(
            "eventID" to event.id,
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

    Firebase.firestore.collection(EVENTS).document(event.id).set(eventItem).addOnFailureListener {
        exception ->
      Log.e(TAG, "Error adding new Event", exception)
    }
  }
}
