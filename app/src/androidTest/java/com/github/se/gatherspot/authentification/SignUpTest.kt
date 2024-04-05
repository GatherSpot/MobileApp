package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.screens.SignUpScreen
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
      SignUp(nav = NavigationActions(navController))
    }

    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField { performTextInput("GatherSpot") }
      emailField { performTextInput("gatherspot2024@gmail.com") }
      passwordField { performTextInput("GatherSpot,2024;") }
      Espresso.closeSoftKeyboard()
      button {
        assertIsDisplayed()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("verification"), 6000)
      verifDialog.assertIsDisplayed()
      ok.assertIsDisplayed()
    }

    /*
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("saveButton"), 20000)
      save { performClick() }
      emailText.assertIsDisplayed()
    }

     */

  }
}
