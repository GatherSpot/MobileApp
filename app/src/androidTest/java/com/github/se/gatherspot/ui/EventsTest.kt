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
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.screens.EventDataFormScreen
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.FirebaseAuth
import io.github.kakaocup.compose.node.element.ComposeScreen
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
    Thread.sleep(5000)
    uid = FirebaseAuth.getInstance().currentUser!!.uid
  }

  @After
  fun cleanUp() {
    testLoginCleanUp()
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
    assert(viewModel.getLoadedEvents().size.toLong() == viewModel.PAGESIZE)
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
      assert(viewModel.getLoadedEvents().size.toLong() == 2 * viewModel.PAGESIZE)
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
    // testLoginCleanUp()
  }

  @Test
  fun testFilterWorks() {
    // testLogin()
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
      assert(
          viewModel.uiState.value.list.all { e -> e.categories?.contains(Interests.SPORT) ?: true })
    }

    //  testLoginCleanUp()
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
      val listOfEvents = viewModel.uiState.value.list
      if (listOfEvents.isNotEmpty()) {
        assert(listOfEvents.all { event -> event.organizer.id == uid })
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

      val listOfEvents = viewModel.uiState.value.list
      if (listOfEvents.isNotEmpty()) {
        assert(listOfEvents.all { event -> event.registeredUsers.contains(uid) })
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun entireCreationFlow() {
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
      }
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) { createMenu { performClick() } }

    ComposeScreen.onComposeScreen<EventDataFormScreen>(composeTestRule) {
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

    Thread.sleep(6000)

    // more asserts are needed but ok for now
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu { performClick() }
      myEvents {
        composeTestRule.onNodeWithTag("dropdown").performScrollToNode(hasTestTag("myEvents"))
        performClick()
      }
      // composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 6000)
      //  assert(viewModel.uiState.value.list.any { event -> event.description ==  "Ayo, 5v5: Come
      // show your skills"})
    }

    //  EventFirebaseConnection().delete(viewModel.uiState.value.list[0].id)
  }
}
