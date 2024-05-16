package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.model.event.EventUIViewModel
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import com.github.se.gatherspot.utils.MockEventFirebaseConnection
import com.github.se.gatherspot.utils.MockFollowList
import com.github.se.gatherspot.utils.MockIdListFirebaseConnection
import com.github.se.gatherspot.utils.MockProfileFirebaseConnection
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

class EventsViewCompleteTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val event = DefaultEvents.trivialEvent1

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun fromEventsToOrganizerProfile() {
    // This test will navigate from the events screen to the organizer profile
    val viewModel = EventsViewModel(MockEventFirebaseConnection())
    val eventRegistrationModel =
        EventRegistrationViewModel(
            emptyList(),
            MockProfileFirebaseConnection(),
            MockEventFirebaseConnection(),
            MockIdListFirebaseConnection())
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "trivialEvent", route = "home") {
          composable("trivialEvent") {
            EventUI(
                event,
                NavigationActions(navController),
                EventUIViewModel(
                    event,
                    MockProfileFirebaseConnection(),
                    MockEventFirebaseConnection(),
                    MockIdListFirebaseConnection()),
                MockProfileFirebaseConnection())
          }
          composable("viewProfile/{uid}") { backstackEntry ->
            backstackEntry.arguments?.getString("uid")?.let {
              ViewProfile(
                  NavigationActions(navController),
                  it,
                  ProfileViewModel(
                      it,
                      NavigationActions(navController),
                      MockProfileFirebaseConnection(),
                      MockFollowList()))
            }
          }
        }
      }
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      // click on the profile indicator
      profileIndicator {
        assertIsDisplayed()
        performClick()
      }
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      // Check that the profile screen is displayed
      back.performClick()
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      // Check that the event screen is displayed
      composeTestRule.waitUntilAtLeastOneExists(hasText(event.title))
    }
  }
}
