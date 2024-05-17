package com.github.se.gatherspot.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.defaults.DefaultProfiles
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import com.github.se.gatherspot.utils.MockFollowList
import com.github.se.gatherspot.utils.MockProfileFirebaseConnection
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileInstrumentedTest {

  @get:Rule val composeTestRule = createComposeRule()
  private val profile = DefaultProfiles.trivial
  private val newUsername = "Alex"
  private val newBio = "I am a bot"

  @Test
  fun editableProfileScreenTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      Profile(
          NavigationActions(navController),
          MockFollowList(),
          MockProfileFirebaseConnection()
        )
    }
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      // wait for update
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
      usernameInput { performTextReplacement(newUsername) }
      bioInput { performTextReplacement(newBio) }
      cancel { performClick() }
      // check if things are here :
      usernameInput { assert(hasText(profile.userName)) }
      bioInput { assert(hasText(profile.bio)) }
      // modify text, press save and verify it did change.
      edit { performClick() }
      bioInput { performTextReplacement(newBio) }
      save { performClick() }
      bioInput { assert(hasText(newBio)) }
    }
  }

  // For now on this branch, we will not test the profile screen because it does not pass the CI

  @Test
  fun viewProfileTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val string = ""
      val viewModel = viewModel {
        ProfileViewModel(
            string,
            NavigationActions(navController),
            MockProfileFirebaseConnection(),
            MockFollowList())
      }
      ViewProfile(NavigationActions(navController), "TEST2")
    }
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      // wait for update :
      usernameInput { assertExists() }
      bioInput { assertExists() }
      profileImage { assertExists() }
      edit { assertDoesNotExist() }
      save { assertDoesNotExist() }
      cancel { assertDoesNotExist() }
      follow { hasText("Follow") }
      addFriend { assertExists() }
      follow { performClick() }
      follow { hasText("Unfollow") }
    }
  }
}
