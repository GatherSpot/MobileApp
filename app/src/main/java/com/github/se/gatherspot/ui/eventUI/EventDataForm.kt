package com.github.se.gatherspot.ui.eventUI

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.github.se.gatherspot.R
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseImages
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.model.utils.EventUtils
import com.github.se.gatherspot.ui.eventUI.EventAction.CREATE
import com.github.se.gatherspot.ui.eventUI.EventAction.EDIT
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.utils.BannerImagePicker
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val WIDTH = 300.dp
private val WIDTH_2ELEM = 100.dp
private val WIDTH_WITH_PICKER = WIDTH.times(0.8f)
private val HEIGHT = 65.dp
private val DESCRIPTION_HEIGHT = 150.dp
private val FONT_SIZE = 14.sp

private const val TITLE_MESSAGE_INDEX = 0
private const val BUTTON_MESSAGE_INDEX = 1
private const val ERROR_MESSAGE_INDEX = 2
private val CREATE_SPECIFIC_MESSAGES: List<String> =
    listOf("Create a new event", "Create event", "Error on the event creation")
private val EDIT_SPECIFIC_MESSAGES: List<String> =
    listOf("Edit an event", "Edit event", "Error on the event edition")
private val MESSAGES = arrayOf(CREATE_SPECIFIC_MESSAGES, EDIT_SPECIFIC_MESSAGES)

/**
 * Composable function that creates a scrollable Box with the content passed as a parameter
 *
 * @param content the content to be displayed
 * @return a Box with the content passed as a parameter
 */
@Composable
fun ScrollableContent(paddingValues: PaddingValues, content: @Composable () -> Unit) {
  Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) { content() }
  }
}

