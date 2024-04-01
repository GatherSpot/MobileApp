package com.github.se.gatherspot.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.EventViewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEventTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testIsEverythingHere() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    // Check if every element are displayed
    composeTestRule.onNodeWithTag("inputTitle").assertExists()
    composeTestRule.onNodeWithTag("inputDescription").assertExists()
    composeTestRule.onNodeWithTag("inputStartDateEvent").assertExists()
    composeTestRule.onNodeWithTag("inputEndDateEvent").assertExists()
    composeTestRule.onNodeWithTag("inputTimeStartEvent").assertExists()
    composeTestRule.onNodeWithTag("inputTimeEndEvent").assertExists()
    composeTestRule.onNodeWithTag("inputLocation").assertExists()
    composeTestRule.onNodeWithTag("inputMaxAttendees").assertExists()
    composeTestRule.onNodeWithTag("inputMinAttendees").assertExists()
    composeTestRule.onNodeWithTag("inputInscriptionLimitDate").assertExists()
    composeTestRule.onNodeWithTag("inputInscriptionLimitTime").assertExists()
    composeTestRule.onNodeWithTag("createEventButton").assertExists()
  }

  @Test
  fun testMinimalData() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    // Fill the form with minimal data
    composeTestRule.onNodeWithTag("inputTitle").performTextInput("Test Event")
    composeTestRule.onNodeWithTag("inputDescription").performTextInput("This is a test event")
    composeTestRule.onNodeWithTag("inputStartDateEvent").performTextInput("12/04/2026")
    composeTestRule.onNodeWithTag("inputTimeStartEvent").performTextInput("10:00")
    composeTestRule.onNodeWithTag("inputTimeEndEvent").performTextInput("12:00")
    // Check if the button is enabled
    composeTestRule.onNodeWithTag("createEventButton").assertIsEnabled()
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
}
