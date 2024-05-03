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
      assert(
          viewModel.uiState.value.list.all { e -> e.categories?.contains(Interests.SPORT) ?: true })
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testRefreshButtonFunctionalWithFilter() {
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
}
