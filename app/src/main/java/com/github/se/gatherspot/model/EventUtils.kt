package com.github.se.gatherspot.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.app.ActivityCompat
import com.github.se.gatherspot.cache.LocalStorage
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.model.event.DraftEvent
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.EventAction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

private const val ELEMENTS_TO_DISPLAY = 5

class EventUtils {

  /**
   * Create an event from verified data
   *
   * @param title: The title of the event
   * @param description: A short description of the event
   * @param location: The location of the event (GPS coordinates)
   * @param eventStartDate: The date of the start the event
   * @param eventEndDate: The date the event ends, if it is a multi-day event.
   * @param eventTimeStart: The time in the eventStartDate the event starts
   * @param eventTimeEnd: The time in the eventEndDate the event ends
   * @param maxAttendees: The maximum number of attendees
   * @param minAttendees: The minimum number of attendees (default 0)
   * @param dateLimitInscription: The last date to register for the event
   * @param timeLimitInscription: The last time to register for the event
   * @return The event created
   */
  private val eventFirebaseConnection = EventFirebaseConnection()

  private fun createEvent(
      title: String,
      description: String,
      location: Location?,
      eventStartDate: LocalDate,
      eventEndDate: LocalDate?,
      eventTimeStart: LocalTime,
      eventTimeEnd: LocalTime,
      categories: List<Interests>?,
      maxAttendees: Int?,
      minAttendees: Int?,
      dateLimitInscription: LocalDate?,
      timeLimitInscription: LocalTime?
  ): Event {

    // First fetch an unique ID for the event
    val eventID = eventFirebaseConnection.getNewID()

    // Create the event
    val event =
        Event(
            eventID,
            title,
            description,
            location,
            eventStartDate,
            eventEndDate,
            eventTimeStart,
            eventTimeEnd,
            maxAttendees,
            attendanceMinCapacity = minAttendees ?: 0,
            dateLimitInscription,
            timeLimitInscription,
            globalRating = null,
            image = "",
            categories = categories?.toSet(),
            eventStatus = EventStatus.CREATED)

    // Add the event to the database
    eventFirebaseConnection.add(event)

    return event
  }

  /**
   * Delete an event from the database Need the firebase to be implemented to be properly tested
   *
   * @param event: The event to delete
   */
  fun deleteEvent(event: Event) {
    // Remove the event from all the users who registered for it
    val idListFirebase = IdListFirebaseConnection()
    runBlocking {
      event.registeredUsers.forEach { userID ->
        val registeredEvents =
            idListFirebase.fetchFromFirebase(userID, FirebaseCollection.REGISTERED_EVENTS) {}
        registeredEvents?.remove(event.id)
        if (registeredEvents != null) {
          idListFirebase.saveToFirebase(registeredEvents)
        }
      }
    }
    eventFirebaseConnection.delete(event.id)
  }

