package com.github.se.gatherspot.ui.eventUI

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.sql.EventDao
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.EventsViewModel

/**
 * Composable function that gives the GUI to create an event
 *
 * @param nav the navigation actions
 * @param eventUtils the event utilities
 * @param viewModel the events view model
 */
@Composable
fun CreateEvent(
    nav: NavigationActions,
    eventUtils: EventUtils,
    viewModel: EventsViewModel,
    eventDao: EventDao?
) {
  EventDataForm(
      eventUtils = eventUtils,
      viewModel = viewModel,
      nav = nav,
      eventAction = EventAction.CREATE,
      eventDao = eventDao)
}

@Preview
@Composable
fun CreateEventPreview() {
//  CreateEvent(NavigationActions(rememberNavController()), EventUtils(), EventsViewModel(), null)
}
