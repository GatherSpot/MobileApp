package com.github.se.gatherspot.ui.eventUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.getEventImageHeader
import com.github.se.gatherspot.sql.EventDao
import com.github.se.gatherspot.ui.eventUI.CalendarReminderGenerator.Companion.generateCalendarReminder
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.qrcode.EventQRCodeUI
import com.github.se.gatherspot.ui.topLevelDestinations.GeoMap
import com.github.se.gatherspot.utils.BannerImageViewer
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Composable that displays the UI for an event. It displays the title, description, organizer,
 * rating, categories, location, dates and times. It is different for the organizer of the event and
 * for the participants.
 *
 * @param event the event to display
 * @param navActions the navigation actions
 * @param eventUIViewModel the view model for the event UI
 */
@Composable
fun EventUI(
    event: Event,
    navActions: NavigationActions,
    eventUIViewModel: EventUIViewModel,
    eventDao: EventDao?
) {
  if (event.organizerID == Firebase.auth.currentUser?.uid) {
    EventUIOrganizer(event, navActions, eventUIViewModel, eventDao)
  } else {
    EventUINonOrganizer(event, navActions, eventUIViewModel, eventDao)
  }
}

/**
 * EventUINonOrganizer displays the UI for an event for a participant. It displays the title,
 * description, organizer, rating, categories, location, dates and times, and a button to register
 * for the event.
 *
 * @param event the event to display
 * @param navActions the navigation actions
 * @param eventUIViewModel the view model for the event UI
 */
@Composable
fun EventUINonOrganizer(
    event: Event,
    navActions: NavigationActions,
    eventUIViewModel: EventUIViewModel,
    eventDao: EventDao?
) {

  val showDialogRegistration by eventUIViewModel.displayAlertRegistration.observeAsState()
  val ownRating by eventUIViewModel.ownRating.observeAsState()
  val eventRating by eventUIViewModel.eventRating.observeAsState()
  val organizerRating by eventUIViewModel.organizerRating.observeAsState()
  val organizerProfile by eventUIViewModel.organizer.observeAsState()

  val registrationState by eventUIViewModel.registrationState.observeAsState()
  val isButtonEnabled = eventUIViewModel.registrationState.value !is RegistrationState.Error

  val buttonText =
      when (registrationState) {
        is RegistrationState.Registered -> "Registered / Unregister"
        is RegistrationState.Unregistered -> "Unregistered / Register"
        is RegistrationState.Error -> (registrationState as RegistrationState.Error).message
        else -> "Register"
      }

  Scaffold(
      modifier = Modifier.testTag("EventUIScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topBar"),
            title = { Text(text = event.title) },
            backgroundColor = Color.White,
            navigationIcon = {
              IconButton(
                  onClick = { navActions.controller.navigate("events") },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to overview")
                  }
            },
            actions = { ExportToCalendarIcon(event) })
      },
      bottomBar = {
        Column {
          if (eventUIViewModel.canAttend())
              AttendButton(
                  event,
                  eventUIViewModel,
              )
          RegisterButton(
              eventUIViewModel,
              isButtonEnabled,
              buttonText,
              showDialogRegistration,
              registrationState,
              eventDao)
        }
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .padding(8.dp)
                    .testTag("eventColumn")
                    .verticalScroll(rememberScrollState())) {
              EventBody(event, organizerProfile, organizerRating ?: 0.0, navActions)
              // Rating Action
              if (eventUIViewModel.canRate()) {
                RatingDisplay(
                    ownRating = ownRating ?: Rating.UNRATED, eventUIViewModel = eventUIViewModel)
              }

              EventRating(eventRating)

              Spacer(modifier = Modifier.height(16.dp).testTag("bottomSpacer"))

              OutlinedButton(
                  onClick = { navActions.controller.navigate("nfc_participant/${event.toJson()}") },
                  enabled = EventUtils().isEventUnderway(event),
                  modifier = Modifier.testTag("checkin")) {
                    Text("Check-in")
                  }
            }
      }
}

