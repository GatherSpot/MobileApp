package com.github.se.gatherspot.model.event

import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.location.Location

/**
 * Represents a draft event that is being created by a user.
 *
 * @property title Title of the event.
 * @property description Description of the event.
 * @property location Location of the event.
 * @property eventStartDate Start date of the event.
 * @property eventEndDate End date of the event.
 * @property timeBeginning Beginning time of the event.
 * @property timeEnding Ending time of the event.
 * @property attendanceMaxCapacity Maximum capacity of the event.
 * @property attendanceMinCapacity Minimum capacity of the event.
 * @property inscriptionLimitDate Limit date for inscriptions.
 * @property inscriptionLimitTime Limit time for inscriptions.
 * @property categories Categories of the event.
 * @property image Image of the event.
 */
data class DraftEvent(
    val title: String?,
    val description: String?,
    val location: Location?,
    val eventStartDate: String?,
    val eventEndDate: String?,
    val timeBeginning: String?,
    val timeEnding: String?,
    val attendanceMaxCapacity: String?,
    val attendanceMinCapacity: String?,
    val inscriptionLimitDate: String?,
    val inscriptionLimitTime: String?,
    val categories: Set<Interests>? = emptySet(),
    val image: String
)
