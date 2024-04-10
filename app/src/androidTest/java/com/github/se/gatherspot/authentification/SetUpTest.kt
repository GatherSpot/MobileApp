package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.UserFirebaseConnection
import com.github.se.gatherspot.model.User
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.ui.SetUpProfile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetUpTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

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

    UserFirebaseConnection.addUser(User("test", "test", "test", "test"))

    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      save {
        assertExists()
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitUntilAtLeastOneExists(
          hasText("Please verify your email before continuing"))
    }
    UserFirebaseConnection.deleteUser(MainActivity.uid)
    UserFirebaseConnection.deleteCurrentUser()
  }
}
