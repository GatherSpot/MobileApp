package com.github.se.gatherspot.model.event

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.se.gatherspot.firebase.CollectionClass
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
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
 * @param images: The images uploaded for the event
 * @param globalRating: The rating of the event by the attendees
 */
@Entity
data class Event(
    // How to generate a unique ID
    @PrimaryKey override val id: String,
    val title: String,
    val description: String?,
    @Embedded val location: Location?,
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
    // Find a way to upoad image
    var images: ImageBitmap? =
        ImageBitmap(30, 30, config = ImageBitmapConfig.Rgb565), // TODO find default image
    val globalRating: Int?,
) : CollectionClass() {
    companion object {
        val testEvent1 = Event(
        id = "1",
        title = "Event Title",
        description = "Hello: I am a description",
        attendanceMaxCapacity = 10,
        attendanceMinCapacity = 1,
        organizerID = Profile.testParticipant().id,
        categories = setOf(Interests.BASKETBALL),
        eventEndDate = LocalDate.of(2024, 4, 15),
        eventStartDate = LocalDate.of(2024, 4, 14),
        inscriptionLimitDate = LocalDate.of(2024, 4, 11),
        inscriptionLimitTime = LocalTime.of(23, 59),
        location = null,
        registeredUsers = mutableListOf("TEST"),
        timeBeginning = LocalTime.of(13, 0),
        globalRating = 4,
        timeEnding = LocalTime.of(16, 0),
        )
        val testEvent2 = Event(
            id = "2",
            title = "Event Title2",
            description = "Hello: I am a description2",
            attendanceMaxCapacity = 20,
            attendanceMinCapacity = 4,
            organizerID = Profile.testParticipant().id,
            categories = setOf(Interests.BASKETBALL),
            eventEndDate = LocalDate.of(2024, 4, 15),
            eventStartDate = LocalDate.of(2024, 4, 14),
            globalRating = 4,
            inscriptionLimitDate = LocalDate.of(2024, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            location = null,
            registeredUsers = mutableListOf("TEST"),
            timeBeginning = LocalTime.of(13, 0),
            timeEnding = LocalTime.of(16, 0),
        )
    }
}

