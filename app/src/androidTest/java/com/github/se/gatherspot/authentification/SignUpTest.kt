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
import com.github.se.gatherspot.EnvironmentSetter.Companion.signUpCleanUp
import com.github.se.gatherspot.EnvironmentSetter.Companion.signUpErrorSetUp
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.screens.SignUpScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.SetUpProfile
import com.github.se.gatherspot.ui.topLevelDestinations.SignUp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
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

  @After
  fun cleanUp() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
      ProfileFirebaseConnection().delete(currentUser.uid)
      testLoginCleanUp()
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun signUp() {
    val email = "gatherspot2024@gmail.com"
    val userName = "GatherSpot"
    // signUpSetUp(userName, email)
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "auth") {
        navigation(startDestination = "signup", route = "auth") {
          composable("signup") { SignUp(NavigationActions(navController)) }
        }
        navigation(startDestination = "events", route = "home") {
          composable("setup") {
            SetUpProfile(NavigationActions(navController), Firebase.auth.currentUser!!.uid)
          }
        }
      }
    }

    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField {
        assertExists()
        assertIsDisplayed()
        performTextInput(userName)
      }
      emailField {
        assertExists()
        assertIsDisplayed()
        performTextInput(email)
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
    }

    signUpCleanUp(userName)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun signUpError() {
    signUpErrorSetUp()
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "auth") {
        navigation(startDestination = "signup", route = "auth") {
          composable("signup") { SignUp(NavigationActions(navController)) }
        }
        navigation(startDestination = "events", route = "home") {
          composable("setup") {
            SetUpProfile(NavigationActions(navController), Firebase.auth.currentUser!!.uid)
          }
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
      Espresso.closeSoftKeyboard()
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("badUsername"), 6000)
      badUsername.assertIsDisplayed()
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("badEmail"), 1000)
      badEmail.assertIsDisplayed()
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("badPassword"), 1000)
      badPassword.assertIsDisplayed()
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun emailAlreadyInUse() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "auth") {
        navigation(startDestination = "signup", route = "auth") {
          composable("signup") { SignUp(NavigationActions(navController)) }
        }
        navigation(startDestination = "events", route = "home") {
          composable("setup") {
            SetUpProfile(NavigationActions(navController), Firebase.auth.currentUser!!.uid)
          }
        }
      }
    }

    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField { performTextInput("otherTest") }
      emailField { performTextInput("test@test.com") }
      passwordField { performTextInput("Test,2024;") }
      button { performClick() }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("signUpFailed"), 6000)
      dialog { assertIsDisplayed() }
    }
  }
}
