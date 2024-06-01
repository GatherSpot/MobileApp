package com.github.se.gatherspot.ui.eventUI

import androidx.compose.runtime.Composable
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.utils.EventUtils
import com.github.se.gatherspot.ui.navigation.NavigationActions

/**
 * Composable function that give the GUI to edit an event
 *
 * @param nav the navigation actions
 * @param eventUtils the event utilities
 * @param event the event to edit
 */
@Composable
fun EditEvent(
    nav: NavigationActions,
    eventUtils: EventUtils,
    event: Event,
) {
  EventDataForm(eventUtils = eventUtils, nav = nav, eventAction = EventAction.EDIT, event = event)
}
