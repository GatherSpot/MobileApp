package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.screens.ChatsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.Chats
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatsTest {

  @get:Rule val composeTestRule = createComposeRule()
  val event = DefaultEvents.trivialEvent1

  @Before
  fun setup() = runBlocking {
    testLogin()
    EventFirebaseConnection().add(event)
  }

  @After
  fun cleanUp() = runBlocking {
    testLoginCleanUp()
    EventFirebaseConnection().delete(event.id)
  }

  @Test
  fun testEverythingExists() {

    composeTestRule.waitForIdle()

    composeTestRule.setContent {
      val viewModel = ChatsListViewModel()
      val nav = NavigationActions(rememberNavController())
      Chats(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
      topBar {
        assertExists()
        assertIsDisplayed()
      }

      empty {
        assertExists()
        assertIsDisplayed()
      }
    }
  }

  @OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)
  @Test
  fun chatsAreDisplayedAndScrollable() {

    composeTestRule.waitForIdle()

    val viewModel = ChatsListViewModel()
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Chats(viewModel = viewModel, nav = nav)
    }

    runBlocking { EventFirebaseConnection().addRegisteredUser(event.id, Firebase.auth.uid!!) }

    ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("chatsList"), 20000)
      eventsList {
        assertExists()
        assertIsDisplayed()
        performGesture { swipeUp(400F, 0F, 1000) }
      }
    }
  }
}
