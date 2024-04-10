package com.github.se.gatherspot.model.event

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
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
 * @param eventEndDate: The date the event ends
 * @param timeBeginning: The time in the eventStartDate the event starts
 * @param timeEnding: The time in the eventEndDate the event ends
 * @param attendanceMaxCapacity: The maximum number of attendees (optional)
 * @param attendanceMinCapacity: The minimum number of attendees (optional)
 * @param inscriptionLimitDate: The last date to register for the event (optional)
 * @param eventStatus: The status of the event (draft, created, ongoing, completed)
 * @param categories: List of category labels of the event
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
    val timeBeginning: LocalTime?, // Beginning in the eventStartDate
    val timeEnding: LocalTime?, // End in the eventEndDate
    val attendanceMaxCapacity: Int?,
    val attendanceMinCapacity: Int = 0,
    val inscriptionLimitDate: LocalDate?,
    val inscriptionLimitTime: LocalTime?,
    val eventStatus: EventStatus = EventStatus.DRAFT,
    // TODO : List of Categories, but for now before the implementation of category class just use
    // String
    val categories: List<String>? = emptyList(),
    // List of the IDs of the users who registered for the event
    val registeredUsers: List<String>? = emptyList(),
    val finalAttendees: List<String>? = emptyList(),
    // Find a way to upload image
    var images: ImageBitmap? = ImageBitmap(30, 30, config = ImageBitmapConfig.Rgb565), // TODO find default image
    val globalRating: Int?
)
