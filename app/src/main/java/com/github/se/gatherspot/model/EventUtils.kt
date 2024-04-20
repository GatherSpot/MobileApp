package com.github.se.gatherspot.model

import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.EventAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
  private val EventFirebaseConnection = EventFirebaseConnection()

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
    val eventID = EventFirebaseConnection.getNewID()

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
        categories = categories?.toSet(),
        eventStatus = EventStatus.CREATED
      )

    // Add the event to the database
    EventFirebaseConnection.add(event)

    return event
  }

  /**
   * Delete an event from the database Need the firebase to be implemented to be properly tested
   *
   * @param event: The event to delete
   */
  fun deleteEvent(event: Event) {
    // Remove the event from all the users who registered for it
    EventFirebaseConnection.delete(event.id)
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
          dateLimitInscription, DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)
        )
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
        parsedTimeLimitInscription!!.isAfter(parsedEventTimeStart)
      ) {
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
        parsedTimeLimitInscription
      )
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
        event!!
      )
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
        images = oldEvent.images,
        eventStatus = EventStatus.CREATED,
      )
    // Add the event to the database
    EventFirebaseConnection.add(event)
    return event
  }

  fun validateDate(date: String, eMessage: String): LocalDate {
    try {
      return LocalDate.parse(date, DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT))
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

  /** Fetch location suggestions from the OpenStreetMap API. */
  suspend fun fetchLocationSuggestions(query: String): List<Location> =
    withContext(Dispatchers.IO) {
      if (query.isEmpty()) return@withContext emptyList()

      val client = OkHttpClient()
      val requestUrl = "https://nominatim.openstreetmap.org/search?format=json&q=$query"
      val request = Request.Builder().url(requestUrl).build()
      val suggestions = mutableListOf<Location>()

      try {
        client.newCall(request).execute().use { response ->
          if (!response.isSuccessful) throw IOException("Unexpected code $response")

          val responseBody = response.body?.string()
          responseBody?.let {
            val jsonArray = JSONArray(it)
            val elementsToDisplay = minOf(jsonArray.length(), ELEMENTS_TO_DISPLAY)
            for (i in 0 until elementsToDisplay) {
              val jsonObject = jsonArray.getJSONObject(i)
              val displayName = jsonObject.getString("display_name")
              val latitude = jsonObject.getDouble("lat")
              val longitude = jsonObject.getDouble("lon")
              suggestions.add(
                Location(latitude = latitude, longitude = longitude, name = displayName)
              )
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      return@withContext suggestions
    }
}
