package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.UserFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.User
import com.github.se.gatherspot.screens.LoginScreen
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.screens.SignUpScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val USERNAME = "AuthEndToEndTest"
const val EMAIL = "AuthEndToEnd@test.com"
const val PASSWORD = "AuthEndToEndTest,2024;"
private val UserFirebaseConnection = UserFirebaseConnection()

@RunWith(AndroidJUnit4::class)
class AllTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @After
  fun cleanUp() {
    UserFirebaseConnection.delete(MainActivity.uid)
    UserFirebaseConnection.deleteCurrentUser()
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
      lazyColumn {
        assertExists()
        assertIsDisplayed()
      }
      composeTestRule.waitForIdle()

      var c = 0
      for (category in allCategories) {
        category {
          composeTestRule
              .onNodeWithTag("lazyColumn")
              .performScrollToNode(hasTestTag(enumValues<Interests>().toList()[c].toString()))
          assertExists()
          performClick()
          c++
        }
      }

      save {
        assertExists()
        assertIsDisplayed()
        performClick()
      }
    }
    UserFirebaseConnection.updateUserInterests(MainActivity.uid, enumValues<Interests>().toList())
    runTest {
      async {
            val user = UserFirebaseConnection.fetch(MainActivity.uid) as User?
            assert(user != null)
            assert(user!!.id == MainActivity.uid)
            assert(user.username == USERNAME)
            assert(user.email == EMAIL)
            assert(user.password == PASSWORD)
            // assert(user.profile.interests == enumValues<Interests>().toSet())
          }
          .await()
    }
  }
}
