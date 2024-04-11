package com.github.se.gatherspot.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.screens.EventDataFormScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditEventTest {
  private val testEvent =
      Event(
          eventID = "testID",
          title = "Test Event",
          description = "This is a test event",
          location = Location(0.0, 0.0, "Test Location"),
          eventStartDate =
              LocalDate.parse(
                  "12/04/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          eventEndDate =
              LocalDate.parse(
                  "12/05/2026", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          timeBeginning =
              LocalTime.parse(
                  "10:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
          timeEnding =
              LocalTime.parse(
                  "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
          attendanceMaxCapacity = 100,
          attendanceMinCapacity = 10,
          inscriptionLimitDate =
              LocalDate.parse(
                  "10/04/2025", DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT)),
          inscriptionLimitTime =
              LocalTime.parse(
                  "12:00", DateTimeFormatter.ofPattern(EventFirebaseConnection.TIME_FORMAT)),
          eventStatus = EventStatus.CREATED,
          globalRating = null)
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEditEveryFieldDisplayed() {
    composeTestRule.setContent {
      val navController = rememberNavController()

      EditEvent(
          nav = NavigationActions(navController), eventUtils = EventUtils(), event = testEvent)
    }

    // Check that every field is displayed
    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventTitle.assertIsDisplayed()
      eventDescription.assertIsDisplayed()
      eventStartDate {
        performScrollTo()
        assertIsDisplayed()
      }
      eventEndDate {
        performScrollTo()
        assertIsDisplayed()
      }
      eventTimeStart {
        performScrollTo()
        assertIsDisplayed()
      }
      eventTimeEnd {
        performScrollTo()
        assertIsDisplayed()
      }
      dropDownCategoriesBox {
        performClick()
        assertIsDisplayed()
      }
      dropDownCategories { assertIsDisplayed() }
      eventMinAttendees {
        performScrollTo()
        assertIsDisplayed()
      }
      eventMaxAttendees.assertIsDisplayed()

      eventLocation {
        performScrollTo()
        assertIsDisplayed()
      }
      eventInscriptionLimitDate {
        performScrollTo()
        assertIsDisplayed()
      }
      eventInscriptionLimitTime {
        performScrollTo()
        assertIsDisplayed()
      }
      eventSaveButton {
        performScrollTo()
        assertIsDisplayed()
      }
    }
  }

  @Test
  fun testEditEventFieldsAreCorrects() {
    composeTestRule.setContent {
      val navController = rememberNavController()

      EditEvent(
          nav = NavigationActions(navController), eventUtils = EventUtils(), event = testEvent)
    }

    // Check that every field is displayed
    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventTitle.assert(hasText("Test Event"))
      eventDescription.assert(hasText("This is a test event"))
      eventStartDate.assert(hasText("12/04/2026"))
      eventEndDate.assert(hasText("12/05/2026"))
      eventTimeStart.assert(hasText("10:00"))
      eventTimeEnd.assert(hasText("12:00"))
      dropDownCategoriesBox {
        performScrollTo()
        performClick()
        assertIsDisplayed()
      }
      dropDownCategories.assertIsDisplayed()
      eventMinAttendees {
        performScrollTo()
        assert(hasText("10"))
      }
      eventMaxAttendees.assert(hasText("100"))

      eventLocation {
        performScrollTo()
        assert(hasText("Location"))
      }
      eventInscriptionLimitDate {
        performScrollTo()
        assert(hasText("10/04/2025"))
      }
      eventInscriptionLimitTime {
        performScrollTo()
        assert(hasText("12:00"))
      }
      eventSaveButton {
        performScrollTo()
        assertIsDisplayed()
      }
    }
  }

  @Test
  fun testEditEventFieldsAreEditable() {
    composeTestRule.setContent {
      val navController = rememberNavController()

      EditEvent(
          nav = NavigationActions(navController), eventUtils = EventUtils(), event = testEvent)
    }

    // Check that every field is displayed
    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventTitle {
        performTextInput("")
        performTextInput("New Test Event")
      }
      eventDescription {
        performTextInput("")
        performTextInput("This is a new test event")
      }
      eventStartDate {
        performTextInput("")
        performTextInput("12/04/2027")
      }
      eventEndDate {
        performTextInput("")
        performTextInput("12/05/2027")
      }
      eventTimeStart {
        performTextInput("")
        performTextInput("11:00")
      }
      eventTimeEnd {
        performTextInput("")
        performTextInput("13:00")
      }

      dropDownCategoriesBox {
        performScrollTo()
        performClick()
        assertIsDisplayed()
      }
      dropDownCategories.assertIsDisplayed()
      eventMinAttendees {
        performScrollTo()
        performTextInput("20")
      }
      eventMaxAttendees.performTextInput("200")

      eventLocation {
        performScrollTo()
        performTextInput("")
        performTextInput("ecole polytechnique fedreale")
      }
      eventInscriptionLimitDate {
        performScrollTo()
        performTextInput("")
        performTextInput("10/04/2026")
      }
      eventInscriptionLimitTime {
        performScrollTo()
        performTextInput("")
        performTextInput("13:00")
      }
      eventSaveButton {
        performScrollTo()
        assertIsDisplayed()
        performClick()
      }
    }
  }
}
