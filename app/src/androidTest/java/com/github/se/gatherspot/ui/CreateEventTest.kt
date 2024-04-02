package com.github.se.gatherspot.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.screens.CreateEventScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEventTest {
  @get:Rule val composeTestRule = createComposeRule()

  // Restructured to use CreateEventScreen
  @Test
  fun testIsEverythingHere() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Check if every element are displayed

      topBar { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      eventTitle { assertIsDisplayed() }
      eventDescription { assertIsDisplayed() }
      eventStartDate { assertIsDisplayed() }
      eventEndDate { assertIsDisplayed() }
      eventTimeStart { assertIsDisplayed() }
      eventTimeEnd { assertIsDisplayed() }
      eventLocation { assertIsDisplayed() }
      eventMaxAttendees { assertIsDisplayed() }
      eventMinAttendees { assertIsDisplayed() }
      eventInscriptionLimitDate { assertIsDisplayed() }
      eventInscriptionLimitTime { assertIsDisplayed() }
      eventSaveButton {
        assertIsDisplayed()
        assertIsNotEnabled()
      }

      // Check if the alert box is not displayed
      alertBox { assertDoesNotExist() }
    }
  }
  // Restructured to use CreateEventScreen
  @Test
  fun testMinimalData() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Check if the button is disabled
      eventSaveButton { assertIsNotEnabled() }
      // Fill the form with minimal data
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("12/04/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      // Check if the button is enabled
      eventSaveButton.assertIsEnabled()
    }
  }

  @Test
  fun testIfChangesAreSaved() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    // Fill every field
    composeTestRule.onNodeWithTag("inputTitle").performTextInput("Test Event")
    composeTestRule.onNodeWithTag("inputDescription").performTextInput("This is a test event")
    composeTestRule.onNodeWithTag("inputStartDateEvent").performTextInput("12/04/2026")
    composeTestRule.onNodeWithTag("inputEndDateEvent").performTextInput("12/05/2026")
    composeTestRule.onNodeWithTag("inputTimeStartEvent").performTextInput("10:00")
    composeTestRule.onNodeWithTag("inputTimeEndEvent").performTextInput("12:00")
    // Location is not finished yet
    composeTestRule.onNodeWithTag("inputMaxAttendees").performTextInput("100")
    composeTestRule.onNodeWithTag("inputMinAttendees").performTextInput("10")
    composeTestRule.onNodeWithTag("inputInscriptionLimitDate").performTextInput("10/04/2025")
    composeTestRule.onNodeWithTag("inputInscriptionLimitTime").performTextInput("09:00")

    // Check the content of the fields
    composeTestRule.onNodeWithTag("inputTitle").assert(hasText("Test Event"))
    composeTestRule.onNodeWithTag("inputDescription").assert(hasText("This is a test event"))
    composeTestRule.onNodeWithTag("inputStartDateEvent").assert(hasText("12/04/2026"))
    composeTestRule.onNodeWithTag("inputEndDateEvent").assert(hasText("12/05/2026"))
    composeTestRule.onNodeWithTag("inputTimeStartEvent").assert(hasText("10:00"))
    composeTestRule.onNodeWithTag("inputTimeEndEvent").assert(hasText("12:00"))
    composeTestRule.onNodeWithTag("inputMaxAttendees").assert(hasText("100"))
    composeTestRule.onNodeWithTag("inputMinAttendees").assert(hasText("10"))
    composeTestRule.onNodeWithTag("inputInscriptionLimitDate").assert(hasText("10/04/2025"))
    composeTestRule.onNodeWithTag("inputInscriptionLimitTime").assert(hasText("09:00"))
  }

  @Test
  fun testButtonIsOnlyEnabledAtTheRightMoment() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    // Fill every field
    composeTestRule.onNodeWithTag("inputTitle").performTextInput("Test Event")
    composeTestRule.onNodeWithTag("createEventButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("inputDescription").performTextInput("This is a test event")
    composeTestRule.onNodeWithTag("createEventButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("inputStartDateEvent").performTextInput("12/04/2026")
    composeTestRule.onNodeWithTag("createEventButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("inputEndDateEvent").performTextInput("12/05/2026")
    composeTestRule.onNodeWithTag("createEventButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("inputTimeStartEvent").performTextInput("10:00")
    composeTestRule.onNodeWithTag("createEventButton").assertIsNotEnabled()
    composeTestRule.onNodeWithTag("inputTimeEndEvent").performTextInput("12:00")
    // Location is not finished yet
    composeTestRule.onNodeWithTag("createEventButton").assertIsEnabled()
  }

  @Test
  fun testDialogBoxDisplayedWhenErrorsAreRaised() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    // Fill every field
    composeTestRule.onNodeWithTag("inputTitle").performTextInput("Test Event")
    composeTestRule.onNodeWithTag("inputDescription").performTextInput("This is a test event")
    composeTestRule.onNodeWithTag("inputStartDateEvent").performTextInput("12/04/2026")
    composeTestRule.onNodeWithTag("inputEndDateEvent").performTextInput("12/05/2026")
    composeTestRule.onNodeWithTag("inputTimeStartEvent").performTextInput("10:00")
    composeTestRule.onNodeWithTag("inputTimeEndEvent").performTextInput("12:00")
    // Location is not finished yet
    // False entry
    composeTestRule.onNodeWithTag("inputMaxAttendees").performTextInput("toto")
    // False entry
    composeTestRule.onNodeWithTag("inputMinAttendees").performTextInput("titi")
    composeTestRule.onNodeWithTag("inputInscriptionLimitDate").performTextInput("10/04/2025")
    composeTestRule.onNodeWithTag("inputInscriptionLimitTime").performTextInput("09:00")

    // Click on the button
    composeTestRule.onNodeWithTag("createEventButton").performClick()
    // add a delay to let the dialog box appear
    Thread.sleep(1000)
    // Check if the dialog box is displayed

    composeTestRule.onNodeWithTag("alertBox").assertExists()
    composeTestRule
        .onNodeWithTag("alertBox")
        .assertTextContains("Invalid max attendees format, must be a number")
    composeTestRule.onNodeWithTag("alertBox").assertExists()
    // Click the OK button
    composeTestRule.onNodeWithTag("alertBoxButton").performClick()
    // Check if the dialog box is closed
    composeTestRule.onNodeWithTag("alertBox").assertDoesNotExist()
  }
  // Does not work

  //    @Test
  //    fun testDialogBoxErrorDate() {
  //        composeTestRule.setContent {
  //            val navController = rememberNavController()
  //            val eventViewModel = EventViewModel()
  //
  //            CreateEvent(nav = NavigationActions(navController), eventViewModel)
  //        }
  //
  //        // Fill every field
  //        composeTestRule.onNodeWithTag("inputTitle").performTextInput("Test Event")
  //        composeTestRule.onNodeWithTag("inputDescription").performTextInput("This is a test
  // event")
  //        composeTestRule.onNodeWithTag("inputStartDateEvent").performTextInput("09/04/2026")
  //        composeTestRule.onNodeWithTag("inputEndDateEvent").performTextInput("12/05/2026")
  //        composeTestRule.onNodeWithTag("inputTimeStartEvent").performTextInput("10:00")
  //        composeTestRule.onNodeWithTag("inputTimeEndEvent").performTextInput("12:00")
  //        // Location is not finished yet
  //        composeTestRule.onNodeWithTag("inputMaxAttendees").performTextInput("100")
  //        composeTestRule.onNodeWithTag("inputMinAttendees").performTextInput("10")
  //        //False entry
  //
  // composeTestRule.onNodeWithTag("inputInscriptionLimitDate").performTextInput("10/04/2026")
  //        composeTestRule.onNodeWithTag("inputInscriptionLimitTime").performTextInput("09:00")
  //
  //        // Click on the button
  //        composeTestRule.onNodeWithTag("createEventButton").performClick()
  //        composeTestRule.onNodeWithTag("createEventButton").assertExists()
  //
  //        // Wait for the dialog box to appear
  //        //composeTestRule.waitForIdle()
  //
  //        // Check if the dialog box is displayed
  //        composeTestRule.onNodeWithTag("alertBox").assertExists()
  //
  //        // Click the OK button
  //        composeTestRule.onNodeWithTag("alertButton").performClick()
  //
  //        // Wait for the dialog box to disappear
  //        composeTestRule.waitForIdle()
  //
  //        // Check if the dialog box is closed
  //        composeTestRule.onNodeWithTag("alertBox").assertDoesNotExist()
  //    }

  @Test
  fun testLocationFieldEditable() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()
      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    // Check if the location field is enabled for text input
    composeTestRule.onNodeWithTag("inputLocation").assertIsEnabled()
  }
}
