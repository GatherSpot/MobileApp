package com.github.se.gatherspot.ui

import android.Manifest
import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.screens.EventDataFormScreen
import com.github.se.gatherspot.ui.eventUI.CreateEvent
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.EventsViewModel
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEventTest {
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun grantLocationPermission() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val packageName = context.packageName
    val uiAutomation =
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation
    uiAutomation.executeShellCommand(
        "pm grant $packageName android.permission.ACCESS_FINE_LOCATION")
  }

  private val eventFirebaseConnection = EventFirebaseConnection()

  @get:Rule val composeTestRule = createComposeRule()

  @Before fun setUp() = runBlocking { testLogin() }

  @After fun cleanUp() = runBlocking { testLoginCleanUp() }

  // Restructured to use EventDataFormScreen
  @Test
  fun testIsEverythingExist() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()

      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      // Check if every element are displayed
      eventScaffold { assertExists() }
      topBar { assertExists() }
      backButton { assertExists() }
      clearButton { assertExists() }
      formColumn { assertExists() }
      eventTitle {
        assertExists()
        // Label exists
        assert(hasText("Event Title*"))
        performClick()
        // Placeholder exists
        assert(hasText("Give a name to the event"))
      }
      eventDescription {
        assertExists()
        assert(hasText("Description*"))
        performClick()
        composeTestRule.waitForIdle()
        assert(hasText("Describe the event"))
      }
      eventStartDate {
        performScrollTo()
        assertExists()
        assert(hasText("Start Date of the event*"))
        performClick()
        composeTestRule.waitForIdle()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
      }
      eventEndDate {
        performScrollTo()
        assertExists()
        assert(hasText("End date of the event"))
        performClick()
        composeTestRule.waitForIdle()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
      }
      eventTimeStart {
        performScrollTo()
        assertExists()
        assert(hasText("Start time*"))
        performClick()
        composeTestRule.waitForIdle()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
      eventTimeEnd {
        performScrollTo()
        assertExists()
        assert(hasText("End time*"))
        performClick()
        composeTestRule.waitForIdle()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
      eventMinAttendees {
        performScrollTo()
        assertExists()
        assert(hasText("Min Attendees"))
        performClick()
        assert(hasText("Min Attendees"))
      }
      eventMaxAttendees {
        performScrollTo()
        assertExists()
        assert(hasText("Max Attendees"))
        performClick()
        assert(hasText("Max Attendees"))
      }
      eventLocation {
        performScrollTo()
        assertExists()
        performScrollTo()
        assert(hasText("Location"))
        performClick()
        assert(hasText("Enter an address"))
      }
      Espresso.closeSoftKeyboard()
      eventInscriptionLimitDate {
        assertExists()
        performScrollTo()
        assert(hasText("Inscription Limit Date"))
        performClick()
        composeTestRule.waitForIdle()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
      }
      Espresso.closeSoftKeyboard()
      eventInscriptionLimitTime {
        assertExists()
        performScrollTo()
        assert(hasText("Inscription Limit Time"))
        performClick()
        composeTestRule.waitForIdle()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
      eventSaveButton {
        performScrollTo()
        assertExists()
      }

      alertBox { assertDoesNotExist() }
      alertBoxText { assertDoesNotExist() }
      alertBoxButton { assertDoesNotExist() }
    }
  }

  @Test
  fun testIsEverythingHere() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()

      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      // Check if every element are displayed
      eventScaffold { assertIsDisplayed() }
      topBar { assertIsDisplayed() }
      backButton { assertIsDisplayed() }
      clearButton { assertIsDisplayed() }
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
      eventMinAttendees {
        performScrollTo()
        assertIsDisplayed()
      }
      eventMaxAttendees {
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

  // Restructured to use EventDataFormScreen
  @Test
  fun testMinimalData() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()

      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
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
      val eventUtils = EventUtils()

      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      // Fill every field
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("12/04/2026")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      eventLocation.performTextInput("Test Location")
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
      eventLocation.assert(hasText("Test Location"))
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
      val eventUtils = EventUtils()

      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
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
      val eventUtils = EventUtils()

      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      // Fill every field
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("12/04/2026")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      eventLocation.performTextInput("Test Location")
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
      // alertBoxText.assertIsDisplayed()
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
      val eventUtils = EventUtils()

      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      // Fill every field
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("09/04/2025")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      eventLocation.performTextInput("Test Location")
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
      val eventUtils = EventUtils()
      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    // Check if the location field is enabled for text input
    composeTestRule.onNodeWithTag("inputLocation").assertIsEnabled()
  }

  @Test
  fun verifyPlaceHolderAndLabel() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()
      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
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
        composeTestRule.onNodeWithText("Event Title*").assertIsDisplayed()
        performClick()
        assert(hasText("Give a name to the event"))
        composeTestRule.onNodeWithText("Give a name to the event").assertIsDisplayed()
      }
      eventDescription {
        composeTestRule.onNodeWithText("Description*").assertIsDisplayed()
        performClick()
        assert(hasText("Describe the event"))
        composeTestRule.onNodeWithText("Describe the event").assertIsDisplayed()
      }
      eventStartDate {
        performScrollTo()
        composeTestRule.onNodeWithText("Start Date of the event*").assertIsDisplayed()
        performClick()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
      }
      eventEndDate {
        performScrollTo()
        composeTestRule.onNodeWithText("End date of the event").assertIsDisplayed()
        performClick()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
      }
      eventTimeStart {
        performScrollTo()
        composeTestRule.onNodeWithText("Start time*").assertIsDisplayed()
        performClick()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
      eventTimeEnd {
        performScrollTo()
        composeTestRule.onNodeWithText("End time*").assertIsDisplayed()
        performClick()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }

      eventMaxAttendees {
        performScrollTo()
        composeTestRule.onNodeWithText("Max Attendees").assertIsDisplayed()
        performClick()
        assert(hasText("Max Attendees"))
        composeTestRule.onNodeWithText("Max Attendees").assertIsDisplayed()
      }
      eventMinAttendees {
        performScrollTo()
        composeTestRule.onNodeWithText("Min Attendees").assertIsDisplayed()
        performClick()
        assert(hasText("Min Attendees"))
        composeTestRule.onNodeWithText("Min Attendees").assertIsDisplayed()
      }
      eventInscriptionLimitDate {
        performScrollTo()
        composeTestRule.onNodeWithText("Inscription Limit Date").assertIsDisplayed()
        performClick()
        assert(hasText(EventFirebaseConnection.DATE_FORMAT_DISPLAYED))
      }
      eventInscriptionLimitTime {
        performScrollTo()
        composeTestRule.onNodeWithText("Inscription Limit Time").assertIsDisplayed()
        performClick()
        assert(hasText(EventFirebaseConnection.TIME_FORMAT))
      }
      eventLocation {
        performScrollTo()
        composeTestRule.onNodeWithText("Location").assertIsDisplayed()
        performClick()
        assert(hasText("Enter an address"))
        composeTestRule.onNodeWithText("Enter an address").assertIsDisplayed()
      }
    }
  }

  // Location test
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testLocationQueryResult() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()
      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventLocation {
        performClick()
        performTextInput("ecole polytechnique federale de lausanne")
      }
      // wait for the location proposition to appear
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("MenuItem"), 6000)
      locationProposition { performClick() }
      eventLocation {
        assertTextContains("École Polytechnique Fédérale de Lausanne", substring = true)
      }
    }
  }

  @Test
  fun testClearButton() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()
      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("12/04/2026")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      eventMaxAttendees.performTextInput("100")
      eventMinAttendees.performTextInput("10")
      eventInscriptionLimitDate.performTextInput("10/04/2025")
      eventInscriptionLimitTime.performTextInput("09:00")

      clearButton.performClick()

      eventTitle.assert(hasText(""))
      eventDescription.assert(hasText(""))
      eventStartDate.assert(hasText(""))
      eventEndDate.assert(hasText(""))
      eventTimeStart.assert(hasText(""))
      eventTimeEnd.assert(hasText(""))
      eventLocation.assert(hasText(""))
      eventMaxAttendees.assert(hasText(""))
      eventMinAttendees.assert(hasText(""))
      eventInscriptionLimitDate.assert(hasText(""))
      eventInscriptionLimitTime.assert(hasText(""))
    }
  }

  @Test
  fun testDraftEventIsDisplay() {
    // Create a draft event
    // Save the draft
    val context = ApplicationProvider.getApplicationContext<Context>()
    EventUtils()
        .saveDraftEvent(
            "title",
            "description",
            Location(0.0, 0.0, "Malibu"),
            "12/04/2026",
            "12/05/2026",
            "10:00",
            "12:00",
            "100",
            "10",
            "10/04/2025",
            "09:00",
            setOf(Interests.TENNIS, Interests.BASKETBALL),
            "",
            context = context)
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()
      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      // Check if the draft event is displayed
      eventTitle.assert(hasText("title"))
      eventDescription.assert(hasText("description"))
      eventStartDate.assert(hasText("12/04/2026"))
      eventEndDate.assert(hasText("12/05/2026"))
      eventTimeStart.assert(hasText("10:00"))
      eventTimeEnd.assert(hasText("12:00"))
      eventMaxAttendees.assert(hasText("100"))
      eventMinAttendees.assert(hasText("10"))
      eventInscriptionLimitDate.assert(hasText("10/04/2025"))
      eventInscriptionLimitTime.assert(hasText("09:00"))
    }
    EventUtils().deleteDraft(context)
  }

  @Test
  fun testClickOnSaveDraft() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    composeTestRule.setContent {
      val navController = rememberNavController()
      val eventUtils = EventUtils()
      CreateEvent(nav = NavigationActions(navController), eventUtils, EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventTitle.performTextInput("Test Event")
      eventDescription.performTextInput("This is a test event")
      eventStartDate.performTextInput("12/04/2026")
      eventEndDate.performTextInput("12/05/2026")
      eventTimeStart.performTextInput("10:00")
      eventTimeEnd.performTextInput("12:00")
      eventMaxAttendees.performTextInput("100")
      eventMinAttendees.performTextInput("10")
      eventInscriptionLimitDate.performTextInput("10/04/2025")
      eventInscriptionLimitTime.performTextInput("09:00")

      saveDraftButton {
        assertExists()
        performClick()
      }
      // Check if the draft event is displayed
      eventTitle.assert(hasText("Test Event"))
      eventDescription.assert(hasText("This is a test event"))
      eventStartDate.assert(hasText("12/04/2026"))
      eventEndDate.assert(hasText("12/05/2026"))
      eventTimeStart.assert(hasText("10:00"))
      eventTimeEnd.assert(hasText("12:00"))
      eventMaxAttendees.assert(hasText("100"))
      eventMinAttendees.assert(hasText("10"))
      eventInscriptionLimitDate.assert(hasText("10/04/2025"))
      eventInscriptionLimitTime.assert(hasText("09:00"))
    }
    EventUtils().retrieveFromDraft(context)!!
    EventUtils().deleteDraft(context)
  }
}
