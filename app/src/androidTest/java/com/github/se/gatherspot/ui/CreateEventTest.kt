package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EventFirebaseConnection
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
  fun testIsEverythingExist() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Check if every element are displayed
      eventScaffold { assertExists() }
      topBar { assertExists() }
      backButton { assertExists() }
      formColumn { assertExists() }
      eventTitle { assertExists() }
      eventDescription { assertExists() }
      eventStartDate { assertExists() }
      eventEndDate { assertExists() }
      eventTimeStart { assertExists() }
      eventTimeEnd { assertExists() }
      eventLocation { assertExists() }
      eventMaxAttendees { assertExists() }
      eventMinAttendees { assertExists() }
      eventInscriptionLimitDate { assertExists() }
      eventInscriptionLimitTime { assertExists() }
      eventSaveButton { assertExists() }

      alertBox { assertDoesNotExist() }
      alertBoxText { assertDoesNotExist() }
      alertBoxButton { assertDoesNotExist() }
    }
  }

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
      eventLocation {
        performScrollTo()
        assertIsDisplayed()
      }
      eventMaxAttendees {
        performScrollTo()
        assertIsDisplayed()
      }
      eventMinAttendees {
        performScrollTo()
        assertIsDisplayed()
      }

      // scroll the screen to see the rest of the fields
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

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Fill every field
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("12/04/2026")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      // eventLocation.performTextInput("Test Location")
      eventMaxAttendees.performTextInput("100")
      eventMinAttendees.performTextInput("10")
      eventInscriptionLimitDate.performTextInput("10/04/2025")
      eventInscriptionLimitTime.performTextInput("09:00")
      // Check the content of the fields
      eventTitle.assert(hasText("Test Event"))
      eventDescription.assert(hasText("This is a test event"))
      eventStartDate.assert(hasText("12/04/2026"))
      eventEndDate.assert(hasText("12/05/2026"))
      eventTimeStart.assert(hasText("10:00"))
      eventTimeEnd.assert(hasText("12:00"))
      // eventLocation.assert(hasText("Test Location"))
      eventMaxAttendees.assert(hasText("100"))
      eventMinAttendees.assert(hasText("10"))
      eventInscriptionLimitDate.assert(hasText("10/04/2025"))
      eventInscriptionLimitTime.assert(hasText("09:00"))
    }
  }

  @Test
  fun testButtonIsOnlyEnabledAtTheRightMoment() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Check if the button is disabled
      eventSaveButton.assertIsNotEnabled()
      // Fill every field
      eventTitle.performTextInput("Test Event")
      eventSaveButton.assertIsNotEnabled()
      eventDescription.performTextInput("This is a test event")
      eventSaveButton.assertIsNotEnabled()
      eventStartDate.performTextInput("12/04/2026")
      eventSaveButton.assertIsNotEnabled()
      eventEndDate.performTextInput("12/05/2026")
      eventSaveButton.assertIsNotEnabled()
      eventTimeStart.performTextInput("10:00")
      eventSaveButton.assertIsNotEnabled()
      eventTimeEnd.performTextInput("12:00")

      eventSaveButton.assertIsEnabled()

      eventLocation.performTextInput("Test Location")
      eventMaxAttendees.performTextInput("100")
      eventMinAttendees.performTextInput("10")
      eventInscriptionLimitDate.performTextInput("10/04/2025")
      eventInscriptionLimitTime.performTextInput("09:00")
      // Check if the button is enabled
      eventSaveButton.assertIsEnabled()
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testDialogBoxDisplayedWhenErrorsAreRaised() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Fill every field
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("12/04/2026")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      // TODO: This for when we can implement the locations dropwdown
      // eventLocation.performTextInput("Test Location")
      eventMaxAttendees.performTextInput("toto")
      eventMinAttendees.performTextInput("titi")
      eventInscriptionLimitDate.performTextInput("10/04/2025")
      eventInscriptionLimitTime.performTextInput("09:00")

      // Click on the button
      eventSaveButton {
        performScrollTo()
        assertIsDisplayed()
        performClick()
      }
      // Check if the dialog box is displayed
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)

      alertBox.assertIsDisplayed()
      alertBoxText.assertTextContains("Invalid max attendees format, must be a number")
      // Click the OK button
      alertBoxButton.performClick()
      // Check if the dialog box is closed
      alertBox.assertDoesNotExist()
    }
  }
  // Does not work

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testDialogBoxErrorDate() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()

      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Fill every field
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("09/04/2025")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      // eventLocation.performTextInput("Test Location")
      eventMaxAttendees.performTextInput("100")
      eventMinAttendees.performTextInput("10")
      eventInscriptionLimitDate.performTextInput("10/04/2025")
      eventInscriptionLimitTime.performTextInput("09:00")

      // Click on the button
      eventSaveButton {
        performScrollTo()
        assertIsDisplayed()
        performClick()
      }
      // Check if the dialog box is displayed
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)

      alertBox.assertIsDisplayed()
      alertBoxText.assertTextContains("Inscription limit date must be before event start date")
      // Click the OK button
      alertBoxButton.performClick()
      // Check if the dialog box is closed
      alertBox.assertDoesNotExist()
    }
  }

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

  @Test
  fun testVerifyLabelContent() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()
      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Check if the labels are displayed
      eventTitle.assert(hasText("Event Title*"))
      eventDescription.assert(hasText("Description*"))
      eventStartDate.assert(hasText("Start Date of the event*"))
      eventEndDate.assert(hasText("End date of the event"))
      eventTimeStart.assert(hasText("Start time*"))
      eventTimeEnd.assert(hasText("End time*"))
      // eventLocation.assert(hasText("Event Location"))
      eventMaxAttendees.assert(hasText("Max Attendees"))
      eventMinAttendees.assert(hasText("Min Attendees"))
      eventInscriptionLimitDate.assert(hasText("Inscription Limit Date"))
      eventInscriptionLimitTime.assert(hasText("Inscription Limit Time"))
    }
  }

  @Test
  fun verifyPlaceHolderAndLabel() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventViewModel = EventViewModel()
      CreateEvent(nav = NavigationActions(navController), eventViewModel)
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      // Check if the labels are displayed
      eventTitle.assert(hasText("Event Title*"))
      eventDescription.assert(hasText("Description*"))
      eventStartDate.assert(hasText("Start Date of the event*"))
      eventEndDate.assert(hasText("End date of the event"))
      eventTimeStart.assert(hasText("Start time*"))
      eventTimeEnd.assert(hasText("End time*"))
      eventLocation.assert(hasText("Location"))
      eventMaxAttendees.assert(hasText("Max Attendees"))
      eventMinAttendees.assert(hasText("Min Attendees"))
      eventInscriptionLimitDate.assert(hasText("Inscription Limit Date"))
      eventInscriptionLimitTime.assert(hasText("Inscription Limit Time"))

      // Check if the placeholders are displayed when clicked
      eventTitle {
        performClick()
        assert(hasText("Give a name to the event"))
      }
      eventDescription {
        performClick()
        assert(hasText("Describe the event"))
      }
      eventStartDate {
        performClick()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT))
      }
      eventEndDate {
        performClick()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT))
      }
      eventTimeStart {
        performClick()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
      eventTimeEnd {
        performClick()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
      // todo: LOCATION not handled yet
      /*
      eventLocation{
        performClick()
        assert(hasText("Enter an address"))
      }*/
      eventMaxAttendees {
        performClick()
        assert(hasText("Max Attendees"))
      }
      eventMinAttendees {
        performClick()
        assert(hasText("Min Attendees"))
      }
      eventInscriptionLimitDate {
        performScrollTo()
        performClick()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT))
      }
      eventInscriptionLimitTime {
        performScrollTo()
        performClick()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
    }
  }
}
