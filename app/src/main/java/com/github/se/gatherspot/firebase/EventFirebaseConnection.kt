package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.tasks.await

/** Class to handle the connection to the Firebase database for events */
open class EventFirebaseConnection : FirebaseConnectionInterface<Event> {

  override val COLLECTION = FirebaseCollection.EVENTS.toString().lowercase()
  override val TAG = "FirebaseConnection" // Used for debugging/logs
  val EVENTS = "events" // Collection name for events

  companion object {
    val DATE_FORMAT_DISPLAYED = "dd/MM/yyyy"
    val DATE_FORMAT_STORED = "yyyy/MM/dd"
    val TIME_FORMAT = "HH:mm"
  }

  val BATTLE_OF_THE_APPS_START_DATE =
      LocalDate.parse("2024/05/28", DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
  val BATTLE_OF_THE_APPS_END_DATE =
      LocalDate.parse("2024/05/28", DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
  val BATTLE_OF_THE_APPS_START_TIME =
      LocalTime.parse("10:15", DateTimeFormatter.ofPattern(TIME_FORMAT))
  val BATTLE_OF_THE_APPS_END_TIME =
      LocalTime.parse("12:00", DateTimeFormatter.ofPattern(TIME_FORMAT))

  val EVENT_START_DATE_DEFAULT_VALUE = BATTLE_OF_THE_APPS_START_DATE
  val EVENT_END_DATE_DEFAULT_VALUE = BATTLE_OF_THE_APPS_END_DATE
  val EVENT_START_TIME_DEFAULT_VALUE = BATTLE_OF_THE_APPS_START_TIME
  val EVENT_END_TIME_DEFAULT_VALUE = BATTLE_OF_THE_APPS_END_TIME

  var offset: DocumentSnapshot? = null

  // val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern())

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
          "ON_GOING" -> EventStatus.ON_GOING
          "COMPLETED" -> EventStatus.COMPLETED
          else -> EventStatus.CREATED
        }
    val categoriesList = d.get("categories") as List<String>
    val categories = categoriesList.map { Interests.valueOf(it) }.toSet()
    val registeredUsers = d.get("registeredUsers") as MutableList<String>
    val finalAttendee = d.get("finalAttendee") as List<String>
    val image = d.getString("image")
    val globalRating =
        when (val rating = d.getString("globalRating")!!) {
          "null" -> null
          else -> rating.toInt()
        }
    val organizerID = d.getString("organizerID")!!
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
        image = image ?: "",
        globalRating = globalRating,
        organizerID = organizerID)
  }

  private suspend fun eventsFromQuerySnapshot(querySnapshot: QuerySnapshot): MutableList<Event> {
    val listOfMaps = querySnapshot.documents.map { it.data!! }
    val listOfEvents = mutableListOf<Event>()

    listOfMaps.forEach { map ->
      val uid = map["eventID"] as String
      val event = super.fetch(uid)
      event?.let { listOfEvents.add(it) }
    }

    return listOfEvents
  }

  /**
   * Fetch the next number events stating from the offset
   *
   * @param number: the number of events to fetch
   * @return list of events
   */
  // to be changed, Firebase not clear, I want to filter out events created by current user
  open suspend fun fetchNextEvents(number: Long): MutableList<Event> {
    val querySnapshot: QuerySnapshot =
        if (offset == null) {
          Firebase.firestore
              .collection(EVENTS)
              .orderBy("eventStartDate")
              .orderBy("eventID")
              .whereGreaterThanOrEqualTo(
                  "eventStartDate",
                  LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)))
              .limit(number)
              .get()
              .await()
        } else {
          Firebase.firestore
              .collection(EVENTS)
              .orderBy("eventStartDate")
              .orderBy("eventID")
              .whereGreaterThanOrEqualTo(
                  "eventStartDate",
                  LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)))
              .startAfter(offset!!.get("eventStartDate"), offset!!.get("eventID"))
              .limit(number)
              .get()
              .await()
        }

    if (querySnapshot.documents.isNotEmpty()) {
      offset = querySnapshot.documents.last()
    }

    return eventsFromQuerySnapshot(querySnapshot)
  }

  open suspend fun fetchNextEvents(idlist: IdList?, number: Long): MutableList<Event> {

    if (idlist?.events == null || idlist.events.isEmpty()) {
      return mutableListOf()
    }
    val querySnapshot: QuerySnapshot =
        if (offset == null) {
          Firebase.firestore
              .collection(EVENTS)
              .orderBy("eventID")
              .whereIn("eventID", idlist.events)
              .limit(number)
              .get()
              .await()
        } else {
          Firebase.firestore
              .collection(EVENTS)
              .orderBy("eventID")
              .whereIn("eventID", idlist?.events ?: listOf())
              .startAfter(offset!!.get("eventID"))
              .limit(number)
              .get()
              .await()
        }
    if (querySnapshot.documents.isNotEmpty()) {
      offset = querySnapshot.documents.last()
    }
    return eventsFromQuerySnapshot(querySnapshot)
  }

  open suspend fun fetchEventsBasedOnInterests(
      number: Long,
      l: List<Interests>
  ): MutableList<Event> {
    val querySnapshot: QuerySnapshot =
        if (offset == null) {
          Firebase.firestore
              .collection(EVENTS)
              .orderBy("eventStartDate")
              .orderBy("eventID")
              .whereGreaterThanOrEqualTo(
                  "eventStartDate",
                  LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)))
              .whereArrayContainsAny("categories", l.map { it.name })
              .limit(number)
              .get()
              .await()
        } else {
          Firebase.firestore
              .collection(EVENTS)
              .orderBy("eventStartDate")
              .orderBy("eventID")
              .whereArrayContainsAny("categories", l.map { it.name })
              .whereGreaterThanOrEqualTo(
                  "eventStartDate",
                  LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)))
              .startAfter(offset!!.get("eventStartDate"), offset!!.get("eventID"))
              .limit(number)
              .get()
              .await()
        }
    if (querySnapshot.documents.isNotEmpty()) {
      offset = querySnapshot.documents.last()
    }
    return eventsFromQuerySnapshot(querySnapshot)
  }

  open suspend fun fetchMyEvents(): MutableList<Event> {
    val querySnapshot: QuerySnapshot =
        Firebase.firestore
            .collection(EVENTS)
            .orderBy("eventID")
            .whereEqualTo(
                "organizerID", FirebaseAuth.getInstance().currentUser?.uid ?: "noneForTests")
            .get()
            .await()

    return eventsFromQuerySnapshot(querySnapshot)
  }

  open suspend fun fetchRegisteredTo(): MutableList<Event> {
    val querySnapshot: QuerySnapshot =
        Firebase.firestore
            .collection(EVENTS)
            .orderBy("eventID")
            .whereArrayContains(
                "registeredUsers", FirebaseAuth.getInstance().currentUser?.uid ?: "noneForTests")
            .get()
            .await()

    return eventsFromQuerySnapshot(querySnapshot)
  }

  open suspend fun addRegisteredUser(eventID: String, uid: String) {
    Firebase.firestore
        .collection(EVENTS)
        .document(eventID)
        .update("registeredUsers", FieldValue.arrayUnion(uid))
        .await()
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
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT_DISPLAYED))
          } catch (e: Exception) {
            try {
              LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
            } catch (e: Exception) {
              null
            }
          }
    }
  }

  /**
   * Adds an event to the database
   *
   * @param element: The event to add
   */
  override suspend fun add(element: Event) {
    val eventItem =
        hashMapOf(
            "eventID" to
                when (element.id) {
                  "" -> getNewID()
                  else -> element.id
                },
            "title" to
                when (element.title) {
                  "" -> element.id
                  else -> element.title
                },
            "description" to element.description,
            "locationLatitude" to
                when (element.location) {
                  null -> 200.0
                  else -> element.location.latitude
                },
            "locationLongitude" to
                when (element.location) {
                  null -> 200.0
                  else -> element.location.longitude
                },
            "locationName" to
                when (element.location) {
                  null -> ""
                  else -> element.location.name
                },
            "eventStartDate" to
                when (element.eventStartDate) {
                  null ->
                      EVENT_START_DATE_DEFAULT_VALUE.format(
                          DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
                  else ->
                      element.eventStartDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
                },
            "eventEndDate" to
                when (element.eventEndDate) {
                  null ->
                      EVENT_END_DATE_DEFAULT_VALUE.format(
                          DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
                  else ->
                      element.eventEndDate.format(
                          DateTimeFormatter.ofPattern(DATE_FORMAT_DISPLAYED))
                },
            "timeBeginning" to
                when (element.timeBeginning) {
                  null ->
                      EVENT_START_TIME_DEFAULT_VALUE.format(
                          DateTimeFormatter.ofPattern(TIME_FORMAT))
                  else -> element.timeBeginning.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                },
            "timeEnding" to
                when (element.timeEnding) {
                  null ->
                      EVENT_END_TIME_DEFAULT_VALUE.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                  else -> element.timeEnding.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                },
            "attendanceMaxCapacity" to
                when (element.attendanceMaxCapacity) {
                  null -> "Unlimited"
                  else -> element.attendanceMaxCapacity.toString()
                },
            "attendanceMinCapacity" to element.attendanceMinCapacity.toString(),
            "inscriptionLimitDate" to
                when (element.inscriptionLimitDate) {
                  null ->
                      element.eventEndDate?.format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
                          ?: EVENT_START_DATE_DEFAULT_VALUE.format(
                              DateTimeFormatter.ofPattern(DATE_FORMAT_STORED))
                  else ->
                      element.inscriptionLimitDate.format(
                          DateTimeFormatter.ofPattern(DATE_FORMAT_DISPLAYED))
                },
            "inscriptionLimitTime" to
                when (element.inscriptionLimitTime) {
                  null ->
                      element.timeEnding?.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                          ?: EVENT_END_TIME_DEFAULT_VALUE.format(
                              DateTimeFormatter.ofPattern(TIME_FORMAT))
                  else ->
                      element.inscriptionLimitTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                },
            "categories" to element.categories?.toList(),
            "registeredUsers" to element.registeredUsers,
            "finalAttendee" to
                when (element.finalAttendees) { // TODO Harmonize spelling to one or the other
                  null -> mutableListOf<String>()
                  else -> element.finalAttendees
                },
            "globalRating" to // TODO Change globalRating to an Int ?
                when (element.globalRating) {
                  null -> "null"
                  else -> element.globalRating.toString()
                },
            "image" to element.image,
            "organizerID" to
                when (element.organizerID) {
                  "" -> Firebase.auth.currentUser?.uid!!
                  else -> element.organizerID
                },
            "eventStatus" to element.eventStatus) // TODO remove ?

    Firebase.firestore
        .collection(EVENTS)
        .document(element.id)
        .set(eventItem)
        .addOnFailureListener { exception -> Log.e(TAG, "Error adding new Event", exception) }
        .await()
  }
}
