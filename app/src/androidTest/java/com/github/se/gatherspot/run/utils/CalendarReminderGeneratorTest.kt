package com.github.se.gatherspot.run.utils

import android.content.Intent
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.eventUI.CalendarReminderGenerator
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CalendarReminderGeneratorTest {

  @Test
  fun testGenerateCalendarReminder() {
    val event =
        Event(
            id = "calendarReminderTest",
            title = "RemindMeOfThisEvent",
            description = "An event that should be in Calendar",
            location = Location(200.0, 200.0, "location"),
            eventStartDate = LocalDate.of(2024, 5, 19),
            eventEndDate = LocalDate.of(2024, 5, 20),
            timeBeginning = LocalTime.of(20, 0),
            timeEnding = LocalTime.of(23, 0),
            attendanceMaxCapacity = null,
            attendanceMinCapacity = 0,
            inscriptionLimitDate = null,
            inscriptionLimitTime = null,
            eventStatus = EventStatus.CREATED,
            categories = setOf(),
            organizerID = "",
            registeredUsers = mutableListOf(),
            finalAttendees = listOf(),
            image = "",
            globalRating = null)
    val intent = CalendarReminderGenerator.generateCalendarReminder(event)
    assertNotNull(intent)
    assertEquals(intent.action, Intent.ACTION_INSERT)
    assertEquals(intent.getStringExtra(Events.TITLE), event.title)
    assertEquals(intent.getStringExtra(Events.DESCRIPTION), event.description)
    assertEquals(intent.getStringExtra(Events.EVENT_LOCATION), event.location?.name)
    intent.data?.let { assertEquals(it.toString(), CalendarContract.Events.CONTENT_URI.toString()) }
  }
}
