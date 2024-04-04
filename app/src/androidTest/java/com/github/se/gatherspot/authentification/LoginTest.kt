package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.screens.LoginScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)
  private lateinit var loginScreen: LoginScreen

  @Before
  fun setup() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) { loginScreen = this }
  }

  @Test
  fun buttonsAreCorrectlyDisplayedAndFunctional() {
    loginScreen.apply {
      // Test the UI elements
      loginButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      signUpButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      loginButton { performClick() }
    }
  }
}