/**
 * Composable function that displays the form to create or edit an event
 *
 * @param eventUtils the event utilities
 * @param nav the navigation actions
 * @param eventAction the action to perform (create or edit)
 * @param event the event to edit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDataForm(
    eventUtils: EventUtils,
    nav: NavigationActions,
    eventAction: EventAction,
    event: Event? = null,
) {
  // State of the event
  val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
  var title by remember { mutableStateOf(TextFieldValue(event?.title ?: "")) }
  var description by remember { mutableStateOf(TextFieldValue(event?.description ?: "")) }
  var location by remember { mutableStateOf(event?.location) }
  var eventStartDate by remember {
    mutableStateOf(TextFieldValue(event?.eventStartDate?.format(dateFormatter) ?: ""))
  }
  var eventEndDate by remember {
    mutableStateOf(TextFieldValue(event?.eventEndDate?.format(dateFormatter) ?: ""))
  }
  var eventTimeStart by remember {
    mutableStateOf(TextFieldValue(event?.timeBeginning?.format(timeFormatter) ?: ""))
  }
  var eventTimeEnd by remember {
    mutableStateOf(TextFieldValue(event?.timeEnding?.format(timeFormatter) ?: ""))
  }
  var maxAttendees by remember {
    mutableStateOf(TextFieldValue(event?.attendanceMaxCapacity?.toString() ?: ""))
  }
  var minAttendees by remember {
    mutableStateOf(TextFieldValue(event?.attendanceMinCapacity?.toString() ?: ""))
  }
  var inscriptionLimitDate by remember {
    mutableStateOf(TextFieldValue(event?.inscriptionLimitDate?.format(dateFormatter) ?: ""))
  }
  var inscriptionLimitTime by remember {
    mutableStateOf(TextFieldValue(event?.inscriptionLimitTime?.format(timeFormatter) ?: ""))
  }
  var imageUri by remember { mutableStateOf(event?.image ?: "") }

  var showErrorDialog by remember { mutableStateOf(false) }
  var errorMessage = ""
  var locationName by remember { mutableStateOf(event?.location?.name ?: "") }
  val categories: MutableList<Interests> = remember {
    event?.categories?.toMutableStateList() ?: mutableStateListOf()
  }
  // Flow for query text input
  var suggestions: List<Location> by remember { mutableStateOf(emptyList()) }
  // Context to use for draft saving
  val context = LocalContext.current

  // Coroutine scope for launching coroutines
  val coroutineScope = rememberCoroutineScope()
  // Permission handling
  val locationPermissionGranted = remember {
    mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED)
  }
  val requestLocationPermissionLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
          isGranted ->
        locationPermissionGranted.value = isGranted
      }

  // Check and request permission if not already granted
  LaunchedEffect(Unit) {
    if (!locationPermissionGranted.value) {
      requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  // image logic
  val placeHolder = R.drawable.default_event_image
  val updateImageUri: (String) -> Unit = { imageUri = it }
  val deleteImage: () -> Unit = {
    runBlocking {
      // if we create event it should never be already in the database
      if (eventAction == EDIT) {
        event?.id?.let { FirebaseImages().removePicture("eventImage", it) }
      }
    }
    imageUri = ""
  }
  val uploadImage: () -> Unit = {
    if (event != null) {
      runBlocking {
        imageUri = FirebaseImages().pushPicture(imageUri.toUri(), "eventImage", event.id)
      }
    }
  }

  if (eventAction == CREATE) {
    // Restore the draft
    val draft = eventUtils.retrieveFromDraft(context)
    if (draft != null) {
      title = TextFieldValue(draft.title ?: "")
      description = TextFieldValue(draft.description ?: "")
      location = draft.location
      eventStartDate = TextFieldValue(draft.eventStartDate ?: "")
      eventEndDate = TextFieldValue(draft.eventEndDate ?: "")
      eventTimeStart = TextFieldValue(draft.timeBeginning ?: "")
      eventTimeEnd = TextFieldValue(draft.timeEnding ?: "")
      maxAttendees = TextFieldValue(draft.attendanceMaxCapacity ?: "")
      minAttendees = TextFieldValue(draft.attendanceMinCapacity ?: "")
      inscriptionLimitDate = TextFieldValue(draft.inscriptionLimitDate ?: "")
      inscriptionLimitTime = TextFieldValue(draft.inscriptionLimitTime ?: "")
      categories.addAll(draft.categories ?: emptySet())
    }
  }

  var newEvent: Event? = null

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
              // SAVE DRAFT
              IconButton(
                  onClick = {
                    if (eventAction == CREATE) {
                      eventUtils.saveDraftEvent(
                          title.text,
                          description.text,
                          location,
                          eventStartDate.text,
                          eventEndDate.text,
                          eventTimeStart.text,
                          eventTimeEnd.text,
                          maxAttendees.text,
                          minAttendees.text,
                          inscriptionLimitDate.text,
                          inscriptionLimitTime.text,
                          categories.toSet(),
                          image = imageUri,
                          context = context)
                    }
                    nav.goBack()
                  },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        modifier = Modifier.testTag("backIcon"),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to overview")
                  }
            },
            actions = {
              // ERASE FIELDS
              Button(
                  onClick = {
                    if (imageUri.isNotEmpty()) {
                      deleteImage()
                    }
                    title = TextFieldValue("")
                    description = TextFieldValue("")
                    location = null
                    eventStartDate = TextFieldValue("")
                    eventEndDate = TextFieldValue("")
                    eventTimeStart = TextFieldValue("")
                    eventTimeEnd = TextFieldValue("")
                    maxAttendees = TextFieldValue("")
                    minAttendees = TextFieldValue("")
                    inscriptionLimitDate = TextFieldValue("")
                    inscriptionLimitTime = TextFieldValue("")
                    imageUri = ""
                    categories.clear()
                    eventUtils.deleteDraft(context)
                  },
                  modifier = Modifier.testTag("clearFieldsButton"),
                  shape = RoundedCornerShape(size = 10.dp)) {
                    Text(text = "Clear all fields")
                  }
              Spacer(modifier = Modifier.width(10.dp))
              // SAVE DRAFT AGAIN (?)
              Button(
                  onClick = {
                    eventUtils.saveDraftEvent(
                        title.text,
                        description.text,
                        location,
                        eventStartDate.text,
                        eventEndDate.text,
                        eventTimeStart.text,
                        eventTimeEnd.text,
                        maxAttendees.text,
                        minAttendees.text,
                        inscriptionLimitDate.text,
                        inscriptionLimitTime.text,
                        categories.toSet(),
                        image = imageUri,
                        context = context)
                  },
                  modifier = Modifier.testTag("saveDraftButton"),
                  shape = RoundedCornerShape(size = 10.dp)) {
                    Text(text = "Save draft")
                  }
            })
      }) { innerPadding ->
        // Make the content scrollable
        ScrollableContent(innerPadding) {
          // Image picker
          BannerImagePicker(imageUri, placeHolder, "event", updateImageUri, deleteImage)
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
            Row(
                modifier = Modifier.width(WIDTH),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_WITH_PICKER)
                              .height(HEIGHT)
                              .testTag("inputStartDateEvent"),
                      value = eventStartDate,
                      onValueChange = { eventStartDate = it },
                      label = { Text("Start Date of the event*") },
                      placeholder = { Text(EventFirebaseConnection.DATE_FORMAT_DISPLAYED) })

                  MyDatePickerDialog(
                      onDateChange = { eventStartDate = TextFieldValue(it) }, testTag = "start")
                }
            // End Date
            Row(
                modifier = Modifier.width(WIDTH),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_WITH_PICKER)
                              .height(HEIGHT)
                              .testTag("inputEndDateEvent"),
                      value = eventEndDate,
                      onValueChange = { eventEndDate = it },
                      label = { Text("End date of the event") },
                      placeholder = { Text(EventFirebaseConnection.DATE_FORMAT_DISPLAYED) })
                  MyDatePickerDialog(
                      onDateChange = { eventEndDate = TextFieldValue(it) }, testTag = "end")
                }

            Row(
                modifier = Modifier.width(WIDTH),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  // Time Start
                  Row(
                      modifier = Modifier.width(WIDTH.times(0.5f)),
                      horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            modifier =
                                Modifier.width(WIDTH_2ELEM)
                                    .height(HEIGHT)
                                    .testTag("inputTimeStartEvent"),
                            value = eventTimeStart,
                            onValueChange = { eventTimeStart = it },
                            label = { Text("Start time*", fontSize = FONT_SIZE) },
                            placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })

                        MyTimePickerDialog(
                            onTimeChange = { eventTimeStart = TextFieldValue(it) },
                            title = "Select start time",
                            textFieldValue = eventTimeStart)
                      }

                  // Time End
                  Row(
                      modifier = Modifier.width(WIDTH.times(0.5f)),
                      horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            modifier =
                                Modifier.width(WIDTH_2ELEM)
                                    .height(HEIGHT)
                                    .testTag("inputTimeEndEvent"),
                            value = eventTimeEnd,
                            onValueChange = { eventTimeEnd = it },
                            label = { Text("End time*", fontSize = FONT_SIZE) },
                            placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })

                        MyTimePickerDialog(
                            onTimeChange = { eventTimeEnd = TextFieldValue(it) },
                            title = "Select end time",
                            textFieldValue = eventTimeEnd)
                      }
                }
            // Location
            var isDropdownExpanded by remember { mutableStateOf(false) }
            var searchJob by remember { mutableStateOf<Job?>(null) }
            ExposedDropdownMenuBox(
                modifier = Modifier.testTag("locationDropDownMenuBox"),
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it }) {
                  OutlinedTextField(
                      modifier =
                          Modifier.menuAnchor()
                              .width(WIDTH)
                              .height(HEIGHT)
                              .testTag("inputLocation"),
                      // Do a query to get a location from text input
                      value = locationName,
                      onValueChange = { newValue ->
                        locationName = newValue
                        // Give focus to the element
                        isDropdownExpanded = true
                        searchJob?.cancel()
                        searchJob =
                            coroutineScope.launch {
                              // Debounce logic: wait for 300 milliseconds after the last text
                              // change
                              delay(300)
                              suggestions = eventUtils.fetchLocationSuggestions(context, newValue)
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
                modifier = Modifier.width(WIDTH),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  // Min attendees
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_2ELEM).height(HEIGHT).testTag("inputMinAttendees"),
                      value = minAttendees,
                      onValueChange = { minAttendees = it },
                      label = { Text("Min Attendees", fontSize = FONT_SIZE) },
                      placeholder = { Text("Min") })
                  // Max attendees
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_2ELEM).height(HEIGHT).testTag("inputMaxAttendees"),
                      value = maxAttendees,
                      onValueChange = { maxAttendees = it },
                      label = { Text("Max Attendees", fontSize = FONT_SIZE) },
                      placeholder = { Text("Max") })
                }

            // Inscription limit date
            Row(
                modifier = Modifier.width(WIDTH),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_WITH_PICKER)
                              .height(HEIGHT)
                              .testTag("inputInscriptionLimitDate"),
                      value = inscriptionLimitDate,
                      onValueChange = { inscriptionLimitDate = it },
                      label = { Text("Inscription Limit Date", fontSize = FONT_SIZE) },
                      placeholder = { Text(EventFirebaseConnection.DATE_FORMAT_DISPLAYED) })

                  MyDatePickerDialog(
                      onDateChange = { inscriptionLimitDate = TextFieldValue(it) },
                      testTag = "inscriptionLimitDatePicker")
                }
            // Inscription limit time
            Row(
                modifier = Modifier.width(WIDTH),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  OutlinedTextField(
                      modifier =
                          Modifier.width(WIDTH_WITH_PICKER)
                              .height(HEIGHT)
                              .testTag("inputInscriptionLimitTime"),
                      value = inscriptionLimitTime,
                      onValueChange = { inscriptionLimitTime = it },
                      label = { Text("Inscription Limit Time", fontSize = FONT_SIZE) },
                      placeholder = { Text(EventFirebaseConnection.TIME_FORMAT) })

                  MyTimePickerDialog(
                      onTimeChange = { inscriptionLimitTime = TextFieldValue(it) },
                      title = "Select inscription limit time",
                      textFieldValue = inscriptionLimitTime)
                }
            // CREATE EVENT
            Button(
                onClick = {
                  try {
                    // give the event if update
                    uploadImage()
                    newEvent =
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
                            event,
                            imageUri)
                  } catch (e: Exception) {
                    errorMessage = e.message.toString()
                    showErrorDialog = true
                  }
                  // Delete the draft
                  eventUtils.deleteDraft(context)

                  if (!showErrorDialog) {
                    if (eventAction == CREATE) {
                      // Go back to the list of events
                      nav.controller.navigate("events")
                    } else {
                      // Go back to the event details
                      val json = newEvent!!.toJson()
                      nav.controller.navigate("event/$json")
                    }
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

  Log.d("ll", "end")
}

/**
 * Composable function that displays a dropdown menu to select the interests
 *
 * @param interests the list of interests
 * @param categories the list of categories
 */
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
            modifier = Modifier.menuAnchor().width(WIDTH).height(HEIGHT))

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

