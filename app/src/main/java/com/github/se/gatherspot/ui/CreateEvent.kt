package com.github.se.gatherspot.ui

// GUI to create an event
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.navigation.NavigationActions

// Constant values for the the width and the height of the text fields
private val WIDTH = 300.dp
private val WIDTH_2ELEM = 150.dp
private val HEIGHT = 65.dp
private val DESCRIPTION_HEIGHT = 150.dp

/** Composable routine that creates a scrollable Box with the content passed as a parameter */
@Composable
fun ScrollableContent(content: @Composable () -> Unit) {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) { content() }
  }
}

/** Composable function that gives the GUI to create an event */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent(nav: NavigationActions, eventViewModel: EventViewModel) {
  var title by remember { mutableStateOf(TextFieldValue("")) }
  var description by remember { mutableStateOf(TextFieldValue("")) }
  var location by remember { mutableStateOf<Location?>(null) }
  var eventStartDate by remember { mutableStateOf(TextFieldValue("")) }
  var eventEndDate by remember { mutableStateOf(TextFieldValue("")) }
  var eventTimeStart by remember { mutableStateOf(TextFieldValue("")) }
  var eventTimeEnd by remember { mutableStateOf(TextFieldValue("")) }
  var maxAttendees by remember { mutableStateOf(TextFieldValue("")) }
  var minAttendees by remember { mutableStateOf(TextFieldValue("")) }
  var inscriptionLimitDate by remember { mutableStateOf(TextFieldValue("")) }
  var inscriptionLimitTime by remember { mutableStateOf(TextFieldValue("")) }

  var showErrorDialog by remember { mutableStateOf(false) }
  var errorMessage: String = ""
  var locationName by remember { mutableStateOf("") }

  // TODO: For now, the location is not handled
  location = Location(0.0, 0.0, "Test Location")

  Scaffold(
      modifier = Modifier.testTag("CreateEventScreen"),
      topBar = {
        MediumTopAppBar(
            title = {
              Text(text = "Create a new event", modifier = Modifier.testTag("createEventTitle"))
            },
            navigationIcon = {
              IconButton(onClick = { nav.goBack() }, modifier = Modifier.testTag("goBackButton")) {
                Icon(
                    modifier = Modifier.testTag("backIcon"),
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back to overview")
              }
            })
      }) { innerPadding ->
        // Make the content scrollable
        ScrollableContent {
          // Create event form
          Column(
              modifier =
                  Modifier.padding(innerPadding).padding(horizontal = 28.dp).testTag("formColumn"),
              verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
              horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(text = "Fields with * are required", modifier = Modifier.testTag("requiredFields"))
            // Title
            OutlinedTextField(
                modifier = Modifier.width(WIDTH).height(HEIGHT).testTag("inputTitle"),
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title*") },
                placeholder = { Text("Give a name to the event") })
            // Description
            OutlinedTextField(
                modifier =
                    Modifier.width(WIDTH).height(DESCRIPTION_HEIGHT).testTag("inputDescription"),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description*") },
                placeholder = { Text("Describe the event") })
            // Start Date
            OutlinedTextField(
                modifier = Modifier.width(WIDTH).height(HEIGHT).testTag("inputStartDateEvent"),
                value = eventStartDate,
                onValueChange = { eventStartDate = it },
                label = { Text("Start Date of the event*") },
                placeholder = { Text(EventFirebaseConnection.DATE_FORMAT) })
            // End Date
            OutlinedTextField(
                modifier = Modifier.width(WIDTH).height(HEIGHT).testTag("inputEndDateEvent"),
                value = eventEndDate,
                onValueChange = { eventEndDate = it },
                label = { Text("End date of the event") },
                placeholder = { Text(EventFirebaseConnection.DATE_FORMAT) })

            // Time Start
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_2ELEM).height(HEIGHT).testTag("inputTimeStartEvent"),
                      value = eventTimeStart,
                      onValueChange = { eventTimeStart = it },
                      label = { Text("Start time*") },
                      placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })

                  // Time End
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_2ELEM).height(HEIGHT).testTag("inputTimeEndEvent"),
                      value = eventTimeEnd,
                      onValueChange = { eventTimeEnd = it },
                      label = { Text("End time*") },
                      placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })
                }
            // Location
            OutlinedTextField(
                modifier = Modifier.width(WIDTH).height(HEIGHT).testTag("inputLocation"),
                // Do a query to get a location from text input
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Location") },
                placeholder = { Text("Enter an address") })

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                  // Min attendees
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_2ELEM).height(HEIGHT).testTag("inputMinAttendees"),
                      value = minAttendees,
                      onValueChange = { minAttendees = it },
                      label = { Text("Min Attendees") },
                      placeholder = { Text("Min Attendees") })
                  // Max attendees
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_2ELEM).height(HEIGHT).testTag("inputMaxAttendees"),
                      value = maxAttendees,
                      onValueChange = { maxAttendees = it },
                      label = { Text("Max Attendees") },
                      placeholder = { Text("Max Attendees") })
                }

            // Inscription limit date
            OutlinedTextField(
                modifier =
                    Modifier.width(WIDTH).height(HEIGHT).testTag("inputInscriptionLimitDate"),
                value = inscriptionLimitDate,
                onValueChange = { inscriptionLimitDate = it },
                label = { Text("Inscription Limit Date") },
                placeholder = { Text(EventFirebaseConnection.DATE_FORMAT) })
            // Inscription limit time
            OutlinedTextField(
                modifier =
                    Modifier.width(WIDTH).height(HEIGHT).testTag("inputInscriptionLimitTime"),
                value = inscriptionLimitTime,
                onValueChange = { inscriptionLimitTime = it },
                label = { Text("Inscription Limit Time") },
                placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })

            // TODO :Upload images

            // Button to create the event
            Button(
                onClick = {
                  try {
                    eventViewModel.validateAndCreateEvent(
                        title.text,
                        description.text,
                        location!!,
                        eventStartDate.text,
                        eventEndDate.text,
                        eventTimeStart.text,
                        eventTimeEnd.text,
                        maxAttendees.text,
                        minAttendees.text,
                        inscriptionLimitDate.text,
                        inscriptionLimitTime.text)
                  } catch (e: Exception) {
                    // Display error message
                    errorMessage = e.message.toString()
                    showErrorDialog = true
                  }
                },
                modifier = Modifier.width(WIDTH).height(HEIGHT).testTag("createEventButton"),
                enabled =
                    (title.text != "") &&
                        (description.text != "") &&
                        (eventStartDate.text != "") &&
                        (eventTimeStart.text != "") &&
                        (eventTimeEnd.text != ""),
                shape = RoundedCornerShape(size = 10.dp)) {
                  Text(text = "Create event")
                }
          }
        }
        if (showErrorDialog) {
          Alert("Error on the event creation", errorMessage) { showErrorDialog = false }
        }
      }
}

@Composable
fun Alert(errorTitle: String, errorMessage: String, onDismiss: () -> Unit) {
  AlertDialog(
      modifier = Modifier.testTag("alertBox"),
      onDismissRequest = onDismiss,
      icon = { Icon(Icons.Default.Warning, contentDescription = null) },
      title = { Text(errorTitle) },
      text = { Text(modifier = Modifier.testTag("errorMessage"), text = errorMessage) },
      confirmButton = {
        Button(onClick = onDismiss, modifier = Modifier.testTag("alertButton")) { Text("OK") }
      },
      dismissButton = {})
}

@Preview
@Composable
fun CreateEventPreview() {
  CreateEvent(NavigationActions(rememberNavController()), EventViewModel())
}
