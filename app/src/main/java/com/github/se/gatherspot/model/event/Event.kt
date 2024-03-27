package com.github.se.gatherspot.model.event

import androidx.compose.ui.graphics.ImageBitmap
import com.github.se.gatherspot.model.location.Location
import java.time.LocalDate
import java.time.LocalTime

/**
 * Data class for an event
 *
 * @param eventID: The unique identifier of the event
 * @param title: The title of the event
 * @param description: A short description of the event
 * @param location: The location of the event (GPS coordinates)
 * @param eventStartDate: The date of the start the event
 * @param eventEndDate: The date the event ends, if it is a multi-day event.
 * If not precised, it is the same as the start date
 * @param timeBeginning: The time the event starts
 * @param timeEnding: The time the event ends
 * @param attendanceMaxCapacity: The maximum number of attendees (optional)
 * @param attendanceMinCapacity: The minimum number of attendees (optional)
 * @param inscriptionLimitDate: The last date to register for the event (optional)
 * @param eventStatus: The status of the event (draft, created, ongoing, completed)
 * @param category: List of category labels of the event
 * @param registeredUsers: The list of users who registered for the event
 * @param finalAttendees: The list of users who attended the event
 * @param images: The images uploaded for the event
 * @param globalRating: The rating of the event by the attendees
 */
data class Event(
    // How to generate a unique ID
    val eventID: String,
    val title: String,
    val description: String?,
    val location: Location?,
    val eventStartDate: LocalDate?,
    val eventEndDate: LocalDate?,
    val timeBeginning: LocalTime?,
    val timeEnding: LocalTime?,
    val attendanceMaxCapacity: Int?,
    val attendanceMinCapacity: Int?,
    val inscriptionLimitDate: LocalDate?,
    val eventStatus: EventStatus = EventStatus.DRAFT,
    // TODO : List of Categories, but for now before the implementation of category class just use String
    val category: List<String>?,
    // TODO : List of User, but for now before the implementation of user class just use String
    val registeredUsers: List<String>? = emptyList(),
    val finalAttendees: List<String>? = emptyList(),
    // Find a way to upload image
    val images: ImageBitmap? = null, //TODO find default image
    val globalRating: Int?
)