  /**
   * Check if the data entered by the user is valid. Parse the data and check if it is in the
   * correct format, then call createEvent or updateEvent function. If the eventCreation is
   * successful, return true. All the parameters are strings, as they are taken from the user input.
   *
   * @param title: The title of the event
   * @param description: A short description of the event
   * @param location: The location of the event (GPS coordinates)
   * @param eventStartDate: The date of the start the event
   * @param eventEndDate: The date the event ends, if it is a multi-day event.
   * @param eventTimeStart: The time in the eventStartDate the event starts
   * @param eventTimeEnd: The time in the eventEndDate the event ends
   * @param maxAttendees: The maximum number of attendees
   * @param minAttendees: The minimum number of attendees
   * @param dateLimitInscription: The last date to register for the event
   * @param timeLimitInscription: The last time to register for the event
   * @return true if the data is valid
   * @throws Exception if the data is not valid
   */
  fun validateAndCreateOrUpdateEvent(
      title: String,
      description: String,
      location: Location?,
      eventStartDate: String,
      eventEndDate: String,
      eventTimeStart: String,
      eventTimeEnd: String,
      categories: List<Interests>?,
      maxAttendees: String,
      minAttendees: String,
      dateLimitInscription: String,
      timeLimitInscription: String,
      eventAction: EventAction,
      event: Event? = null
  ): Event {
    // test if the date is valid
    val parsedEventStartDate = validateDate(eventStartDate, "Invalid date format")
    // Check whether the start date is in the future
    if (parsedEventStartDate.isBefore(LocalDate.now())) {
      throw Exception("Event date must be in the future")
    }

    // Check if the end date is valid and after the start date
    var parsedEventEndDate: LocalDate? = parsedEventStartDate
    if (eventEndDate.isNotEmpty()) {
      parsedEventEndDate = validateDate(eventEndDate, "Invalid end date format")
      // If the end date is before the start date, throw an exception
      // If the end date = start date, it's valid, the event is on the same day

      if (parsedEventEndDate.isBefore(parsedEventStartDate)) {
        throw Exception("Event end date must be after start date")
      }
    }

    // Check if eventStartDate is today
    val isToday = parsedEventStartDate.isEqual(LocalDate.now())

    // test if the time is valid
    val parsedEventTimeStart = validateTime(eventTimeStart, "Invalid time format for start time")
    val parsedEventTimeEnd = validateTime(eventTimeEnd, "Invalid time format for end time")
    // Check if the end time is after the start time
    if (eventStartDate == eventEndDate && parsedEventTimeEnd.isBefore(parsedEventTimeStart)) {
      throw Exception("Event end time must be after start time")
    }

    // If the event is today, check if the start time is in the future
    if (isToday && parsedEventTimeStart.isBefore(LocalTime.now())) {
      throw Exception("Event start time must be in the future")
    }

    // test if the max attendees is valid
    var parsedMaxAttendees: Int? = null
    if (maxAttendees.isNotEmpty()) {
      parsedMaxAttendees =
          validateNumber(maxAttendees, "Invalid max attendees format, must be a number")
    }

    // test if the min attendees is valid
    var parsedMinAttendees: Int? = null
    if (minAttendees.isNotEmpty()) {
      parsedMinAttendees =
          validateNumber(minAttendees, "Invalid min attendees format, must be a number")
      if (parsedMaxAttendees != null && parsedMinAttendees > parsedMaxAttendees) {
        throw Exception("Minimum attendees must be less than maximum attendees")
      }
    }

    // If given by the user,check if the inscription limit date and time are valid and
    // before the start date and time
    var parsedDateLimitInscription: LocalDate? = null
    var parsedTimeLimitInscription: LocalTime? = null
    if (dateLimitInscription.isNotEmpty()) {

      parsedDateLimitInscription =
          validateDate(dateLimitInscription, "Invalid inscription limit date format")
      try {
        LocalDate.parse(
            dateLimitInscription,
            DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
      } catch (e: Exception) {
        throw Exception("Invalid inscription limit date format")
      }
      if (parsedDateLimitInscription.isAfter(parsedEventStartDate)) {
        throw Exception("Inscription limit date must be before event start date")
      }
      // If Limit time is not given, set it to 23:59
      parsedTimeLimitInscription =
          if (timeLimitInscription.isNotEmpty()) {
            validateTime(timeLimitInscription, "Invalid inscription limit time format")
          } else {
            LocalTime.of(23, 59)
          }
      if (parsedDateLimitInscription.isEqual(parsedEventStartDate) &&
          parsedTimeLimitInscription!!.isAfter(parsedEventTimeStart)) {
        throw Exception("Inscription limit time must be before event start time on the same day")
      }
    }
    // If all the data is valid and eventAction = CREATE, call createEvent function
    if (eventAction == EventAction.CREATE) {
      return createEvent(
          title,
          description,
          location,
          parsedEventStartDate,
          parsedEventEndDate,
          parsedEventTimeStart,
          parsedEventTimeEnd,
          categories,
          parsedMaxAttendees,
          parsedMinAttendees,
          parsedDateLimitInscription,
          parsedTimeLimitInscription)
    } else {
      return editEvent(
          title,
          description,
          location,
          parsedEventStartDate,
          parsedEventEndDate,
          parsedEventTimeStart,
          parsedEventTimeEnd,
          categories,
          parsedMaxAttendees,
          parsedMinAttendees,
          parsedDateLimitInscription,
          parsedTimeLimitInscription,
          event!!)
    }
  }

  private fun editEvent(
      title: String,
      description: String,
      location: Location?,
      eventStartDate: LocalDate,
      eventEndDate: LocalDate?,
      eventTimeStart: LocalTime,
      eventTimeEnd: LocalTime,
      categories: List<Interests>?,
      maxAttendees: Int?,
      minAttendees: Int?,
      dateLimitInscription: LocalDate?,
      timeLimitInscription: LocalTime?,
      oldEvent: Event
  ): Event {
    val event =
        Event(
            oldEvent.id,
            title,
            description,
            location,
            eventStartDate,
            eventEndDate,
            eventTimeStart,
            eventTimeEnd,
            maxAttendees,
            attendanceMinCapacity = minAttendees ?: 0,
            dateLimitInscription,
            timeLimitInscription,
            globalRating = oldEvent.globalRating,
            categories = categories?.toSet(),
            registeredUsers = oldEvent.registeredUsers,
            image = oldEvent.image,
            eventStatus = EventStatus.CREATED,
        )
    // Add the event to the database
    eventFirebaseConnection.add(event)
    return event
  }

  fun validateDate(date: String, eMessage: String): LocalDate {
    try {
      return LocalDate.parse(
          date, DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
    } catch (e: Exception) {
      throw Exception(eMessage)
    }
  }

  fun validateTime(time: String, eMessage: String): LocalTime {
    try {
      return LocalTime.parse(time, DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT))
    } catch (e: Exception) {
      throw Exception(eMessage)
    }
  }

  fun validateNumber(number: String, eMessage: String): Int {
    try {
      return number.toInt()
    } catch (e: Exception) {
      throw Exception(eMessage)
    }
  }

  suspend fun fetchLocationSuggestions(context: Context, query: String): List<Location> =
      withContext(Dispatchers.IO) {
        if (query.isEmpty()) return@withContext emptyList()

        val client = OkHttpClient()
        val requestUrl = "https://nominatim.openstreetmap.org/search?format=json&q=$query"
        val request = Request.Builder().url(requestUrl).build()
        val suggestions = mutableListOf<Location>()

        try {
          // Get the user's current location
          val userLocation = getCurrentLocation(context) ?: return@withContext emptyList()

          client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string()
            responseBody?.let {
              val jsonArray = JSONArray(it)
              for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val displayName = jsonObject.getString("display_name")
                val latitude = jsonObject.getDouble("lat")
                val longitude = jsonObject.getDouble("lon")
                suggestions.add(
                    Location(latitude = latitude, longitude = longitude, name = displayName))
              }
            }
          }

          // Sort suggestions based on distance to user's current location
          suggestions.sortBy { location ->
            calculateDistance(
                userLocation.latitude,
                userLocation.longitude,
                location.latitude,
                location.longitude)
          }

          // Limit the number of elements to display
          return@withContext suggestions.take(ELEMENTS_TO_DISPLAY)
        } catch (e: Exception) {
          e.printStackTrace()
          return@withContext emptyList()
        }
      }

  private suspend fun getCurrentLocation(context: Context): Location? {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    return if (ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
      // Request permissions if not granted (this should be handled in the UI layer)
      null
    } else {
      val location = fusedLocationClient.lastLocation.await()
      location?.let { Location(it.latitude, it.longitude, "Current Location") }
    }
  }

  fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // in kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a =
        sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c
  }

  fun saveDraftEvent(
      title: String?,
      description: String?,
      location: Location?,
      startDate: String?,
      endDate: String?,
      startTime: String?,
      endTime: String?,
      maxAttendees: String?,
      minAttendees: String?,
      dateLimitInscription: String?,
      timeLimitInscription: String?,
      categories: Set<Interests>?,
      image: ImageBitmap?,
      context: Context
  ) {
    val draftEvent =
        DraftEvent(
            title,
            description,
            location,
            startDate,
            endDate,
            startTime,
            endTime,
            maxAttendees,
            minAttendees,
            dateLimitInscription,
            timeLimitInscription,
            image = image,
            categories = categories)

    val localStorage = LocalStorage(context)
    localStorage.storeDraftEvent(draftEvent)
  }

  fun retrieveFromDraft(context: Context): DraftEvent? {
    val localStorage = LocalStorage(context)
    return localStorage.loadDraftEvent()
  }

  fun deleteDraft(context: Context) {
    val localStorage = LocalStorage(context)
    try {
      localStorage.deleteDraftEvent()
    } catch (e: Exception) {
      Log.e("EventUtils", "Error deleting draft event from local storage", e)
    }
  }
}
