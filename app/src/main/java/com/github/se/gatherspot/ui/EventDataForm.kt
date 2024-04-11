package com.github.se.gatherspot.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.navigation.NavigationActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private val WIDTH = 300.dp
private val WIDTH_2ELEM = 150.dp
private val HEIGHT = 65.dp
private val DESCRIPTION_HEIGHT = 150.dp

private const val TITLE_MESSAGE_INDEX = 0
private const val BUTTON_MESSAGE_INDEX = 1
private const val ERROR_MESSAGE_INDEX = 2
private val CREATE_SPECIFIC_MESSAGES: List<String> =
    listOf("Create a new event", "Create event", "Error on the event creation")
private val EDIT_SPECIFIC_MESSAGES: List<String> =
    listOf("Edit an event", "Edit event", "Error on the event edition")
private val MESSAGES = arrayOf(CREATE_SPECIFIC_MESSAGES, EDIT_SPECIFIC_MESSAGES)

/** Composable routine that creates a scrollable Box with the content passed as a parameter */
@Composable
fun ScrollableContent(content: @Composable () -> Unit) {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) { content() }
  }
}

@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDataForm(
    eventUtils: EventUtils,
    nav: NavigationActions,
    eventAction: EventAction,
    event: Event? = null
) {
  // State of the event
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
  var errorMessage = ""
  var locationName by remember { mutableStateOf("") }
  var categories: MutableList<Interests> = remember { mutableStateListOf() }
  // Flow for query text input
  val queryText = MutableStateFlow("")
  var suggestions: List<Location> by remember { mutableStateOf(emptyList()) }

    // Coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()


  if (eventAction == EventAction.EDIT) {
    event!!
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    title = TextFieldValue(event.title!!)
    description = TextFieldValue(event.description!!)
    location = event.location
    eventStartDate = TextFieldValue(event.eventStartDate!!.format(dateFormatter) ?: "")
    eventEndDate = TextFieldValue(event.eventEndDate?.format(dateFormatter) ?: "")
    eventTimeStart = TextFieldValue(event.timeBeginning!!.format(timeFormatter) ?: "")
    eventTimeEnd = TextFieldValue(event.timeEnding!!.format(timeFormatter) ?: "")
    maxAttendees = TextFieldValue(event.attendanceMaxCapacity?.toString() ?: "")
    minAttendees = TextFieldValue(event.attendanceMinCapacity.toString())
    inscriptionLimitDate = TextFieldValue(event.inscriptionLimitDate?.format(dateFormatter) ?: "")
    inscriptionLimitTime = TextFieldValue(event.inscriptionLimitTime?.format(timeFormatter) ?: "")
  }

  Scaffold(
      modifier = Modifier.testTag("EventDataFormScreen"),
      topBar = {
        MediumTopAppBar(
            title = {
              Text(
                  text = MESSAGES[eventAction.ordinal][TITLE_MESSAGE_INDEX],
                  modifier = Modifier.testTag("createEventTitle"))
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
              Modifier
                  .padding(innerPadding)
                  .padding(horizontal = 28.dp)
                  .testTag("formColumn"),
              verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
              horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(text = "Fields with * are required", modifier = Modifier.testTag("requiredFields"))
            // Title
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputTitle"),
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title*") },
                placeholder = { Text("Give a name to the event") })
            // Description
            OutlinedTextField(
                modifier =
                Modifier
                    .width(WIDTH)
                    .height(DESCRIPTION_HEIGHT)
                    .testTag("inputDescription"),
                value = description,
                onValueChange = { description = it },
                label = { Text("Description*") },
                placeholder = { Text("Describe the event") })
            // Start Date
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputStartDateEvent"),
                value = eventStartDate,
                onValueChange = { eventStartDate = it },
                label = { Text("Start Date of the event*") },
                placeholder = { Text(EventFirebaseConnection.DATE_FORMAT) })
            // End Date
            OutlinedTextField(
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputEndDateEvent"),
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
                      Modifier
                          .width(WIDTH_2ELEM)
                          .height(HEIGHT)
                          .testTag("inputTimeStartEvent"),
                      value = eventTimeStart,
                      onValueChange = { eventTimeStart = it },
                      label = { Text("Start time*") },
                      placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })

                  // Time End
                  OutlinedTextField(
                      modifier =
                      Modifier
                          .width(WIDTH_2ELEM)
                          .height(HEIGHT)
                          .testTag("inputTimeEndEvent"),
                      value = eventTimeEnd,
                      onValueChange = { eventTimeEnd = it },
                      label = { Text("End time*") },
                      placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })
                }
            // Location
            var isDropdownExpanded by remember { mutableStateOf(false) }
            var searchJob by remember { mutableStateOf<Job?>(null) }
            ExposedDropdownMenuBox(
                modifier = Modifier.testTag("locationDropDownMenuBox"),
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it }) {

              OutlinedTextField(
                  modifier = Modifier
                      .menuAnchor()
                      .width(WIDTH)
                      .height(HEIGHT)
                      .testTag("inputLocation"),
                      // Do a query to get a location from text input
                      value = locationName,
                      onValueChange = {newValue ->
                          locationName = newValue
                          //Give focus to the element
                          isDropdownExpanded = true
                          searchJob?.cancel()
                          searchJob = coroutineScope.launch {
                              // Debounce logic: wait for 300 milliseconds after the last text change
                              delay(300)
                              Log.e("EventDataForm", "Querying for $newValue")
                              suggestions = eventUtils.fetchLocationSuggestions(newValue)
                              Log.e("EventDataForm", "Suggestions: $suggestions")
                          }
                      },
                      label = { Text("Location") },
                      placeholder = { Text("Enter an address") })

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded && suggestions.isNotEmpty(),
                    onDismissRequest = { isDropdownExpanded = false }) {
                    suggestions.forEach { suggestion ->
                        Log.e("EventDataForm", "Suggestion: ${suggestion.name}")
                        DropdownMenuItem(
                            modifier = Modifier.testTag("MenuItem"),
                            text = { Text(suggestion.name) },
                            onClick = {
                                location = suggestion
                                locationName = suggestion.name
                                isDropdownExpanded = false
                            })
                    }
                }


            }

            // Categories
            InterestSelector(Interests.entries, categories)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                  // Min attendees
                  OutlinedTextField(
                      modifier =
                      Modifier
                          .width(WIDTH_2ELEM)
                          .height(HEIGHT)
                          .testTag("inputMinAttendees"),
                      value = minAttendees,
                      onValueChange = { minAttendees = it },
                      label = { Text("Min Attendees") },
                      placeholder = { Text("Min Attendees") })
                  // Max attendees
                  OutlinedTextField(
                      modifier =
                      Modifier
                          .width(WIDTH_2ELEM)
                          .height(HEIGHT)
                          .testTag("inputMaxAttendees"),
                      value = maxAttendees,
                      onValueChange = { maxAttendees = it },
                      label = { Text("Max Attendees") },
                      placeholder = { Text("Max Attendees") })
                }

            // Inscription limit date
            OutlinedTextField(
                modifier =
                Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputInscriptionLimitDate"),
                value = inscriptionLimitDate,
                onValueChange = { inscriptionLimitDate = it },
                label = { Text("Inscription Limit Date") },
                placeholder = { Text(EventFirebaseConnection.DATE_FORMAT) })
            // Inscription limit time
            OutlinedTextField(
                modifier =
                Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("inputInscriptionLimitTime"),
                value = inscriptionLimitTime,
                onValueChange = { inscriptionLimitTime = it },
                label = { Text("Inscription Limit Time") },
                placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })

            // TODO :Upload images

            // Button to create the event
            Button(
                onClick = {
                  try {
                      //give the event if update
                    eventUtils.validateAndCreateOrUpdateEvent(
                        title.text,
                        description.text,
                        location,
                        eventStartDate.text,
                        eventEndDate.text,
                        eventTimeStart.text,
                        eventTimeEnd.text,
                        categories.toList(),
                        maxAttendees.text,
                        minAttendees.text,
                        inscriptionLimitDate.text,
                        inscriptionLimitTime.text,
                        eventAction,
                        event
                    )
                  } catch (e: Exception) {
                    errorMessage = e.message.toString()
                    showErrorDialog = true
                  }
                },
                modifier = Modifier
                    .width(WIDTH)
                    .height(HEIGHT)
                    .testTag("createEventButton"),
                enabled =
                    (title.text != "") &&
                        (description.text != "") &&
                        (eventStartDate.text != "") &&
                        (eventTimeStart.text != "") &&
                        (eventTimeEnd.text != ""),
                shape = RoundedCornerShape(size = 10.dp)) {
                  Text(text = MESSAGES[eventAction.ordinal][BUTTON_MESSAGE_INDEX])
                }
          }
        }
        if (showErrorDialog) {
          Alert(MESSAGES[eventAction.ordinal][ERROR_MESSAGE_INDEX], errorMessage) {
            showErrorDialog = false
          }
        }
      }
}

