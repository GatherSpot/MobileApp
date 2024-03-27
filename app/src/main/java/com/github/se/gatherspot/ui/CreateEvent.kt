package com.github.se.gatherspot.ui

// GUI to create an event
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.navigation.NavigationActions


//Constant values for the the width and the height of the text fields
private val WIDTH = 340.dp
private val HEIGHT = 65.dp
private val DESCRIPTION_HEIGHT = 150.dp

/**
 * Composable function that gives the GUI to create an event
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent() {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var location by remember { mutableStateOf<Location?>(null) }
    var eventDate by remember { mutableStateOf(TextFieldValue("")) }
    var eventTimeStart by remember { mutableStateOf(TextFieldValue("")) }
    var eventTimeEnd by remember { mutableStateOf(TextFieldValue("")) }
    var maxAttendees by remember { mutableStateOf(TextFieldValue("")) }
    var minAttendees by remember { mutableStateOf(TextFieldValue("")) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }



    Scaffold(
        modifier = Modifier.testTag("CreateEventScreen"),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Create a new task",
                        modifier = Modifier.testTag("createEventTitle")
                    )
                },

            )
        }


    ) { innerPadding ->
        // Create event form
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Text(
                text = "Fields with * are required",
                modifier = Modifier.testTag("requiredFields")
            )
            // Title
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputTitle"),
                value = title,
                onValueChange = { title = it },
                label = { Text("Event title *") },
                placeholder = { Text(text = "Give a name to the event") })
            // Description
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(DESCRIPTION_HEIGHT)
                    .testTag("inputDescription"),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description *") },
                placeholder = { Text("Describe the event") })
            // Date
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputDateEvent"),
                value = eventDate,
                onValueChange = { eventDate = it },
                label = { Text("Date *") },
                placeholder = { Text("dd/MM/yyyy") })
            // Time Start
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputTimeStartEvent"),
                value = eventTimeStart,
                onValueChange = { eventTimeStart = it },
                label = { Text("Start time*") },
                placeholder = { Text("hh:mm") })
            // Time End
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputTimeEndEvent"),
                value = eventTimeEnd,
                onValueChange = { eventTimeEnd = it },
                label = { Text("End time*") },
                placeholder = { Text("hh:mm") })
            // TODO : Location
            // Location


            // Max attendees
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputMaxAttendees"),
                value = maxAttendees,
                onValueChange = { maxAttendees = it },
                label = { Text("Max attendees") },
                placeholder = { Text("Maximum number of attendees") })
            //TODO: add a min attendee

            // TODO :Upload images


            // Button to create the event
            Button(
                onClick = {
                    println("CLICKED")
                    try {
                        EventViewModel.validateEventData(
                            title.text,
                            description.text,
                            location!!,
                            eventDate.text,
                            eventTimeStart.text,
                            eventTimeEnd.text,
                            maxAttendees.text
                        )
                    } catch (e: Exception) {
                        // Display error message
                        errorMessage = e.message ?: "An error occurred"
                        showErrorDialog = true
                    }
                },
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("createEventButton"),
                //enabled = (title.text != "") && (description.text != "") && (eventDate.text != "")
                 //       && (eventTimeStart.text != "") && (eventTimeEnd.text != ""),
                shape = RoundedCornerShape(size = 10.dp)
            ) {
                Text(text = "Create event")
            }
        }
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text("Error on the event creation") },
                text = { Text(errorMessage.toString()) },
                confirmButton = {
                    Button(
                        onClick = { showErrorDialog = false },
                        modifier = Modifier.testTag("alertButton")
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {}
            )
        }
    }
}




@Preview
@Composable
fun CreateEventPreview() {
    CreateEvent()
}