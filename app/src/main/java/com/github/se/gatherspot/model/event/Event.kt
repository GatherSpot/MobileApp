package com.github.se.gatherspot.model.event

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.se.gatherspot.firebase.CollectionClass
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalTimeSerializer
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalTime

/**
 * Data class for an event
 *
 * @param id: The unique identifier of the event
 * @param title: The title of the event
 * @param description: A short description of the event
 * @param location: The location of the event (GPS coordinates)
 * @param eventStartDate: The date of the start the event
 * @param eventEndDate: The date the event ends
 * @param timeBeginning: The time in the eventStartDate the event starts
 * @param timeEnding: The time in the eventEndDate the event ends
 * @param attendanceMaxCapacity: The maximum number of attendees (optional)
 * @param attendanceMinCapacity: The minimum number of attendees (optional)
 * @param inscriptionLimitDate: The last date to register for the event (optional)
 * @param eventStatus: The status of the event (draft, created, ongoing, completed)
 * @param categories: List of category labels of the event
 * @param organizerID: Id of the Profile of the organizer
 * @param registeredUsers: The list of users who registered for the event
 * @param finalAttendees: The list of users who attended the event
 * @param image: The images uploaded for the event
 * @param globalRating: The rating of the event by the attendees
 */
@Entity
data class Event(
    // How to generate a unique ID
    @PrimaryKey override val id: String,
    val title: String,
    val description: String?,
    @Embedded(prefix = "location_") val location: Location?,
    val eventStartDate: LocalDate?,
    val eventEndDate: LocalDate?,
    val timeBeginning: LocalTime?, // Beginning in the eventStartDate
    val timeEnding: LocalTime?, // End in the eventEndDate
    val attendanceMaxCapacity: Int?,
    val attendanceMinCapacity: Int = 0,
    val inscriptionLimitDate: LocalDate?,
    val inscriptionLimitTime: LocalTime?,
    val eventStatus: EventStatus = EventStatus.CREATED,
    val categories: Set<Interests>? = emptySet(),
    val organizerID: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    // List of the IDs of the users who registered for the event
    val registeredUsers: MutableList<String> = mutableListOf(),
    val finalAttendees: List<String>? = emptyList(),
    // Find a way to upload image
    var image: String,
    val globalRating: Int?,
) : CollectionClass() {

  /**
   * Converts the event to a JSON string.
   *
   * @return The JSON string representing the event.
   */
  fun toJson(): String {
    val eventJson = gson.toJson(this)
    return URLEncoder.encode(eventJson, StandardCharsets.US_ASCII.toString()).replace("+", "%20")
  }

  companion object {

    private val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .create()

    /**
     * Converts a JSON string to an event.
     *
     * @param string The JSON string representing the event.
     * @return The event.
     */
    fun fromJson(string: String): Event {
      val eventJson = string.replace("%20", "+")
      return gson.fromJson(eventJson, Event::class.java)
    }
  }
}
