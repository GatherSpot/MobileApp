package com.github.se.gatherspot.ui

// GUI to create an event

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.navigation.NavigationActions

/** Composable function that gives the GUI to create an event */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent(nav: NavigationActions, eventUtils: EventUtils) {
  EventDataForm(eventUtils = eventUtils, nav = nav, eventAction = EventAction.CREATE)
}

@Preview
@Composable
fun CreateEventPreview() {
  CreateEvent(NavigationActions(rememberNavController()), EventUtils())
}
