package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventUITest {
  @get:Rule val composeTestRule = createComposeRule()
  private val trivialEvent = DefaultEvents.trivialEvent1
  private lateinit var ownEvent: Event

  @Before
  fun setUp() = runBlocking {
    testLogin()
    ownEvent = DefaultEvents.withAuthor(Firebase.auth.uid!!, "1")
  }

  @After fun cleanUp() = runBlocking { testLoginCleanUp() }

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event = trivialEvent
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
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
      val event = trivialEvent
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
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
      val event = trivialEvent
      // make sure the profile used in showProfile is there
      runBlocking { ProfileFirebaseConnection().add(Profile.testOrganizer()) }
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      description { assert(hasText(DefaultEvents.trivialEvent1.description!!)) }
      profileIndicator {
        hasText("Hosted by")
        hasText("John Doe")
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
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    runBlocking { EventFirebaseConnection().add(trivialEvent) }
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
    runBlocking { EventFirebaseConnection().delete(trivialEvent.id) }
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
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
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
  fun testAlreadyRegistered(): Unit {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          DefaultEvents.withRegistered(FirebaseAuth.getInstance().currentUser!!.uid, eventId = "2")
      val eventfirebase = EventFirebaseConnection()
      runBlocking { eventfirebase.add(event) }

      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf(FirebaseAuth.getInstance().currentUser!!.uid)),
          EventsViewModel())
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
      val event = ownEvent

      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
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
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
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
  fun testProfileIsCorrectlyFetched() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event = DefaultEvents.withAuthor(Profile.testOrganizer().id, "1")
      runBlocking { ProfileFirebaseConnection().add(Profile.testOrganizer()) }
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      profileIndicator.assertIsDisplayed()
      userName { hasText("John Doe") }
      // profileIndicator.performClick()
    }
  }
  // write an integration test that tests the following:
  // Start from Events screen
}
