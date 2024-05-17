package com.github.se.gatherspot.ui.eventUI

import androidx.compose.runtime.Composable
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.EventsViewModel

/** Composable function that give the GUI to edit an event */
@Composable
fun EditEvent(
    nav: NavigationActions,
    eventUtils: EventUtils,
    event: Event,
    viewModel: EventsViewModel
) {
  EventDataForm(
      eventUtils = eventUtils,
      viewModel = viewModel,
      nav = nav,
      eventAction = EventAction.EDIT,
      event = event)
}
