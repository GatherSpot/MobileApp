package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScaffold
import com.github.se.gatherspot.ui.profile.ProfileScreen
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.lang.Thread.sleep
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileInstrumentedTest {

  @get:Rule val composeTestRule = createComposeRule()

  // for useful documentation on testing compose
  // https://developer.android.com/develop/ui/compose/testing-cheatsheet
  @Before
  fun setUp() = runBlocking {
    testLogin()
    ProfileFirebaseConnection().add(com.github.se.gatherspot.model.Profile.testOrganizer())
    ProfileFirebaseConnection().add(com.github.se.gatherspot.model.Profile.testParticipant())
    IdListFirebaseConnection().delete(
        "TEST", com.github.se.gatherspot.firebase.FirebaseCollection.FOLLOWING) {}
  }

  // For now on this branch, we will not test the profile screen because it does not pass the CI

  @OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
  @Test
  fun editableProfileScreenTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      ProfileScaffold(NavigationActions(navController), viewModel { OwnProfileViewModel() })
    }
    sleep(6000) // forced to use sleep when coroutines are there as waituntil seems undeterministic.
    val original_username = "testOrganizer"
    val original_bio = "Bio"
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
      usernameInput { performTextReplacement("Alex") }
      bioInput { performTextReplacement("I am a bot") }
      Espresso.closeSoftKeyboard()
      composeTestRule.waitForIdle()
      cancel { performClick() }
      composeTestRule.waitForIdle()
      // check if things are here :
      usernameInput { hasText(original_username) }
      bioInput { hasText(original_bio) }
      // modify text, press save and verify it did change.
      edit { performClick() }
      bioInput { performTextReplacement("I am a bot") }
      save { performClick() }
      bioInput { assert(hasText("I am a bot")) }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun viewProfileTest() {
    testLogin()
    composeTestRule.setContent {
      val navController = rememberNavController()
      ProfileScreen(viewModel { ProfileViewModel("TEST", navController) })
    }
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      // wait for update :
      sleep(6000)
      usernameInput { assertExists() }
      bioInput { assertExists() }
      profileImage { assertExists() }
      edit { assertDoesNotExist() }
      save { assertDoesNotExist() }
      cancel { assertDoesNotExist() }
      follow { hasText("Follow") }
      addFriend { assertExists() }
      follow { performClick() }
      sleep(6000)
      follow { hasText("Unfollow") }
    }
  }
}
