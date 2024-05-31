package com.github.se.gatherspot.ui

import android.util.Log
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.EnvironmentSetter.Companion.profileFirebaseConnection
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.sql.AppDatabase
import com.github.se.gatherspot.ui.eventUI.EventUI
import com.github.se.gatherspot.ui.eventUI.EventUIViewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.lang.Thread.sleep
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventUITest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var db: AppDatabase

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
    db =
        Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
  }

  fun cleanUp() {
    runBlocking {
      ProfileFirebaseConnection().delete(testLoginUID)
      ProfileFirebaseConnection().delete(Profile.testParticipant().id)
      ProfileFirebaseConnection().delete(Profile.testOrganizer().id)
    }
  }

  private val pastEventAttended =
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
          finalAttendees = listOf(testLoginUID),
          timeBeginning = LocalTime.of(13, 0),
          timeEnding = LocalTime.of(16, 0),
          image = "")
  val eventAttendable =
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
          eventStartDate = LocalDate.now().minusDays(4),
          globalRating = 4,
          inscriptionLimitDate = LocalDate.now().plusDays(1),
          inscriptionLimitTime = LocalTime.of(23, 59),
          location = null,
          registeredUsers = mutableListOf(testLoginUID),
          timeBeginning = LocalTime.of(13, 0),
          timeEnding = LocalTime.of(16, 0),
          image = "")

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val navController = rememberNavController()

      EventUI(
          eventAttendable,
          NavigationActions(navController),
          EventUIViewModel(eventAttendable),
          null)
    }
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("profileIndicator"), 10000)
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
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("attendButton"), 10000)
      attendButton.assertExists()
    }
  }

  @OptIn(ExperimentalTestApi::class)
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
      EventUI(event, NavigationActions(navController), EventUIViewModel(eventAttendable), null)
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("profileIndicator"), 10000)
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
      registerButton { assertIsDisplayed() }
      attendButton { assertIsDisplayed() }
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
      EventUI(event, NavigationActions(navController), EventUIViewModel(event), null)
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
              eventEndDate = LocalDate.of(2025, 4, 15),
              eventStartDate = LocalDate.of(2025, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2025, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf(),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(event, NavigationActions(navController), EventUIViewModel(event), null)
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
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
        assertIsEnabled()
        assert(hasText("Registered / Unregister"))
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
              eventEndDate = LocalDate.of(2025, 4, 15),
              eventStartDate = LocalDate.of(2025, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2025, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("1", "2"),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), null)
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        assert(hasText("Full event"))
        assertIsNotEnabled()
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRegistrationStatus() =
      runTest(timeout = Duration.parse("20s")) {
        val eventFirebase = EventFirebaseConnection()
        val event =
            Event(
                id = "1",
                title = "Event Title",
                description = "Hello: I am a description",
                attendanceMaxCapacity = 10,
                attendanceMinCapacity = 1,
                organizerID = Profile.testParticipant().id,
                categories = setOf(Interests.BASKETBALL),
                eventEndDate = LocalDate.of(2025, 4, 15),
                eventStartDate = LocalDate.of(2025, 4, 14),
                globalRating = 4,
                inscriptionLimitDate = LocalDate.of(2025, 4, 11),
                inscriptionLimitTime = LocalTime.of(23, 59),
                location = null,
                registeredUsers = mutableListOf(),
                timeBeginning = LocalTime.of(13, 0),
                timeEnding = LocalTime.of(16, 0),
                image = "")
        eventFirebase.add(event)

        composeTestRule.setContent {
          val navController = rememberNavController()

          EventUI(event, NavigationActions(navController), EventUIViewModel(event), null)
        }
        ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
          composeTestRule.waitForIdle()
          registerButton {
            assert(hasText("Unregistered / Register"))
            performClick()
          }
          composeTestRule.waitUntilAtLeastOneExists(hasTestTag("okButton"), 6000)
          okButton { performClick() }
          registerButton {
            assert(hasText("Registered / Unregister"))
            performClick()
          }
          composeTestRule.waitUntilAtLeastOneExists(hasTestTag("okButton"), 6000)
          okButton { performClick() }
          checkin { assertExists() }
          verifyCheckin { assertDoesNotExist() }
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

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), null)
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      calendarButton { assertIsDisplayed() }
      editEventButton { assertIsDisplayed() }
      deleteButton { assertIsDisplayed() }
      verifyCheckin { assertExists() }
      checkin { assertDoesNotExist() }
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
              organizerID = Firebase.auth.uid!!,
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

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), null)
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
    val eventUIViewModel = EventUIViewModel(pastEventAttended)
    composeTestRule.setContent {
      val navController = rememberNavController()
      EventUI(pastEventAttended, NavigationActions(navController), eventUIViewModel, null)
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      Log.e("isOrganizer", eventUIViewModel.isOrganizer().toString())
      Log.e(
          "In the list",
          pastEventAttended.finalAttendees
              ?.contains(FirebaseAuth.getInstance().currentUser!!.uid)
              .toString())
      Log.e("isEventOver", EventUtils().isEventOver(pastEventAttended).toString())
      sleep(1000)
      assert(eventUIViewModel.canRate())
      sleep(1000)
      starRow {
        performScrollTo()
        assertIsDisplayed()
      }
      bottomSpacer { performScrollTo() }
      starIcon_1 { assertIsDisplayed() }
    }
  }

  @OptIn(ExperimentalTestApi::class)
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
              organizerID = Profile.testParticipant().id,
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

      EventUI(event, NavigationActions(navController), EventUIViewModel(event), null)
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("profileIndicator"), 10000)
      profileIndicator.assertIsDisplayed()
      userName { hasText("John Doe") }
      // profileIndicator.performClick()
    }
  }
}
