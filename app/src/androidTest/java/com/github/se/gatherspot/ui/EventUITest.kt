package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class EventUITest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    // To make it works, need to define a global MainActivity.uid
    MainActivity.uid = "test"
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description =
          "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
          organizer =
          Profile(
            "Elias",
            "Bio",
            "image",
            "uid",
            setOf(
              Interests.BASKETBALL,
              Interests.FOOTBALL,
              Interests.BOWLING,
              Interests.CHESS
            )
          ),
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
        )
      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
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
    // To make it works in isolation, need to define a global MainActivity.uid
    MainActivity.uid = "test"
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description =
          "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
          organizer =
          Profile(
            "Elias",
            "Bio",
            "image",
            "uid",
            setOf(
              Interests.BASKETBALL,
              Interests.FOOTBALL,
              Interests.BOWLING,
              Interests.CHESS
            )
          ),
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
        )
      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
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
    // To make it works, need to define a global MainActivity.uid
    MainActivity.uid = "test"
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description =
          "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
          organizer =
          Profile(
            "Elias",
            "Bio",
            "image",
            "uid",
            setOf(
              Interests.BASKETBALL,
              Interests.FOOTBALL,
              Interests.BOWLING,
              Interests.CHESS
            )
          ),
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
        )
      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      description {
        assert(
          hasText(
            "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry."
          )
        )
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
    // To make it works, need to define a global MainActivity.uid
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description =
          "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
          organizer =
          Profile.testParticipant(),
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
        )
      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
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
    // To make it works, need to define a global MainActivity.uid
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description = "Hello: I am a description",
          attendanceMaxCapacity = 2,
          attendanceMinCapacity = 1,
          organizer = Profile.testParticipant(),
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
        )

      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
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
  fun testAlreadyRegistered() {
    // To make it works, need to define a global MainActivity.uid
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description = "Hello: I am a description",
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 1,
          organizer = Profile.testParticipant(),
          categories = setOf(Interests.BASKETBALL),
          eventEndDate = LocalDate.of(2024, 4, 15),
          eventStartDate = LocalDate.of(2024, 4, 14),
          globalRating = 4,
          inscriptionLimitDate = LocalDate.of(2024, 4, 11),
          inscriptionLimitTime = LocalTime.of(23, 59),
          location = null,
          registeredUsers = mutableListOf("TEST"),
          timeBeginning = LocalTime.of(13, 0),
          timeEnding = LocalTime.of(16, 0),
        )

      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        performScrollTo()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)
      alertBox {
        assertIsDisplayed()
        hasText("Already registered for this event")
      }

      okButton.performClick()
      registerButton {
        performScrollTo()
        assertIsNotEnabled()
        assert(hasText("Registered"))
      }
    }
  }

  @Test
  fun testOrganiserDeleteEditButtonAreHere() {
    // To make it works, need to define a global MainActivity.uid
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description = "Hello: I am a description",
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 1,
          organizer = Profile.testOrganizer(),
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
        )

      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {}
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testClickOnDeleteButton() {
    // To make it works, need to define a global MainActivity.uid
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
        Event(
          id = "1",
          title = "Event Title",
          description = "Hello: I am a description",
          attendanceMaxCapacity = 10,
          attendanceMinCapacity = 1,
          organizer = Profile.testOrganizer(),
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
        )

      EventUI(event, NavigationActions(navController), EventRegistrationViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      editButton { assertIsDisplayed() }
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
}
