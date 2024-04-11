package com.github.se.gatherspot.ui

import androidx.compose.runtime.Composable
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.navigation.NavigationActions

/** Composable function that give the GUI to edit an event */
@Composable
fun EditEvent(nav: NavigationActions, eventUtils: EventUtils, event: Event) {
  EventDataForm(eventUtils = eventUtils, nav = nav, eventAction = EventAction.EDIT, event = event)
}