/** Composable function that displays a dropdown menu to select the interests */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestSelector(interests: List<Interests>, categories: MutableList<Interests>) {
  var isExpanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
      modifier = Modifier.testTag("interestSelector"),
      expanded = isExpanded,
      onExpandedChange = { isExpanded = it }) {
        TextField(
            value = categories.joinToString(", "),
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(text = "Select categories") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .width(WIDTH)
                .height(HEIGHT))

        ExposedDropdownMenu(
            modifier = Modifier.testTag("exposedDropdownMenu"),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
              interests.forEach { interest ->
                AnimatedContent(
                    targetState = categories.contains(interest),
                    label = "Animate the selected item") { isSelected ->
                      if (isSelected) {
                        DropdownMenuItem(
                            text = { Text(text = interest.name) },
                            onClick = { categories.remove(interest) },
                            leadingIcon = {
                              Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                            })
                      } else {
                        DropdownMenuItem(
                            text = { Text(text = interest.name) },
                            onClick = { categories.add(interest) },
                        )
                      }
                    }
              }
            }
      }
}

/** Composable function that displays an alert dialog with an error message */
@Composable
fun Alert(errorTitle: String, errorMessage: String, onDismiss: () -> Unit) {
  AlertDialog(
      modifier = Modifier.testTag("alertBox"),
      onDismissRequest = onDismiss,
      icon = { Icon(Icons.Default.Warning, contentDescription = null) },
      title = { Text(errorTitle) },
      text = { Text(modifier = Modifier.testTag("errorMessageIdentifier"), text = errorMessage) },
      confirmButton = {
        Button(onClick = onDismiss, modifier = Modifier.testTag("alertButton")) { Text("OK") }
      },
      dismissButton = {})
}

enum class EventAction {
  CREATE,
  EDIT
}
