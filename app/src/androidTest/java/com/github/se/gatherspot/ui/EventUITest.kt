package com.github.se.gatherspot.ui

import android.util.Log
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.EnvironmentSetter.Companion.profileFirebaseConnection
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventUIViewModel
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventUITest {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    runBlocking {
      testLogin()
      profileFirebaseConnection.add(Profile.testOrganizer())
      profileFirebaseConnection.add(Profile.testParticipant())
      profileFirebaseConnection.add(Profile("testLogin", "", "image", testLoginUID, setOf()))
      async { profileFirebaseConnection.fetch(Profile.testOrganizer().id) }.await()
      async { profileFirebaseConnection.fetch(testLoginUID) }.await()
    }
  }

  fun cleanUp() {
    testLoginCleanUp()
    runBlocking {
      ProfileFirebaseConnection().delete(testLoginUID)
      ProfileFirebaseConnection().delete(Profile.testParticipant().id)
      ProfileFirebaseConnection().delete(Profile.testOrganizer().id)
    }
  }

  private val pastEventRegisteredTo =
      Event(
          id = "1",
          title = "Event Title",
          description = "Hello: I am a description",
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 1,
          organizerID = Profile.testParticipant().id,
          categories = setOf(Interests.BASKETBALL),
          eventEndDate = LocalDate.of(2024, 4, 15),
          eventStartDate = LocalDate.of(2024, 4, 14),
          globalRating = null,
          inscriptionLimitDate = LocalDate.of(2024, 4, 11),
          inscriptionLimitTime = LocalTime.of(23, 59),
          location = null,
          registeredUsers = mutableListOf(testLoginUID),
          timeBeginning = LocalTime.of(13, 0),
          timeEnding = LocalTime.of(16, 0),
          image = "")

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testOrganizer().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.now().plusDays(5),
              eventStartDate = LocalDate.now().plusDays(4),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.now().plusDays(1),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      eventScaffold.assertExists()
      topBar.assertExists()
      backButton.assertExists()

      formColumn.assertExists()
      image.assertExists()
      profileIndicator.assertExists()
      description.assertExists()
      attendeesInfoTitle.assertExists()
      attendeesInfo.assertExists()
      categories.assertExists()
      mapView.assertExists()
      eventDatesTimes.assertExists()
      inscriptionLimitTitle.assertExists()
      inscriptionLimitDateAndTime.assertExists()
      registerButton.assertExists()
    }
  }

  @Test
  fun testEverythingIsDisplayed() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testOrganizer().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.now().plusDays(5),
              eventStartDate = LocalDate.now().plusDays(4),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.now().plusDays(1),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      eventScaffold.assertIsDisplayed()
      topBar.assertIsDisplayed()
      backButton.assertIsDisplayed()

      formColumn { assertIsDisplayed() }
      image {
        performScrollTo()
        assertIsDisplayed()
      }
      profileIndicator {
        performScrollTo()
        assertIsDisplayed()
      }
      description {
        performScrollTo()
        assertIsDisplayed()
      }
      attendeesInfoTitle {
        performScrollTo()
        assertIsDisplayed()
      }
      attendeesInfo {
        performScrollTo()
        assertIsDisplayed()
      }
      categories {
        performScrollTo()
        assertIsDisplayed()
      }
      mapView {
        performScrollTo()
        assertIsDisplayed()
      }
      eventDatesTimes {
        performScrollTo()
        assertIsDisplayed()
      }
      inscriptionLimitTitle {
        performScrollTo()
        assertIsDisplayed()
      }
      inscriptionLimitDateAndTime {
        performScrollTo()
        assertIsDisplayed()
      }
      registerButton {
        performScrollTo()
        assertIsDisplayed()
      }
      alertBox { assertIsNotDisplayed() }
    }
  }

  @Test
  fun textsDisplayedAreCorrect() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testOrganizer().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf(),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      description {
        assert(
            hasText(
                "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry."))
      }
      profileIndicator {
        hasText("Hosted by")
        hasText("Elias")
      }
      attendeesInfoTitle.assertTextEquals("Number of attendees")
      attendeesInfo {
        hasText("Min: ")
        hasText("Current: ")
        hasText("Max: ")
        hasText("100")
        hasText("10")
        hasText("0")
      }
      categories { hasText("Basketball") }
      eventDatesTimes {
        hasText("Event Start:")
        hasText("Event End:")
        hasText("Apr 14, 2024")
        hasText("Apr 15, 2024")
        hasText("1:00 PM")
        hasText("4:00 PM")
      }
      inscriptionLimitTitle.assertTextContains("Inscription Limit:")
      inscriptionLimitDateAndTime {
        hasText("Apr 11, 2024")
        hasText("11:59 PM")
      }
      registerButton { hasText("Register") }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun registerToAnEventWorks() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testParticipant().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf(),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        performScrollTo()
        assertIsEnabled()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)
      alertBox {
        assertIsDisplayed()
        hasText("You have been successfully registered!")
      }

      okButton {
        assertExists()
        performClick()
      }
      Thread.sleep(2000)
      registerButton {
        performScrollTo()
        assertIsNotEnabled()
        assert(hasText("Registered"))
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testUnableToRegisterToAFullEvent() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 2,
              attendanceMinCapacity = 1,
              organizerID = Profile.testParticipant().id,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("1", "2"),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        performScrollTo()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)
      alertBox {
        assertIsDisplayed()
        hasText("Event is full")
      }

      okButton.performClick()
      registerButton {
        performScrollTo()
        assertIsNotEnabled()
        assert(hasText("Full"))
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testAlreadyRegistered(): Unit = runBlocking {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = Profile.testParticipant().id,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf(FirebaseAuth.getInstance().currentUser!!.uid),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      val eventfirebase = EventFirebaseConnection()
      eventfirebase.add(event)
      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    Thread.sleep(3000)
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        performScrollTo()
        assertIsNotEnabled()
        assert(hasText("Registered"))
      }
    }
  }

  @Test
  fun testOrganiserDeleteEditButtonAreHere() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = testLoginUID,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("TEST"),
              timeBeginning = LocalTime.of(13, 0),
              globalRating = 4,
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      calendarButton { assertIsDisplayed() }
      editEventButton { assertIsDisplayed() }
      deleteButton { assertIsDisplayed() }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testClickOnDeleteButton() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = ProfileFirebaseConnection().getCurrentUserUid()!!,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("TEST"),
              timeBeginning = LocalTime.of(13, 0),
              globalRating = 4,
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      editEventButton { assertIsDisplayed() }
      deleteButton {
        assertIsDisplayed()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)
      alertBox {
        assertIsDisplayed()
        hasText("Are you sure you want to delete this event? This action cannot be undone.")
      }
      okButton {
        assertIsDisplayed()
        hasText("Delete")
      }
      cancelButton.performClick()
      alertBox { assertIsNotDisplayed() }
    }
  }

  @Test
  fun ratingIsDisplayed() {
    val eventUIViewModel = EventUIViewModel(pastEventRegisteredTo)
    composeTestRule.setContent {
      val navController = rememberNavController()
      EventUI(
          pastEventRegisteredTo,
          NavigationActions(navController),
          eventUIViewModel,
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      Log.e("isOrganizer", eventUIViewModel.isOrganizer().toString())
      Log.e(
          "In the list",
          pastEventRegisteredTo.registeredUsers
              .contains(FirebaseAuth.getInstance().currentUser!!.uid)
              .toString())
      Log.e("isEventOver", EventUtils().isEventOver(pastEventRegisteredTo).toString())
      assert(eventUIViewModel.canRate())
      starRow {
        performScrollTo()
        assertIsDisplayed()
      }
      star {
        performScrollTo()
        assertIsDisplayed()
      }
    }
  }

  @Test
  fun testProfileIsCorrectlyFetched() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = Profile.testOrganizer().id,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("TEST"),
              timeBeginning = LocalTime.of(13, 0),
              globalRating = 4,
              timeEnding = LocalTime.of(16, 0),
              image = "EventUITestImage")

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      profileIndicator.assertIsDisplayed()
      userName { hasText("John Doe") }
      // profileIndicator.performClick()
    }
  }
}
