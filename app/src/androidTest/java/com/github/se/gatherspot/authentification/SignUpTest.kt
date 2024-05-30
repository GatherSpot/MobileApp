package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.github.se.gatherspot.ui.signUp.SignUp
import com.github.se.gatherspot.ui.signUp.SignUpViewModel
import com.github.se.gatherspot.ui.topLevelDestinations.SetUpProfile
import com.google.firebase.auth.FirebaseAuth
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlin.time.Duration
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var viewModel: SignUpViewModel

  @Before
  fun setup() {
    viewModel = SignUpViewModel()
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "auth") {
        navigation(startDestination = "signup", route = "auth") {
          composable("signup") { SignUp(viewModel, NavigationActions(navController)) }
        }
        navigation(startDestination = "events", route = "home") {
          composable("setup") { SetUpProfile(NavigationActions(navController)) }
        }
      }
    }
  }

  @After
  fun cleanUp() = runTest {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
      ProfileFirebaseConnection().delete(currentUser.uid)
      testLoginCleanUp()
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun signUp() =
      runTest(timeout = Duration.parse("30s")) {
        val email = "gatherspot2024@gmail.com"
        val userName = "GatherSpot"
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
        }

        signUpCleanUp(userName)
      }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun signUpError() {
    signUpErrorSetUp()
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
