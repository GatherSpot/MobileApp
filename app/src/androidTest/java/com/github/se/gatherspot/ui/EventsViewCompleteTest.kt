package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalTimeSerializer
import com.github.se.gatherspot.screens.EditProfileScreen
import com.github.se.gatherspot.screens.EventUIScreen
import com.github.se.gatherspot.screens.EventsScreen
import com.github.se.gatherspot.ui.topLevelDestinations.Events
import com.github.se.gatherspot.ui.topLevelDestinations.EventsViewModel
import com.github.se.gatherspot.ui.topLevelDestinations.Profile
import com.github.se.gatherspot.ui.topLevelDestinations.ViewProfile
import com.github.se.gatherspot.ui.eventUI.EventUI
import com.github.se.gatherspot.ui.eventUI.EventUIViewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalTime
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
    val viewModel = EventsViewModel()
    Thread.sleep(5000)
    // Create a new Gson instance with the custom serializers and deserializers
    val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .create()

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "events", route = "home") {
          composable("events") { Events(viewModel, NavigationActions(navController)) }
          composable("event/{eventJson}") { backStackEntry ->
            val eventObject =
                gson.fromJson(
                    URLDecoder.decode(
                        backStackEntry.arguments?.getString("eventJson"),
                        StandardCharsets.US_ASCII.toString()),
                    Event::class.java)
            EventUI(
                event = eventObject!!,
                navActions = NavigationActions(navController),
                eventUIViewModel = EventUIViewModel(eventObject),
                eventsViewModel = viewModel)
          }
          composable("profile") { Profile(NavigationActions(navController)) }
          composable("viewProfile/{uid}") { backstackEntry ->
            backstackEntry.arguments?.getString("uid")?.let {
              ViewProfile(NavigationActions(navController), it)
            }
          }
        }
      }
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      filterMenu.performClick()
      dropdown { assertIsDisplayed() }

      val indexBasketball = Interests.BASKETBALL.ordinal

      categories[indexBasketball] {
        composeTestRule
            .onNodeWithTag("dropdown")
            .performScrollToNode(
                hasTestTag(enumValues<Interests>().toList()[indexBasketball].toString()))
        assertExists()
        performClick()
      }

      filterMenu { performClick() }

      composeTestRule.waitForIdle()

      refresh { performClick() }

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("fetch"), 10000)
      composeTestRule.waitUntilDoesNotExist(hasTestTag("fetch"), 10000)
      //  composeTestRule.waitUntilAtLeastOneExists(hasTestTag("Test Event"), 6000)
      eventRow.performClick()
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
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
      // Test the profile indicator
      profileIndicator {
        assertIsDisplayed()
        performClick()
      }
    }

    ComposeScreen.onComposeScreen<EditProfileScreen>(composeTestRule) {
      // Check that the profile screen is displayed
      usernameInput.assertIsDisplayed()
      bioInput.assertIsDisplayed()
      profileImage.assertIsDisplayed()
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
