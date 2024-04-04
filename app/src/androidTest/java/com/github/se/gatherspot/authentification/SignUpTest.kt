package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.screens.LoginScreen
import com.github.se.gatherspot.screens.SignUpScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @Test
  fun signUp() {

    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) { signUpButton { performClick() } }

    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField { performTextInput("GatherSpot") }
      emailField { performTextInput("gatherspot2024@gmail.com") }
      passwordField { performTextInput("GatherSpot,2024;") }
      Espresso.closeSoftKeyboard()
      composeTestRule.waitForIdle()
      button { performClick() }
      composeTestRule.waitForIdle()
      //  verifDialog { performClick() } -> cannot see it ??? to be done
    }
  }
}