@Composable
fun AttendButton(event: Event, eventUIViewModel: EventUIViewModel) {
  val showDialogAttend by eventUIViewModel.displayAlertAttend.observeAsState()
  val attended by eventUIViewModel.attended.observeAsState()
  val buttonText = if (attended == true) "Attended" else "Attend"
  /*
  when (registrationState) {
      is RegistrationState.Success -> "Registered"
      is RegistrationState.Error ->
          if ((registrationState as RegistrationState.Error).message == "Event is full") "Full"
          else "Registered"
      else -> "Register"
  }

   */

  Button(
      onClick = { /*eventUIViewModel.attendEvent()*/},
      enabled = (attended == false),
      modifier = Modifier.fillMaxWidth().testTag("attendButton"),
      colors = ButtonDefaults.buttonColors(Color(0xFF3A89C9))) {
        Text(buttonText, color = Color.White)
      }

  if (showDialogAttend!!) {
    AlertDialog(
        modifier = Modifier.testTag("alertBox"),
        onDismissRequest = { eventUIViewModel.dismissAlert() },
        title = { Text("Attending Result") },
        text = { Text("Attended") },
        confirmButton = {
          Button(
              modifier = Modifier.testTag("okButton"),
              onClick = { eventUIViewModel.dismissAlert() }) {
                Text("OK")
              }
        })
  }
}

/**
 * EventUIOrganizer displays the UI for an event for the organizer. It displays the title,
 * description, organizer, rating, categories, location, dates and times, and buttons to edit and
 * delete the event.
 *
 * @param event the event to display
 * @param navActions the navigation actions
 * @param eventUIViewModel the view model for the event UI
 */
@Composable
fun EventUIOrganizer(
    event: Event,
    navActions: NavigationActions,
    eventUIViewModel: EventUIViewModel,
    eventDao: EventDao?
) {

  val organizerRating by eventUIViewModel.organizerRating.observeAsState()
  val eventRating by eventUIViewModel.eventRating.observeAsState()
  val showDialogDelete by eventUIViewModel.displayAlertDeletion.observeAsState()

  val organizerProfile by eventUIViewModel.organizer.observeAsState()

  val eventUtils = EventUtils()

  Scaffold(
      modifier = Modifier.testTag("EventUIScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topBar"),
            title = { Text(text = event.title) },
            backgroundColor = Color.White,
            navigationIcon = {
              IconButton(
                  onClick = { navActions.controller.navigate("events") },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to overview")
                  }
            },
            actions = {
              // Edit button
              IconButton(
                  onClick = { navActions.controller.navigate("editEvent/${event.toJson()}") },
                  modifier = Modifier.testTag("editEventButton")) {
                    Icon(
                        modifier = Modifier.size(24.dp).testTag("editEventIcon"),
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit event")
                  }
              // Delete button
              IconButton(
                  onClick = { eventUIViewModel.clickDeleteButton() },
                  modifier = Modifier.testTag("deleteEventButton")) {
                    Icon(
                        modifier = Modifier.size(24.dp).testTag("deleteEventIcon"),
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete event")
                  }
              ExportToCalendarIcon(event)
            })
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .padding(8.dp)
                    .testTag("eventColumn")
                    .verticalScroll(rememberScrollState())) {
              EventBody(
                  event = event,
                  organizerProfile = organizerProfile,
                  organizerRating = organizerRating,
                  navActions = navActions)
              EventRating(eventRating = eventRating)
              OutlinedButton(
                  onClick = { navActions.controller.navigate("nfc_organizer/${event.toJson()}") },
                  enabled = EventUtils().isEventUnderway(event),
                  modifier = Modifier.testTag("verifyCheckin")) {
                    Text("Verify check-ins")
                  }
            }
      }
  if (showDialogDelete!!) {
    AlertDialog(
        modifier = Modifier.testTag("alertBox"),
        onDismissRequest = { eventUIViewModel.dismissAlert() },
        title = { Text("Delete Event") },
        text = {
          Text("Are you sure you want to delete this event? This action cannot be undone.")
        },
        confirmButton = {
          Button(
              modifier = Modifier.testTag("okButton"),
              onClick = {
                // Delete the event
                eventUtils.deleteEvent(event, eventDao)
                navActions.controller.navigate("events")
              }) {
                Text("Delete")
              }
        },
        dismissButton = {
          Button(
              modifier = Modifier.testTag("cancelButton"),
              onClick = { eventUIViewModel.dismissAlert() }) {
                Text("Cancel")
              }
        })
  }
}

