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
