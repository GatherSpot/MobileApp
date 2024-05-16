package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.utils.MockEventFirebaseConnection
import com.github.se.gatherspot.utils.MockProfileFirebaseConnection
import io.github.kakaocup.compose.node.element.ComposeScreen
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val viewModel = EventsViewModel(MockEventFirebaseConnection())
  val uid = "MC"

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
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

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testEventsAreDisplayedAndScrollable() {

    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      eventsList {
        assertIsDisplayed()
        performGesture { swipeUp(400F, 0F, 1000) }
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRefreshButtonFunctional() {
    val mockEventFirebaseConnection = MockEventFirebaseConnection()
    val viewModel = EventsViewModel(mockEventFirebaseConnection)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      refresh {
        assertExists()
        performClick()
      }
      // we just want to check if we called the function the right amount of times, this is to test
      // ui only, not firebase
      assertEquals(mockEventFirebaseConnection.getFetchedNext(), 1)
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testDropdownMenuFunctional() {

    composeTestRule.setContent {
      val viewModel = EventsViewModel()
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
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
          c++
        }
      }
    }
  }

  @Test
  fun testFilterWorks() {

    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      dropdown { assertIsDisplayed() }

      val index = Interests.CHESS.ordinal

      categories[index] {
        composeTestRule
            .onNodeWithTag("dropdown")
            .performScrollToNode(hasTestTag(enumValues<Interests>().toList()[index].toString()))
        assertExists()
        performClick()
      }

      filterMenu { performClick() }

      composeTestRule.waitForIdle()
      assert(
          viewModel.uiState.value.list.all { e -> e.categories?.contains(Interests.CHESS) ?: true })
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRefreshButtonFunctionalWithFilter() {
    val mockEventFirebaseConnection = MockEventFirebaseConnection()
    val viewModel = EventsViewModel(mockEventFirebaseConnection)
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu {
        assertIsDisplayed()
        performClick()
      }

      dropdown { assertIsDisplayed() }

      val basketball = Interests.BASKETBALL
      val chess = Interests.CHESS

      categories[basketball.ordinal] {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag(basketball.name))
        assertExists()
        performClick()
      }

      categories[chess.ordinal] {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag(chess.name))
        assertExists()
        performClick()
      }

      filterMenu { performClick() }

      composeTestRule.waitForIdle()

      refresh { performClick() }

      assert(
          viewModel.uiState.value.list.all { e ->
            e.categories!!.contains(basketball) || e.categories!!.contains(chess)
          })
      // we only test if the function has been called good amount of times, we only test ui here
      assertEquals(mockEventFirebaseConnection.getFetchedNext(), 1)
    }
  }

  @Test
  fun testMyEventsFilterWorks() {
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
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

      val listOfEvents = viewModel.uiState.value.list
      if (listOfEvents.isNotEmpty()) {
        assert(listOfEvents.all { event -> event.organizerID == uid })
      }
    }
  }

  @Test
  fun testRegisteredToWorks() {
    val viewModel = EventsViewModel()
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Events(viewModel = viewModel, nav = nav, MockProfileFirebaseConnection())
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
      val listOfEvents = viewModel.uiState.value.list
      if (listOfEvents.isNotEmpty()) {
        assert(listOfEvents.all { event -> event.registeredUsers.contains(uid) })
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun entireCreationFlow() {
    /*
    val viewModel = EventsViewModel()
    Thread.sleep(6000)
    assert(viewModel.uiState.value.list.isNotEmpty())

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "events", route = "home") {
          composable("events") { Events(viewModel, NavigationActions(navController)) }
        }
        navigation(startDestination = "form", route = "createEvent") {
          composable("form") {
            CreateEvent(
                nav = NavigationActions(navController),
                eventUtils = EventUtils(),
                viewModel = viewModel)
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
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) { createMenu.performClick() }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      eventTitle.performScrollTo()
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

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu { performClick() }
      myEvents {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag("myEvents"))
        performClick()
      }
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

     */
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
