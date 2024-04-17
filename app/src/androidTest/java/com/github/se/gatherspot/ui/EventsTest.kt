package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val viewModel = EventsViewModel()
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
      EventFirebaseConnection.offset = null
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRefreshButtonFunctional() {
    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    assert(viewModel.getLoadedEvents().size.toLong() == EventsViewModel.PAGESIZE)
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
      assert(viewModel.getLoadedEvents().size.toLong() == 2 * EventsViewModel.PAGESIZE)
      EventFirebaseConnection.offset = null
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun dropdownMenuFunctional() {
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
    EventFirebaseConnection.offset = null
  }

  @Test
  fun filterWorks() {
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
      EventFirebaseConnection.offset = null
    }
  }
}
