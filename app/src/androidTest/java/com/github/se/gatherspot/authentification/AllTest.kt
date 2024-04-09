package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.UserFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.screens.LoginScreen
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.screens.SignUpScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
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
      lazyColumn {
        assertExists()
        assertIsDisplayed()
      }
      for (category in allCategories) {
        category {
          assertExists()
          assertIsDisplayed()
          performClick()
          performGesture { swipeUp() }
        }
      }
      save {
        assertExists()
        assertIsDisplayed()
        performClick()
      }
    }
    UserFirebaseConnection.updateUserInterests(
        MainActivity.uid, Profile(enumValues<Interests>().toSet()))
    runTest {
      async {
            val user = UserFirebaseConnection.fetchUser(MainActivity.uid)
            assert(user != null)
            assert(user!!.uid == MainActivity.uid)
            assert(user.username == USERNAME)
            assert(user.email == EMAIL)
            assert(user.password == PASSWORD)
            assert(user.profile.interests == enumValues<Interests>().toSet())
          }
          .await()
    }
    UserFirebaseConnection.deleteUser(MainActivity.uid)
    UserFirebaseConnection.deleteCurrentUser()
  }
}
