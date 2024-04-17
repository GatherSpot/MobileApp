package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.UserFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.ui.SetUpProfile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetUpTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  val UserFirebaseConnection = UserFirebaseConnection()

  @After
  fun cleanUp() {
    UserFirebaseConnection.delete(MainActivity.uid)
    UserFirebaseConnection.deleteCurrentUser()
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun setUp() {
    MainActivity.uid = "test"
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "auth") {
        navigation(startDestination = "setup", route = "auth") {
          composable("setup") { SetUpProfile(NavigationActions(navController), MainActivity.uid) }
        }
      }
    }

    composeTestRule.waitForIdle()
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      lazyColumn {
        assertExists()
        assertIsDisplayed()
      }
      composeTestRule.waitForIdle()
      var c = 0
      for (category in allCategories) {
        category {
          composeTestRule
              .onNodeWithTag("lazyColumn")
              .performScrollToNode(hasTestTag(enumValues<Interests>().toList()[c].toString()))
          assertExists()
          performClick() // Select the category
          performClick() // Deselect the category
          c++
        }
      }

      save {
        assertExists()
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitUntilAtLeastOneExists(
          hasText("Please verify your email before continuing"))
      emailText.assertIsDisplayed()
    }
  }
}
