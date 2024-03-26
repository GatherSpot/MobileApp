package com.github.se.gatherspot.model

import android.location.Location
import android.media.Image

data class Event(
    val eventID: Int,
    val title: String,
    val description: String?,
    val location: Location?,
    val attendanceCapacity: Int?,
    val date: java.time.LocalDate?,
    val timeBeginning: java.time.LocalTime?,
    val timEnding: java.time.LocalTime?,
    val status: EventStatus = EventStatus.DRAFT,
    // List of User, but for now before the implementation of user class just use String
    val registeredUsers: List<String> = emptyList(),
    val finalAttendee: List<String> = emptyList(),
    val images : List<Image> = emptyList(),
    val globalRating : Int?
)



enum class EventStatus {
    DRAFT,
    CREATED,
    ON_GOING,
    COMPLETED
}