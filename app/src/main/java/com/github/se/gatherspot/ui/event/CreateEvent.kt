package com.github.se.gatherspot.ui.event

// GUI to create an event

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.ui.TopLevelDestinations.EventsViewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions

/** Composable function that gives the GUI to create an event */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent(nav: NavigationActions, eventUtils: EventUtils, viewModel: EventsViewModel) {
  EventDataForm(
      eventUtils = eventUtils, viewModel = viewModel, nav = nav, eventAction = EventAction.CREATE
  )
}

// Not sure if instantiating a new viewModel for preview is ok?
@Preview
@Composable
fun CreateEventPreview() {
  CreateEvent(NavigationActions(rememberNavController()), EventUtils(), EventsViewModel())
}
