package com.github.se.gatherspot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.model.event.RegistrationState
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventUI(
    event: Event,
    navActions: NavigationActions,
    registrationViewModel: EventRegistrationViewModel,
    eventsViewModel: EventsViewModel
) {

  val showDialogRegistration by registrationViewModel.displayAlertRegistration.observeAsState()
  val showDialogDelete by registrationViewModel.displayAlertDeletion.observeAsState()
  val isOrganizer =
      event.organizerID == (Firebase.auth.currentUser?.uid ?: Profile.testOrganizer().id)
  val eventUtils = EventUtils()
  val registrationState by registrationViewModel.registrationState.observeAsState()
  val isButtonEnabled = registrationState == RegistrationState.NoError
  val buttonText =
      when (registrationState) {
        is RegistrationState.Success -> "Registered"
        is RegistrationState.Error ->
            if ((registrationState as RegistrationState.Error).message == "Event is full") "Full"
            else "Registered"
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
                  onClick = { navActions.goBack() }, modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to overview")
                  }
            },
            actions = {
              if (isOrganizer) {
                // Edit button
                IconButton(
                    onClick = {
                      val eventJsonWellFormed = event.toJson()
                      navActions.controller.navigate("editEvent/$eventJsonWellFormed")
                    },
                    modifier = Modifier.testTag("editEventButton")) {
                      Icon(
                          modifier = Modifier.size(24.dp).testTag("editEventIcon"),
                          painter = painterResource(id = R.drawable.edit),
                          contentDescription = "Edit event")
                    }
                // Delete button
                IconButton(
                    onClick = { registrationViewModel.clickDeleteButton() },
                    modifier = Modifier.testTag("deleteEventButton")) {
                      Icon(
                          modifier = Modifier.size(24.dp).testTag("deleteEventIcon"),
                          painter = painterResource(id = R.drawable.delete),
                          contentDescription = "Delete event")
                    }
              }
            })
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .padding(8.dp)
                    .testTag("eventColumn")
                    .verticalScroll(rememberScrollState())) {
              // Event Image
              event.images?.let { img ->
                Image(
                    bitmap = img,
                    contentDescription = "Event Image",
                    modifier = Modifier.fillMaxWidth().height(150.dp).testTag("eventImage"),
                    contentScale = ContentScale.FillBounds)
              }
                  ?: Image(
                      painter = painterResource(id = R.drawable.default_event_image),
                      contentDescription = "Default Event Image",
                      modifier = Modifier.fillMaxWidth().height(150.dp).testTag("eventImage"),
                      contentScale = ContentScale.FillBounds)

              Spacer(modifier = Modifier.height(16.dp))

              // Event Host
              var profile = Profile.testParticipant()

              /*
              runBlocking {
              profile = async{ProfileFirebaseConnection().fetch(event.organizerID)}.await()
                      ?: Profile.testParticipant()
              }

               */
              ProfileIndicator(profile)

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
                  modifier =
                      Modifier.align(Alignment.CenterHorizontally).testTag("attendeesInfoTitle"),
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
              Box(
                  modifier =
                      Modifier.height(200.dp)
                          .fillMaxWidth()
                          .background(Color.Gray)
                          .testTag("mapView")) {
                    // Here should be the code to integrate the actual map
                    event.location?.let { location ->
                      GeoMap(
                          userCoordinates = location,
                          interestsCoordinates = emptyList(),
                          mapViewModifier = Modifier.fillMaxWidth().height(200.dp))
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

              // Registration Button
              Spacer(modifier = Modifier.height(16.dp))

              if (!isOrganizer) {
                Button(
                    onClick = {
                      registrationViewModel.registerForEvent(event)
                      eventsViewModel.updateNewRegistered(event)
                      registrationViewModel.clickRegisterButton()
                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth().testTag("registerButton"),
                    colors = ButtonDefaults.buttonColors(Color(0xFF3A89C9))) {
                      Text(buttonText, color = Color.White)
                    }
              }
            }

        if (showDialogRegistration!!) {
          AlertDialog(
              modifier = Modifier.testTag("alertBox"),
              onDismissRequest = { registrationViewModel.dismissAlert() },
              title = { Text("Registration Result") },
              text = {
                when (val state = registrationState) {
                  is RegistrationState.Success -> Text("You have been successfully registered!")
                  is RegistrationState.Error -> Text(state.message)
                  else -> Text("Unknown state")
                }
              },
              confirmButton = {
                Button(
                    modifier = Modifier.testTag("okButton"),
                    onClick = { registrationViewModel.dismissAlert() }) {
                      Text("OK")
                    }
              })
        }

        if (showDialogDelete!!) {
          AlertDialog(
              modifier = Modifier.testTag("alertBox"),
              onDismissRequest = { registrationViewModel.dismissAlert() },
              title = { Text("Delete Event") },
              text = {
                Text("Are you sure you want to delete this event? This action cannot be undone.")
              },
              confirmButton = {
                Button(
                    modifier = Modifier.testTag("okButton"),
                    onClick = {
                      // Delete the event
                      eventUtils.deleteEvent(event)
                      navActions.goBack()
                      registrationViewModel.dismissAlert()
                    }) {
                      Text("Delete")
                    }
              },
              dismissButton = {
                Button(
                    modifier = Modifier.testTag("cancelButton"),
                    onClick = { registrationViewModel.dismissAlert() }) {
                      Text("Cancel")
                    }
              })
        }
      }
}

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

@Composable
fun ProfileIndicator(profile: Profile) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.padding(horizontal = 16.dp, vertical = 8.dp).testTag("profileIndicator")) {
        // TODO implement image here: do it later
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
        Text(text = profile.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
}

/*
// Preview for the Event UI, for testing purposes
@Preview
@Composable
fun EventUIPreview() {
  // Set global uid for testing
  val event =
      Event(
          id = "idTestEvent",
          title = "Event Title",
          description =
              "Hello: I am a description of the event just saying that I would love to say" +
                  "that Messi is not the best player in the world, but I can't. I am sorry.",
          attendanceMaxCapacity = 5,
          attendanceMinCapacity = 1,
          categories = setOf(Interests.BASKETBALL),
          eventEndDate = LocalDate.of(2025, 4, 15),
          eventStartDate = LocalDate.of(2025, 4, 10),
          globalRating = 4,
          inscriptionLimitDate = LocalDate.of(2025, 4, 1),
          inscriptionLimitTime = LocalTime.of(23, 59),
          location = Location(46.51878838760822, 6.5619011030383, "IC BC"),
          registeredUsers = mutableListOf(),
          timeBeginning = LocalTime.of(11, 0),
          timeEnding = LocalTime.of(13, 0),
          organizerID = Profile.testOrganizer().id)
  val viewModel = EventRegistrationViewModel(listOf(""))
  EventUI(
      event = event,
      navActions = NavigationActions(rememberNavController()),
      registrationViewModel = viewModel,
      eventsViewModel = EventsViewModel())
}

 */
