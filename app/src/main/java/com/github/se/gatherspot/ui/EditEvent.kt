package com.github.se.gatherspot.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.navigation.NavigationActions
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Composable function that give the GUI to edit an event */
@Composable
fun EditEvent(nav: NavigationActions, eventUtils: EventUtils, event: Event) {
  EventDataForm(eventUtils = eventUtils, nav = nav, eventAction = EventAction.EDIT, event = event)
}

@Preview
@Composable
fun EditEventPreview() {
  val testEvent =
      Event(
          eventID = "testID",
          title = "Test Event",
          description = "This is a test event",
          location = Location(0.0, 0.0, "Test Location"),
          eventStartDate =
              LocalDate.parse(
                  "12/04/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          eventEndDate =
              LocalDate.parse(
                  "12/05/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          timeBeginning =
              LocalTime.parse(
                  "10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
          timeEnding =
              LocalTime.parse(
                  "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
          attendanceMaxCapacity = 100,
          attendanceMinCapacity = 10,
          inscriptionLimitDate =
              LocalDate.parse(
                  "10/04/2025", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          inscriptionLimitTime =
              LocalTime.parse(
                  "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
          eventStatus = EventStatus.CREATED,
          globalRating = null)
  EditEvent(NavigationActions(rememberNavController()), EventUtils(), testEvent)
}