/** EventRating displays the rating of the event. */
@Composable
fun EventRating(eventRating: Double?) {
  if ((eventRating ?: 0.0) > 0.0) {
    Modifier.testTag("eventRating")
    Spacer(modifier = Modifier.height(4.dp))
    Row {
      Text(text = "$eventRating", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      Text(text = "/ 5.0", fontWeight = FontWeight.Light, fontSize = 16.sp)
    }
  }
}

/**
 * RegisterButton displays a button to register/unregister to an event. It also displays an alert
 * dialog to show the result of the registration.
 *
 * @param event the event to register/unregister for
 * @param eventUIViewModel the view model for the event UI
 * @param isButtonEnabled true if the button is enabled, false otherwise
 * @param buttonText the text to display on the button
 * @param showDialogRegistration true if the dialog should be displayed, false otherwise
 * @param registrationState the state of the registration
 */
@Composable
fun RegisterButton(
    eventUIViewModel: EventUIViewModel,
    buttonEnabled: Boolean,
    buttonText: String,
    showDialogRegistration: Boolean?,
    registrationState: RegistrationState?,
    eventDao: EventDao?
) {
  Button(
      onClick = { eventUIViewModel.toggleRegistrationStatus(eventDao) },
      enabled = buttonEnabled,
      modifier = Modifier.fillMaxWidth().testTag("registerButton"),
      colors = ButtonDefaults.buttonColors(Color(0xFF3A89C9))) {
        Text(buttonText, color = Color.White)
      }

  if (showDialogRegistration!!) {
    AlertDialog(
        modifier = Modifier.testTag("alertBox"),
        onDismissRequest = { eventUIViewModel.dismissAlert() },
        title = { Text("Registration Result") },
        text = {
          when (registrationState) {
            is RegistrationState.Registered -> Text("You have been successfully registered!")
            is RegistrationState.Unregistered -> Text("You have been successfully unregistered!")
            else -> Text("Unknown state")
          }
        },
        confirmButton = {
          Button(
              modifier = Modifier.testTag("okButton"),
              onClick = { eventUIViewModel.dismissAlert() }) {
                Text("OK")
              }
        })
  }
}

/**
 * Chip displays a chip with the name of an interest.
 *
 * @param interest the interest to display
 */
@Composable
fun Chip(interest: Interests) {
  Surface(
      modifier = Modifier.padding(4.dp).testTag("chip"),
      elevation = 4.dp,
      shape = RoundedCornerShape(50), // Circular shaped corners
      color = Color(0xFF3A89C9) // Use the primary color from the theme
      ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          Text(text = interest.name, style = MaterialTheme.typography.body2, color = Color.White)
        }
      }
}

/**
 * ProfileIndicator displays info about the organizer of the event.
 *
 * @param profile the profile of the organizer
 * @param navActions the navigation actions
 * @param organizerRating the rating of the organizer
 */
@Composable
fun ProfileIndicator(profile: Profile?, navActions: NavigationActions, organizerRating: Double) {
  if (profile == null) return
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("profileIndicator")
              .clickable {
                // Navigate to the profile of the organizer
                if (profile.id != Firebase.auth.currentUser?.uid) {
                  navActions.controller.navigate("viewProfile/${profile.id}")
                } else {
                  navActions.controller.navigate("profile")
                }
              }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.size(40.dp) // Set the size of the circle
                    .background(
                        color = Color(0xFF9C27B0),
                        shape = CircleShape) // Set the background color and shape of the circle
            ) {
              Text(
                  text =
                      profile.userName.take(1).uppercase(), // Take the first character of the name
                  color = Color.White,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold)
            }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Hosted by", fontWeight = FontWeight.Light, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier.testTag("userName"),
            text = profile.userName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp)
        if (organizerRating > 0.0) {
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "whose rate is", fontWeight = FontWeight.Light, fontSize = 16.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "$organizerRating", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text(text = "/5.0", fontWeight = FontWeight.Light, fontSize = 16.sp)
        }
      }
}

/**
 * StarRating displays 5 stars, where the user can click on a star to rate the event from 1 to 5
 * stars.
 *
 * @param ownRating the current rating of the event
 * @param onRatingChanged the function to call when the user clicks on a star
 */
@Composable
fun StarRating(ownRating: Long, onRatingChanged: (Long) -> Unit) {
  Row(
      modifier = Modifier.testTag("starRow"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
          Icon(
              imageVector = if (i <= ownRating) Icons.Filled.Star else Icons.Outlined.Star,
              contentDescription = null,
              tint = if (i <= ownRating) Color(0xFFFFB907) else Color.Gray,
              modifier =
                  Modifier.size(40.dp)
                      .clickable { onRatingChanged(i.toLong()) }
                      .testTag("starIcon_$i"))
        }
      }
}

/**
 * ExportToCalendarIcon displays a clickable calendar icon that exports the event to the user's
 * calendar.
 *
 * @param event the event to save to the calendar
 */
@Composable
fun ExportToCalendarIcon(event: Event) {
  val context = LocalContext.current
  // Export to calendar button
  IconButton(
      onClick = {
        // Export to calendar
        context.startActivity(generateCalendarReminder(event))
      },
      modifier = Modifier.testTag("exportToCalendarButton")) {
        Icon(
            modifier = Modifier.size(24.dp).testTag("exportToCalendarIcon"),
            painter = rememberVectorPainter(image = Icons.Filled.DateRange),
            contentDescription = "Export to calendar")
      }
}

