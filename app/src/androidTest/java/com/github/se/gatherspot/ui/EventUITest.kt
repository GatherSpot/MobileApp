package com.github.se.gatherspot.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventUITest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              eventID = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizer = com.github.se.gatherspot.model.Profile.dummyProfile(),
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.now().plusDays(5),
              eventStartDate = LocalDate.now().plusDays(4),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.now().plusDays(1),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = null,
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
          )
      EventUI(event, NavigationActions(navController))
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
              eventID = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizer = com.github.se.gatherspot.model.Profile.dummyProfile(),
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.now().plusDays(5),
              eventStartDate = LocalDate.now().plusDays(4),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.now().plusDays(1),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = null,
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
          )
      EventUI(event, NavigationActions(navController))
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
    }
  }

  @Test
  fun textsDisplayedAreCorrect() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              eventID = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizer = com.github.se.gatherspot.model.Profile.dummyProfile(),
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = emptyList(),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
          )
      EventUI(event, NavigationActions(navController))
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
}
