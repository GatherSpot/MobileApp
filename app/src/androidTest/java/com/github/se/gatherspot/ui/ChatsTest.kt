package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.swipeUp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.screens.ChatsScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun testLogin() {
    Firebase.auth.signInWithEmailAndPassword("neverdeleted@mail.com", "GatherSpot,2024;")
  }

  @After
  fun testLoginCleanUp() {
    Firebase.auth.signOut()
  }

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val viewModel = ChatsListViewModel()
      val nav = NavigationActions(rememberNavController())
      Chats(viewModel = viewModel, nav = nav)
    }

    ComposeScreen.onComposeScreen<ChatsScreen>(composeTestRule) {
      topBar {
        assertExists()
        assertIsDisplayed()
        assertTextContains("Chats")
      }

      createMenu {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
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

    val viewModel = ChatsListViewModel()
    composeTestRule.setContent {
      val nav = NavigationActions(rememberNavController())
      Chats(viewModel = viewModel, nav = nav)
    }

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
