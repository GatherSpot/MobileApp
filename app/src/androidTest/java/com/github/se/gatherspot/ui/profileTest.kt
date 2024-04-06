package com.github.se.gatherspot.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.data.Profile
import com.github.se.gatherspot.ui.profile.OwnProfile
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScreen
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// NOTE: For ui tests to work, and to make app accessible, please ADD CONTENT DESCRIPTION TO EVERY
// COMPOSE NODE
// adding a text is not enough, as we will probably change theses when internationalizing texts
@RunWith(AndroidJUnit4::class)
class ProfileInstrumentedTest {
  // to start the main activity
  private var profile = Profile("John Doe", "I am not a bot", "")
  private val getProfile = {profile}
  private val saveProfile = { username: String, bio: String, imageUri: String -> profile = Profile(username, bio, imageUri)}

  @get:Rule val composeTestRule = createComposeRule()
  //for usefull documentation on testing compose
  // https://developer.android.com/develop/ui/compose/testing-cheatsheet
  @Test
  fun editableProfileScreenTest() {
    composeTestRule.setContent {
      OwnProfile (OwnProfileViewModel())
    }
//    // inject a dummyLogin or this will not work
//    composeTestRule.onNodeWithContentDescription("login").performClick()
//    // click on the profile button
//    composeTestRule.onNodeWithText("Profile").performClick()
    // check if everything is there
    composeTestRule
        .onNodeWithContentDescription("username")
        .assertExists("username field not found")
    composeTestRule.onNodeWithContentDescription("bio").assertExists("bio field not found")
    composeTestRule.onNodeWithContentDescription("edit").assertExists("edit button not found")
    // check buttons that should not be there yet are not here yet
    composeTestRule.onNodeWithContentDescription("cancel").assertDoesNotExist()
    composeTestRule.onNodeWithContentDescription("save").assertDoesNotExist()
    // press edit button and check the buttons change accordingly
    composeTestRule.onNodeWithContentDescription("edit").performClick()
    composeTestRule.onNodeWithContentDescription("cancel").assertExists()
    composeTestRule.onNodeWithContentDescription("save").assertExists()
    composeTestRule.onNodeWithContentDescription("edit").assertDoesNotExist()
    // modify text, press cancel, and verify it didn't change.
    val defaultProfile = Profile()
    composeTestRule.onNodeWithContentDescription("username").performTextReplacement("Alex")
    composeTestRule.onNodeWithContentDescription("username").assert(hasText("Alex"))
    composeTestRule.onNodeWithContentDescription("cancel").performClick()
    composeTestRule.onNodeWithContentDescription("username").assert(hasText("John Doe"))
    // modify text, press save and verify it did change.
    composeTestRule.onNodeWithContentDescription("edit").performClick()
    composeTestRule.onNodeWithContentDescription("bio").performTextReplacement("I like trains")
    composeTestRule.onNodeWithContentDescription("save").performClick()
    composeTestRule.onNodeWithContentDescription("bio").assert(hasText("I like trains"))
  }
  @Test
  fun profileScreenTest(){
    composeTestRule.setContent {
      ProfileScreen(ProfileViewModel(Profile("John Doe", "I am not a bot", "")))
    }
    composeTestRule.onNodeWithContentDescription("username").assertExists("username field not found")
    composeTestRule.onNodeWithContentDescription("bio").assertExists("bio field not found")
    // check buttons that should not be there yet are not here
    composeTestRule.onNodeWithContentDescription("cancel").assertDoesNotExist()
    composeTestRule.onNodeWithContentDescription("save").assertDoesNotExist()
    composeTestRule.onNodeWithContentDescription("edit").assertDoesNotExist()
    // check if everything is there
    composeTestRule.onNodeWithContentDescription("username").assert(hasText("John Doe"))
    composeTestRule.onNodeWithContentDescription("bio").assert(hasText("I am not a bot"))
  }
}