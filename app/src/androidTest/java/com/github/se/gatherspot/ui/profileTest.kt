package com.github.se.gatherspot.ui

//    composeTestRule.onNodeWithContentDescription("login").performClick()
//    // click on the profile button
//    composeTestRule.onNodeWithText("Profile").performClick()
// check if everything is there
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.ProfileView
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// NOTE: For ui tests to work, and to make app accessible, please ADD CONTENT DESCRIPTION TO EVERY
// COMPOSE NODE
// adding a text is not enough, as we will probably change theses when internationalizing texts
@RunWith(AndroidJUnit4::class)
class ProfileInstrumentedTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  // for useful documentation on testing compose
  // https://developer.android.com/develop/ui/compose/testing-cheatsheet
  @Test
  fun editableProfileScreenTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      com.github.se.gatherspot.ui.Profile(NavigationActions(navController))
    }
    // check if things are here :
    composeTestRule
      .onNodeWithContentDescription("username")
      .assertExists("username field not found")
    composeTestRule.onNodeWithContentDescription("bio").assertExists("bio field not found")
    composeTestRule.onNodeWithContentDescription("edit").assertExists("edit button not found")
    // check buttons that should not be there yet are not here yet
    composeTestRule.onNodeWithContentDescription("cancel").assertDoesNotExist()
    composeTestRule.onNodeWithContentDescription("save").assertDoesNotExist()
    // press edit button
    composeTestRule.onNodeWithContentDescription("edit").performClick()
    // check if things are here :
    composeTestRule
      .onNodeWithContentDescription("username")
      .assertExists("username field not found")
    composeTestRule.onNodeWithContentDescription("bio").assertExists("bio field not found")
    composeTestRule.onNodeWithContentDescription("cancel").assertExists()
    composeTestRule.onNodeWithContentDescription("save").assertExists()
    composeTestRule.onNodeWithContentDescription("edit").assertDoesNotExist()
    // modify text, press cancel, and verify it didn't change.
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
  fun profileScreenTest() {
    composeTestRule.setContent {
      ProfileView().ProfileScreen(ProfileViewModel(Profile.dummyProfile()))
    }
    // check if things are here :
    composeTestRule
      .onNodeWithContentDescription("username")
      .assertExists("username field not found")
    composeTestRule.onNodeWithContentDescription("bio").assertExists("bio field not found")
    composeTestRule.onNodeWithContentDescription("profile image").assertExists("image not found")
    // check if fields are filled properly
    composeTestRule.onNodeWithContentDescription("username").assert(hasText("John Doe"))
    composeTestRule.onNodeWithContentDescription("bio").assert(hasText("I am not a bot"))
    // next: check image
  }

  @Test
  fun interestsTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      Profile(NavigationActions(navController))
    }
    composeTestRule.onNodeWithText("FOOTBALL").assertDoesNotExist()
    // press edit and add a new interest
    composeTestRule.onNodeWithContentDescription("edit").performClick()
    // check if things are here :
    composeTestRule.onNodeWithText("FOOTBALL").assertExists("FOOTBALL field not found")
    // select football interest and go back to view
    composeTestRule.onNodeWithText("FOOTBALL").performClick()
    composeTestRule.onNodeWithContentDescription("save").performClick()
    // check if things are here :
    composeTestRule.onNodeWithText("FOOTBALL").assertExists("FOOTBALL field not found")
  }
}