/**
 * Composable function that displays an alert dialog with an error message
 *
 * @param errorTitle the title of the error
 * @param errorMessage the error message
 * @param onDismiss the action to perform when the dialog is dismissed
 */
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

/**
 * Enum class that represents the possible actions to perform on an event
 *
 * @property CREATE the action to create an event
 * @property EDIT the action to edit an event
 */
enum class EventAction {
  CREATE,
  EDIT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
  fun convertMillisToDate(millis: Long): String {
    val formatter =
        SimpleDateFormat(EventFirebaseConnection.DATE_FORMAT_DISPLAYED, Locale.getDefault())
    return formatter.format(Date(millis))
  }
  val datePickerState = rememberDatePickerState()

  val selectedDate = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: ""

  DatePickerDialog(
      onDismissRequest = { onDismiss() },
      confirmButton = {
        Button(
            onClick = {
              onDateSelected(selectedDate)
              onDismiss()
            }) {
              Text(text = "OK")
            }
      },
      dismissButton = { Button(onClick = { onDismiss() }) { Text(text = "Cancel") } }) {
        DatePicker(state = datePickerState)
      }
}

@Composable
fun MyDatePickerDialog(onDateChange: (String) -> Unit, testTag: String) {

  var showDatePicker by remember { mutableStateOf(false) }

  androidx.compose.material.IconButton(
      onClick = {
        // Open DatePickerDialog
        showDatePicker = true
      },
      modifier = Modifier.height(HEIGHT).testTag("DatePickerButton$testTag"),
  ) {
    androidx.compose.material.Icon(
        modifier = Modifier.size(HEIGHT.times(0.5f)).testTag("DatePickerIcon$testTag"),
        painter = rememberVectorPainter(image = Icons.Filled.DateRange),
        contentDescription = "Date picker icon")
  }

  if (showDatePicker) {
    MyDatePickerDialog(onDateSelected = { onDateChange(it) }) { showDatePicker = false }
  }
}

