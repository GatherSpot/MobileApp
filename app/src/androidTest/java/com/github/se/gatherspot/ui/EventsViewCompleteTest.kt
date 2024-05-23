package com.github.se.gatherspot.ui

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.screens.EditProfileScreen
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.sql.AppDatabase
import com.github.se.gatherspot.ui.eventUI.EventUI
import com.github.se.gatherspot.ui.eventUI.EventUIViewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScaffold
import com.github.se.gatherspot.ui.profile.ProfileScreen
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import com.github.se.gatherspot.ui.topLevelDestinations.Events
import com.github.se.gatherspot.ui.topLevelDestinations.EventsViewModel
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

class EventsViewCompleteTest {
  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun fromEventsToOrganizerProfile() {
    // The test need to be logged in
    testLogin()
    // This test will navigate from the events screen to the organizer profile

    composeTestRule.setContent {
      val context = ApplicationProvider.getApplicationContext<Context>()
      val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
      val viewModel = EventsViewModel(db)
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "events", route = "home") {
          composable("events") { Events(viewModel, NavigationActions(navController)) }
          composable("event/{eventJson}") { backStackEntry ->
            val eventObject = Event.fromJson(backStackEntry.arguments?.getString("eventJson")!!)
            EventUI(
                event = eventObject,
                navActions = NavigationActions(navController),
                eventUIViewModel = EventUIViewModel(eventObject),
                eventsViewModel = viewModel)
          }
          composable("profile") {
            ProfileScaffold(NavigationActions(navController), viewModel { OwnProfileViewModel() })
          }
          composable("viewProfile/{uid}") { backstackEntry ->
            backstackEntry.arguments?.getString("uid")?.let {
              ProfileScreen(viewModel<ProfileViewModel> { ProfileViewModel(it, navController) })
            }
          }
        }
      }
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu.performClick()
      interestsDialog { assertIsDisplayed() }

      val indexBasketball = Interests.BASKETBALL.ordinal

      categories[indexBasketball] { performClick() }

      setFilterButton { performClick() }

      composeTestRule.waitForIdle()

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("eventsList"), 6000)
      eventRow.performClick()
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("profileIndicator"), 10000)
      // Test the back button
      backButton {
        assertIsDisplayed()
        performClick()
      }
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      eventRow.performClick()
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("profileIndicator"), 10000)
      // Test the profile indicator
      profileIndicator {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
    }

    ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
      // Check that the profile screen is displayed
      usernameInput.assertIsDisplayed()
      bioInput.assertIsDisplayed()
      follow {
        assertIsDisplayed()
        performClick()
      }
      addFriend {
        assertIsDisplayed()
        performClick()
      }

      back.performClick()
    }
  }
}
