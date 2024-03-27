package com.github.se.gatherspot.ui

// GUI to create an event
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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


// final values for the padding, the width and the height of the text fields
private val PADDING = 28.dp
private val WIDTH = 340.dp
private val HEIGHT = 50.dp
private val DESCRIPTION_HEIGHT = 150.dp

/**
 * Composable function that gives the GUI to create an event
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent(eventViewModel: EventViewModel, nav: NavigationActions) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var location by remember { mutableStateOf<Location?>(null)}
    var eventDate by remember { mutableStateOf(TextFieldValue("")) }
    var eventTimeStart by remember { mutableStateOf(TextFieldValue("")) }
    var eventTimeEnd by remember { mutableStateOf(TextFieldValue("")) }
    var maxAttendees by remember { mutableStateOf(TextFieldValue("")) }


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
                navigationIcon = {
                    IconButton(
                        onClick = { nav.goBack() }, modifier = Modifier.testTag("goBackButton")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }


    ){ innerPadding ->
        // Create event form
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            // Title
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputTitle"),
                value = title,
                onValueChange = { title = it },
                label = { Text("Event title") },
                placeholder = { Text(text = "Give a name to the event") })
            // Description
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(DESCRIPTION_HEIGHT)
                    .testTag("inputDescription"),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Describe the event") })
            // Date
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputDateEvent"),
                value = eventDate,
                onValueChange = { eventDate = it },
                label = { Text("Date") },
                placeholder = { Text("dd/MM/yyyy") })
            // Time Start
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputTimeStartEvent"),
                value = eventTimeStart,
                onValueChange = { eventTimeStart = it },
                label = { Text("Start time") },
                placeholder = { Text("hh:mm") })
            // Time End
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputTimeEndEvent"),
                value = eventTimeEnd,
                onValueChange = { eventTimeEnd = it },
                label = { Text("End time") },
                placeholder = { Text("hh:mm") })
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

        }

    }
}



//@Preview
//@Composable
//fun CreateEventPreview() {
//    CreateEvent()
//}