package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.profileFirebaseConnection
import com.github.se.gatherspot.EnvironmentSetter.Companion.testDelete
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.screens.LoginScreen
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.screens.SignUpScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


val USERNAME = "AuthEndToEndTest" + java.util.Date().time.toString()
val EMAIL = "AuthEndToEnd@test.com" + java.util.Date().time.toString()
const val PASSWORD = "AuthEndToEndTest,2024;"

@RunWith(AndroidJUnit4::class)
class AllTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @After
  fun cleanUp() {
    try {
      runBlocking {
        ProfileFirebaseConnection().delete(FirebaseAuth.getInstance().currentUser!!.uid)
      }
      testDelete()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @Before
  fun Setup() {
    runBlocking {
      val toDelete = async { profileFirebaseConnection.fetchFromUserName(USERNAME) }.await()
      if (toDelete != null) profileFirebaseConnection.delete(toDelete.id)

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

    runBlocking {
      ProfileFirebaseConnection()
          .update(
              FirebaseAuth.getInstance().currentUser!!.uid,
              "interests",
              enumValues<Interests>().toList())
    }
    runTest {
      async {
            val profile =
                ProfileFirebaseConnection().fetch(FirebaseAuth.getInstance().currentUser!!.uid)
            assertNotNull(profile)
            assertEquals(profile.id, FirebaseAuth.getInstance().currentUser!!.uid)
            assertEquals(USERNAME, profile.userName)
            assertEquals(EMAIL.lowercase(), FirebaseAuth.getInstance().currentUser?.email)
          }
          .await()
    }
  }
}
