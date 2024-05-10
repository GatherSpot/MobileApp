package com.github.se.gatherspot.model.event

import com.github.se.gatherspot.firebase.CollectionClass
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.location.Location
import com.google.firebase.auth.FirebaseAuth
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
data class Event(
    // How to generate a unique ID
    override val id: String,
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
    val eventStatus: EventStatus = EventStatus.CREATED,
    val categories: Set<Interests>? = emptySet(),
    val organizerID: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    // List of the IDs of the users who registered for the event
    val registeredUsers: MutableList<String> = mutableListOf(),
    val finalAttendees: List<String>? = emptyList(),
    // Find a way to upload image
    var image: String,
    val globalRating: Int?,
) : CollectionClass()
