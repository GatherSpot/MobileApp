package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.UserFirebaseConnection
import com.github.se.gatherspot.screens.SignUpScreen
import com.github.se.gatherspot.ui.SetUpProfile
import com.github.se.gatherspot.ui.SignUp
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  //  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun signUp() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "auth") {
        navigation(startDestination = "signup", route = "auth") {
          composable("signup") { SignUp(NavigationActions(navController)) }
        }
        navigation(startDestination = "events", route = "home") {
          composable("setup") { SetUpProfile(NavigationActions(navController), MainActivity.uid) }
        }
      }
    }

    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField {
        assertExists()
        assertIsDisplayed()
        performTextInput("GatherSpot")
      }
      emailField {
        assertExists()
        assertIsDisplayed()
        performTextInput("gatherspot2024@gmail.com")
      }
      passwordField {
        assertExists()
        assertIsDisplayed()
        performTextInput("GatherSpot,2024;")
      }
      Espresso.closeSoftKeyboard()
      button {
        assertExists()
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("verification"), 6000)

      verifDialog.assertExists()
      verifDialog.assertIsDisplayed()
      verifDialog.performClick()

      UserFirebaseConnection.deleteUser(MainActivity.uid)
      UserFirebaseConnection.deleteCurrentUser()
    }
  }

  @Test
  fun signUpError() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "auth") {
        navigation(startDestination = "signup", route = "auth") {
          composable("signup") { SignUp(NavigationActions(navController)) }
        }
        navigation(startDestination = "events", route = "home") {
          composable("setup") { SetUpProfile(NavigationActions(navController), MainActivity.uid) }
        }
      }
    }

    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField {
        performTextInput("test")
        assertExists()
        assertIsDisplayed()
      }
      emailField {
        performTextInput("test")
        assertExists()
        assertIsDisplayed()
      }
      passwordField {
        performTextInput("test")
        assertExists()
        assertIsDisplayed()
      }
    }
  }
}
