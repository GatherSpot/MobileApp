package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.screens.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Before
  fun setUp() {
    runBlocking {
      FirebaseAuth.getInstance().signOut()
      delay(500)
    }
  }

  @Test
  fun buttonsAreCorrectlyDisplayedAndFunctional() {

    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {

      // Test the UI elements
      loginButton {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
      }
      signUpButton {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
      }
      loginButton { performClick() }
    }
  }
}
