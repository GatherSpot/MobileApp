package com.github.se.gatherspot.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.navigation.NavigationActions

/** Composable function that give the GUI to edit an event */
@Composable
fun EditEvent(nav: NavigationActions, eventViewModel: EventViewModel, eventID : String) {
    EventDataForm(eventViewModel = eventViewModel, nav = nav, eventAction = EventAction.EDIT) {

    }
}
