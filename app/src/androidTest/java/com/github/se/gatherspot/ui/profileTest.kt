package com.github.se.gatherspot.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileInstrumentedTest {
  @Before
  fun setUp() {
    ProfileFirebaseConnection().saveToFirebase(Profile.testOrganizer())
  }

  @After
  fun cleanUp() {
    ProfileFirebaseConnection().deleteFromFirebase("TEST")
  }

  @get:Rule val composeTestRule = createComposeRule()

  // for useful documentation on testing compose
  // https://developer.android.com/develop/ui/compose/testing-cheatsheet
  @Test
  fun editableProfileScreenTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      Profile(NavigationActions(navController))
    }
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      // check if things are here :
      usernameInput { assertExists() }
      bioInput { assertExists() }
      profileImage { assertExists() }
      edit { assertExists() }
      // check buttons that should not be there yet are not here yet
      save { assertDoesNotExist() }
      cancel { assertDoesNotExist() }
      // press edit button
      edit { performClick() }
      // check if things are here :
      usernameInput { assertExists() }
      bioInput { assertExists() }
      profileImage { assertExists() }
      save { assertExists() }
      cancel { assertExists() }
      edit { assertDoesNotExist() }
      // modify text, press cancel, and verify it didn't change.
      usernameInput { performTextReplacement("Alex") }
      bioInput { performTextReplacement("I am a bot") }
      cancel { performClick() }
      // check if things are here :
      usernameInput { assert(hasText("John Doe")) }
      bioInput { assert(hasText("I am not a bot")) }
      // modify text, press save and verify it did change.
      edit { performClick() }
      usernameInput { performTextReplacement("Alex") }
      bioInput { performTextReplacement("I am a bot") }
      save { performClick() }
      bioInput { assert(hasText("I am a bot")) }
      usernameInput { assert(hasText("Alex")) }
    }
  }

  @Test
  fun viewProfileTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      ViewProfile(NavigationActions(navController), "TEST")
    }
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      // check if things are here :
      usernameInput { assertExists() }
      bioInput { assertExists() }
      profileImage { assertExists() }
      edit { assertDoesNotExist() }
      save { assertDoesNotExist() }
      cancel { assertDoesNotExist() }
    }
  }
}
