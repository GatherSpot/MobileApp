package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.signUpErrorSetUp
import com.github.se.gatherspot.screens.SignUpScreen
import com.github.se.gatherspot.ui.SignUp
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.SetUpProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    init {
      val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
      firestore.useEmulator("10.0.2.2", 8080)
      val auth: FirebaseAuth = FirebaseAuth.getInstance()
      auth.useEmulator("10.0.2.2", 9099)
      Firebase.auth.createUserWithEmailAndPassword("test", "test")
      Firebase.auth.createUserWithEmailAndPassword("test@test.com", "testPassword123")
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
          composable("setup") { SetUpProfile(NavigationActions(navController)) }
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

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("verification"), 100000)
      verifDialog.assertExists()
      verifDialog.assertIsDisplayed()
      verifDialog.performClick()
    }
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
          composable("setup") { SetUpProfile(NavigationActions(navController)) }
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
      badUsername { assertExists() }
      badEmail { assertExists() }
      badPassword { assertExists() }
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
          composable("setup") { SetUpProfile(NavigationActions(navController)) }
        }
      }
    }

    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField { performTextInput("otherTest") }
      emailField { performTextInput("test@test.com") }
      passwordField { performTextInput("Test,2024;") }
      button { performClick() }
      composeTestRule.waitUntilAtLeastOneExists(
          hasText("Email already in use", substring = true), 6000)
    }
  }
}