@Composable
fun MyTimePickerDialog(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    initialTime: String,
    title: String
) {

  val listener =
      TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        val time = (hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0'))
        onTimeSelected(time)
        onDismiss()
      }

  val initialHour =
      try {
        initialTime.split(":", limit = 2)[0].toInt()
      } catch (e: Exception) {
        12
      }
  val initialMinutes =
      try {
        initialTime.split(":", limit = 2)[1].toInt()
      } catch (e: Exception) {
        0
      }

  val timePickerDialog =
      TimePickerDialog(LocalContext.current, listener, initialHour, initialMinutes, true)

  timePickerDialog.setMessage(title)
  timePickerDialog.setOnDismissListener { onDismiss() }

  timePickerDialog.show()
}

@Composable
fun MyTimePickerDialog(
    onTimeChange: (String) -> Unit,
    title: String,
    textFieldValue: TextFieldValue
) {

  var showTimePicker by remember { mutableStateOf(false) }

  androidx.compose.material.IconButton(
      onClick = {
        // Open TimePickerDialog
        showTimePicker = true
      },
      modifier = Modifier.height(HEIGHT).testTag("TimePickerButton$title"),
  ) {
    androidx.compose.material.Icon(
        modifier = Modifier.size(HEIGHT.times(0.5f)).testTag("TimePickerIcon$title"),
        painter = rememberVectorPainter(image = Icons.Filled.WatchLater),
        contentDescription = "Time picker icon $title")
  }

  if (showTimePicker) {
    MyTimePickerDialog(
        onTimeSelected = { onTimeChange(it) },
        onDismiss = { showTimePicker = false },
        initialTime = textFieldValue.text,
        title = title)
  }
}
