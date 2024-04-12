package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.EventsViewModel
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
    }
  }
}
