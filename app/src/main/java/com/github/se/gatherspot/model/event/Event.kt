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
 * @param eventDate: The date of the event
 * @param timeBeginning: The time the event starts
 * @param timeEnding: The time the event ends
 * @param attendanceCapacity: The maximum number of attendees (optional)
 * @param inscriptionLimitDate: The last date to register for the event (optional)
 * @param status: The status of the event (draft, created, ongoing, completed)
 * @param category: List of category labels of the event
 * @param registeredUsers: The list of users who registered for the event
 * @param finalAttendee: The list of users who attended the event
 * @param images: The images uploaded for the event
 * @param globalRating: The rating of the event by the attendees
 */
data class Event(
    // How to generate a unique ID
    val eventID: String,
    val title: String,
    val description: String?,
    val location: Location?,
    val eventDate: LocalDate?,
    val timeBeginning: LocalTime?,
    val timeEnding: LocalTime?,
    val attendanceCapacity: Int?,
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