/**
 * RatingDisplay is a composable that displays a row with a text "Rate this event" and a StarRating
 * composable.
 *
 * @param ownRating the current rating of the event
 * @param eventUIViewModel the view model for the event UI
 */
@Composable
fun RatingDisplay(ownRating: Rating, eventUIViewModel: EventUIViewModel) {
  Column(
      modifier =
          Modifier.padding(vertical = 8.dp)
              .fillMaxWidth()
              .wrapContentHeight()
              .testTag("ratingColumn"),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Rate this event",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("rateText"))
        Spacer(modifier = Modifier.height(8.dp))
        StarRating(
            ownRating = Rating.toLong(ownRating),
            onRatingChanged = { eventUIViewModel.rateEvent(Rating.fromLong(it)) })
      }
}

/**
 * EventBody displays the body of the event, including the image, description, organizer,
 * categories, location, dates and times.
 *
 * @param event the event to display
 * @param organizerProfile the profile of the organizer
 * @param organizerRating the rating of the organizer
 * @param navActions the navigation actions
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.EventBody(
    event: Event,
    organizerProfile: Profile?,
    organizerRating: Double?,
    navActions: NavigationActions
) {

  BannerImageViewer(event.image, getEventImageHeader(event.categories), "event")

  Spacer(modifier = Modifier.height(16.dp))

  // Event Host
  ProfileIndicator(organizerProfile, navActions, organizerRating ?: 0.0)

  // Event Description
  event.description?.let { description ->
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        modifier = Modifier.testTag("eventDescription"),
        text = description,
        fontWeight = FontWeight(400),
        fontSize = 16.sp)
  }

  Spacer(modifier = Modifier.height(16.dp))

  Text(
      text = "Number of attendees",
      modifier = Modifier.align(Alignment.CenterHorizontally).testTag("attendeesInfoTitle"),
      fontWeight = FontWeight.Bold)

  // Event Capacity
  Row(modifier = Modifier.testTag("attendeesInfo")) {
    Text("Min: ", fontWeight = FontWeight.Bold)
    Text("${event.attendanceMinCapacity}")
    Spacer(modifier = Modifier.width(100.dp))
    Text("Current: ", fontWeight = FontWeight.Bold)
    Text(text = "${event.registeredUsers.size}")
    Spacer(modifier = Modifier.width(100.dp))
    Text("Max: ", fontWeight = FontWeight.Bold)
    Text(text = "${event.attendanceMaxCapacity}")
  }

  // Categories
  Spacer(modifier = Modifier.height(16.dp))
  FlowRow(modifier = Modifier.testTag("categoriesRow")) {
    event.categories?.forEach { interest -> Chip(interest = interest) }
  }

  // Map View Placeholder
  Spacer(modifier = Modifier.height(16.dp))
  Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(Color.Gray).testTag("mapView")) {
    // Here should be the code to integrate the actual map
    event.location?.let { location ->
      GeoMap(
          userCoordinates = location,
          interestsCoordinates = emptyList(),
          modifier = Modifier.fillMaxWidth().height(200.dp))
    } ?: BasicText(text = "No location provided for this event")
  }
  // Event Dates and Times
  Spacer(modifier = Modifier.height(16.dp))
  Row(modifier = Modifier.testTag("eventDatesTimes")) {
    Column {
      Text("Event Start:", fontWeight = FontWeight.Bold)
      event.eventStartDate?.let { startDate ->
        Text(startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
      }
      event.timeBeginning?.let { startTime ->
        Text(startTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
      }
    }
    Spacer(modifier = Modifier.width(16.dp))
    Column {
      Text("Event End:", fontWeight = FontWeight.Bold)
      event.eventEndDate?.let { endDate ->
        Text(endDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
      }
      event.timeEnding?.let { endTime ->
        Text(endTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
      }
    }
  }

  // Inscription Limit Date and Time
  Spacer(modifier = Modifier.height(8.dp))
  Text(
      "Inscription Limit:",
      fontWeight = FontWeight.Bold,
      modifier = Modifier.testTag("inscriptionLimitTitle"))
  Row(modifier = Modifier.testTag("inscriptionLimitDateAndTime")) {
    event.inscriptionLimitDate?.let { limitDate ->
      Text(limitDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    }
    Spacer(modifier = Modifier.width(8.dp))
    event.inscriptionLimitTime?.let { limitTime ->
      Text(limitTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
    }
  }
  EventQRCodeUI(event = event)
}
