package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalTimeSerializer
import com.github.se.gatherspot.screens.EventDataFormScreen
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var uid: String

  @Before
  fun setUp() {
    testLogin()
    uid = FirebaseAuth.getInstance().currentUser!!.uid
  }

  @After
  fun cleanUp() {
    testLoginCleanUp()
    Thread.sleep(1000)
  }

  @Test
  fun testEverythingExists() {
    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterText {
        assertExists()
        assertIsDisplayed()
      }

      filterMenu {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
      }

      createText {
        assertExists()
        assertIsDisplayed()
      }

      createMenu {
        assertExists()
        assertHasClickAction()
      }

      refresh {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)
  @Test
  fun testEventsAreDisplayedAndScrollable() {

    composeTestRule.setContent {
      val viewModel = EventsViewModel()
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      emptyText {
        assertExists()
        assertIsDisplayed()
      }

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 20000)
      eventsList {
        assertIsDisplayed()
        performGesture { swipeUp(400F, 0F, 1000) }
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRefreshButtonFunctional() {
    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    val prev = viewModel.getLoadedEvents().size.toLong()
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      refresh {
        assertExists()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("fetch"), 500)
      composeTestRule.waitUntilDoesNotExist(hasTestTag("fetch"), 10000)
      assert(viewModel.getLoadedEvents().size.toLong() >= prev)
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testDropdownMenuFunctional() {

    composeTestRule.setContent {
      val viewModel = EventsViewModel()
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 10000)
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      dropdown { assertIsDisplayed() }

      var c = 0
      for (category in categories) {
        category {
          composeTestRule
              .onNodeWithTag("dropdown")
              .performScrollToNode(hasTestTag(enumValues<Interests>().toList()[c].toString()))
          assertExists()
          performClick()
          performClick()
          c++
        }
      }
    }
  }

  @Test
  fun testFilterWorks() {

    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      dropdown { assertIsDisplayed() }

      val index = Interests.SPORT.ordinal

      categories[index] {
        composeTestRule
            .onNodeWithTag("dropdown")
            .performScrollToNode(hasTestTag(enumValues<Interests>().toList()[index].toString()))
        assertExists()
        performClick()
      }

      filterMenu { performClick() }

      composeTestRule.waitForIdle()
      Thread.sleep(3000)
      assert(
          viewModel.uiState.value.list.all { e -> e.categories?.contains(Interests.SPORT) ?: true })
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRefreshButtonFunctionalWithFilter() {
    assert(FirebaseAuth.getInstance().currentUser != null)
    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      dropdown { assertIsDisplayed() }

      val indexBasketball = Interests.BASKETBALL.ordinal
      val indexChess = Interests.CHESS.ordinal

      categories[indexBasketball] {
        composeTestRule
            .onNodeWithTag("dropdown")
            .performScrollToNode(
                hasTestTag(enumValues<Interests>().toList()[indexBasketball].toString()))
        assertExists()
        performClick()
      }

      categories[indexChess] {
        composeTestRule
            .onNodeWithTag("dropdown")
            .performScrollToNode(
                hasTestTag(enumValues<Interests>().toList()[indexChess].toString()))
        assertExists()
        performClick()
      }

      filterMenu { performClick() }

      composeTestRule.waitForIdle()

      refresh { performClick() }

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("fetch"), 5000)
      composeTestRule.waitUntilDoesNotExist(hasTestTag("fetch"), 5000)

      Thread.sleep(3000)

      assert(
          viewModel.uiState.value.list.all { e ->
            if (e.categories == null) {
              false
            } else {
              e.categories!!.contains(Interests.BASKETBALL) ||
                  e.categories!!.contains(Interests.CHESS)
            }
          })
    }
  }

  @Test
  fun testMyEventsFilterWorks() {
    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      dropdown { assertIsDisplayed() }

      myEvents {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag("myEvents"))
        performClick()
      }

      Thread.sleep(3000)
      val listOfEvents = viewModel.uiState.value.list
      if (listOfEvents.isNotEmpty()) {
        assert(listOfEvents.all { event -> event.organizerID == uid })
      }
    }
  }

  @Test
  fun testRegisteredToWorks() {
    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      dropdown { assertIsDisplayed() }

      registeredTo {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag("registeredTo"))
        performClick()
      }
      Thread.sleep(3000)
      val listOfEvents = viewModel.uiState.value.list
      if (listOfEvents.isNotEmpty()) {
        assert(listOfEvents.all { event -> event.registeredUsers.contains(uid) })
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun entireCreationFlow() {
    val eventCreated =
        Event(
            id = "8",
            title = "Basketball Game",
            description = "Ayo, 5v5: Come show your skills",
            attendanceMaxCapacity = 10,
            attendanceMinCapacity = 5,
            organizerID = uid,
            categories = setOf(Interests.BASKETBALL),
            eventEndDate = LocalDate.of(2024, 5, 15),
            eventStartDate = LocalDate.of(2024, 5, 15),
            globalRating = 4,
            inscriptionLimitDate = LocalDate.of(2024, 4, 11),
            inscriptionLimitTime = LocalTime.of(23, 59),
            location = null,
            registeredUsers = mutableListOf(),
            timeBeginning = LocalTime.of(13, 0),
            timeEnding = LocalTime.of(19, 0),
        )

    val eventFirebase = EventFirebaseConnection()
    eventFirebase.add(eventCreated)

    runBlocking { eventFirebase.fetch(eventCreated.id) }

    val viewModel = EventsViewModel()
    Thread.sleep(5000)

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "events", route = "home") {
          composable("events") { Events(viewModel, NavigationActions(navController)) }
        }
        navigation(startDestination = "form", route = "createEvent") {
          composable("form") {
            EventDataForm(
                eventUtils = EventUtils(),
                viewModel = viewModel,
                nav = NavigationActions(navController),
                eventAction = EventAction.CREATE)
          }
        }
        navigation(startDestination = "view", route = "event/{eventJson}") {
          composable("view") { backStackEntry ->
            val gson: Gson =
                GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
                    .create()
            val eventObject =
                gson.fromJson(backStackEntry.arguments?.getString("eventJson"), Event::class.java)
            EventUI(
                event = eventObject,
                navActions = NavigationActions(navController),
                registrationViewModel = EventRegistrationViewModel(eventObject.registeredUsers),
                eventsViewModel = viewModel)
          }
        }
        navigation(startDestination = "edit", route = "editEvent/{eventJson}") {
          composable("edit") { backStackEntry ->
            val gson: Gson =
                GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
                    .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
                    .create()

            val eventObject =
                gson.fromJson(backStackEntry.arguments?.getString("eventJson"), Event::class.java)

            EditEvent(
                event = eventObject,
                eventUtils = EventUtils(),
                nav = NavigationActions(navController),
                viewModel = viewModel)
          }
        }
      }
    }

    /*
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) { createMenu.performClick() }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("inputTitle"), 5000)
      eventTitle.performTextInput("Basketball Game")
      Espresso.closeSoftKeyboard()
      eventDescription.performTextInput("Ayo, 5v5: Come show your skills")
      Espresso.closeSoftKeyboard()
      eventStartDate.performTextInput("10/07/2024")
      Espresso.closeSoftKeyboard()
      eventEndDate.performTextInput("10/07/2024")
      Espresso.closeSoftKeyboard()
      eventTimeStart.performTextInput("13:00")
      Espresso.closeSoftKeyboard()
      eventTimeEnd.performTextInput("19:00")
      Espresso.closeSoftKeyboard()
      eventLocation.performTextInput("Bussy-Saint-Georges")
      Espresso.closeSoftKeyboard()
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("MenuItem"), 6000)
      Espresso.closeSoftKeyboard()
      locationProposition { performClick() }
      Espresso.closeSoftKeyboard()
      eventMaxAttendees.performTextInput("10")
      Espresso.closeSoftKeyboard()
      eventMinAttendees.performTextInput("5")
      Espresso.closeSoftKeyboard()
      eventInscriptionLimitDate.performTextInput("10/06/2024")
      Espresso.closeSoftKeyboard()
      eventInscriptionLimitTime.performTextInput("09:00")
      Espresso.closeSoftKeyboard()
      eventSaveButton.performScrollTo()
      eventSaveButton.performClick()
    }

     */

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu { performClick() }
      myEvents {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag("myEvents"))
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 6000)
      assert(
          viewModel.uiState.value.list.any { event ->
            event.description == "Ayo, 5v5: Come show your skills"
          })
      eventCreated { performClick() }
    }

    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      editEventButton { performClick() }
    }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      eventDescription { assertTextContains("Ayo, 5v5: Come show your skills") }
    }

    val event = viewModel.uiState.value.list.filter { e -> e.title == "Basketball Game" }[0]
    EventFirebaseConnection().delete(event.id)
  }
}
