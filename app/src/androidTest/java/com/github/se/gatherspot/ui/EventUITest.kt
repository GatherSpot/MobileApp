package com.github.se.gatherspot.ui

import android.util.Log
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.defaults.DefaultProfiles
import com.github.se.gatherspot.EnvironmentSetter.Companion.profileFirebaseConnection
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.event.EventUIViewModel
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.utils.MockEventFirebaseConnection
import com.github.se.gatherspot.utils.MockIdListFirebaseConnection
import com.github.se.gatherspot.utils.MockProfileFirebaseConnection
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
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
      description { assert(hasText(DefaultEvents.trivialEvent1.description!!)) }
      profileIndicator {
        hasText("Hosted by")
        hasText(profile.id)
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
      val event = trivialEvent
      EventUI(
          event,
          NavigationActions(navController),
          EventUIViewModel(
              event,
              MockProfileFirebaseConnection(),
              MockEventFirebaseConnection(),
              MockIdListFirebaseConnection()),
          MockProfileFirebaseConnection())
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
      val event = DefaultEvents.fullEvent

      EventUI(
          event,
          NavigationActions(navController),
          EventUIViewModel(
              event,
              MockProfileFirebaseConnection(),
              MockEventFirebaseConnection(),
              MockIdListFirebaseConnection()),
          MockProfileFirebaseConnection())
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

  @Test
  fun testAlreadyRegistered() {
    val event = DefaultEvents.withRegistered("MC", eventId = "2")
    runBlocking { EventFirebaseConnection().add(event) }
    composeTestRule.setContent {
      val navController = rememberNavController()

      EventUI(
          event,
          NavigationActions(navController),
          EventUIViewModel(
              event,
              MockProfileFirebaseConnection(),
              MockEventFirebaseConnection(),
              MockIdListFirebaseConnection()),
          MockProfileFirebaseConnection())
    }
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
      EventUI(
          event,
          NavigationActions(navController),
          EventUIViewModel(
              event,
              MockProfileFirebaseConnection(),
              MockEventFirebaseConnection(),
              MockIdListFirebaseConnection()),
          MockProfileFirebaseConnection())
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
      val event = ownEvent

      EventUI(
          event,
          NavigationActions(navController),
          EventUIViewModel(
              event,
              MockProfileFirebaseConnection(),
              MockEventFirebaseConnection(),
              MockIdListFirebaseConnection()),
          MockProfileFirebaseConnection())
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

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun ratingIsDisplayed() {
    val event = DefaultEvents.pastEventRegistered
    val eventUIViewModel =
        EventUIViewModel(
            event,
            MockProfileFirebaseConnection(),
            MockEventFirebaseConnection(),
            MockIdListFirebaseConnection())
    composeTestRule.setContent {
      val navController = rememberNavController()
      EventUI(
          event,
          NavigationActions(navController),
          eventUIViewModel,
          MockProfileFirebaseConnection())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      val tag = "eventUiTest"
      Log.e(tag, eventUIViewModel.isOrganizer().toString())
      Log.e(tag, event.registeredUsers.contains("MC").toString())
      Log.e(tag, EventUtils().isEventOver(event).toString())
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

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testProfileIsCorrectlyFetched() {
    val event = DefaultEvents.withAuthor(DefaultProfiles.trivial.id, "1")
    composeTestRule.setContent {
      val navController = rememberNavController()

      EventUI(
          event,
          NavigationActions(navController),
          EventUIViewModel(
              event,
              MockProfileFirebaseConnection(),
              MockEventFirebaseConnection(),
              MockIdListFirebaseConnection()),
          MockProfileFirebaseConnection())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("profileIndicator"), 6000)
      userName { hasText("John Doe") }
      // profileIndicator.performClick()
    }
  }
}
