package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.defaults.DefaultProfiles
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.lang.Thread.sleep
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventsViewCompleteTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val event = DefaultEvents.trivialEvent1
  private val profile = DefaultProfiles.trivial

  @Before
  fun setUp() = runBlocking {
    testLogin()
    ProfileFirebaseConnection().add(profile)
    sleep(3000) // somehow profile is still not available for fetching
  }

  @After
  fun cleanup() = runBlocking {
    testLoginCleanUp()
    ProfileFirebaseConnection().delete(profile.id)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun fromEventsToOrganizerProfile() {
    // This test will navigate from the events screen to the organizer profile
    val viewModel = EventsViewModel()
    val eventRegistrationModel = EventRegistrationViewModel(emptyList())

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "trivialEvent", route = "home") {
          composable("trivialEvent") {
            EventUI(
                event = event,
                navActions = NavigationActions(navController),
                registrationViewModel = eventRegistrationModel,
                eventsViewModel = viewModel)
          }
          composable("viewProfile/{uid}") { backstackEntry ->
            backstackEntry.arguments?.getString("uid")?.let {
              ViewProfile(NavigationActions(navController), it)
            }
          }
        }
      }
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      // click on the profile indicator
      composeTestRule.waitUntilAtLeastOneExists(hasText(profile.userName), 4000)
      profileIndicator {
        assertIsDisplayed()
        performClick()
      }
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      // Check that the profile screen is displayed
      composeTestRule.waitUntilAtLeastOneExists(hasText(profile.userName), 4000)
      back.performClick()
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      // Check that the event screen is displayed
      composeTestRule.waitUntilAtLeastOneExists(hasText(event.title))
    }
  }
}
