package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.testDelete
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.screens.LoginScreen
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.screens.SignUpScreen
import com.google.firebase.auth.FirebaseAuth
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val USERNAME = "AuthEndToEndTest"
const val EMAIL = "AuthEndToEnd@test.com"
const val PASSWORD = "AuthEndToEndTest,2024;"

@RunWith(AndroidJUnit4::class)
class AllTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @After
  fun cleanUp() {
    try {
      val p = runBlocking { ProfileFirebaseConnection().fetchFromUserName("AuthEndToEndTest") }
      p?.let { ProfileFirebaseConnection().delete(it.id) }
      ProfileFirebaseConnection().delete(FirebaseAuth.getInstance().currentUser!!.uid)
      testDelete()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @Before
  fun setup() {
    runBlocking {
      FirebaseAuth.getInstance().signOut()
      val toDelete = async { ProfileFirebaseConnection().fetchFromUserName(USERNAME) }.await()
      if (toDelete != null) ProfileFirebaseConnection().delete(toDelete.id)

      delay(2000)
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun allTest() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) { signUpButton { performClick() } }
    composeTestRule.waitForIdle()
    ComposeScreen.onComposeScreen<SignUpScreen>(composeTestRule) {
      usernameField {
        assertExists()
        assertIsDisplayed()
        performTextInput(USERNAME)
      }
      emailField {
        assertExists()
        assertIsDisplayed()
        performTextInput(EMAIL)
      }
      passwordField {
        assertExists()
        assertIsDisplayed()
        performTextInput(PASSWORD)
      }
      Espresso.closeSoftKeyboard()
      button {
        assertExists()
        assertIsDisplayed()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("verification"), 10000)
      verifDialog {
        assertExists()
        assertIsDisplayed()
        performClick()
      }
    }
    composeTestRule.waitForIdle()
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      // TODO
    }
  }
}
