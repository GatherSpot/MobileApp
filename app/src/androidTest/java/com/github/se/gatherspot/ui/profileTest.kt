package com.github.se.gatherspot.ui

//    composeTestRule.onNodeWithContentDescription("login").performClick()
//    // click on the profile button
//    composeTestRule.onNodeWithText("Profile").performClick()
// check if everything is there
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import java.lang.Thread.sleep
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// NOTE: For ui tests to work, and to make app accessible, please ADD CONTENT DESCRIPTION TO EVERY
// COMPOSE NODE
// adding a text is not enough, as we will probably change theses when internationalizing texts
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
    composeTestRule.waitForIdle()
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
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithContentDescription("username").assert(hasText("John Doe"))
    // modify text, press save and verify it did change.
    composeTestRule.onNodeWithContentDescription("edit").performClick()
    composeTestRule.onNodeWithContentDescription("bio").performTextReplacement("I like trains")
    composeTestRule.onNodeWithContentDescription("save").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithContentDescription("bio").assert(hasText("I like trains"))
  }

  @Test
  fun interestsTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      Profile(NavigationActions(navController))
    }
    sleep(5000)
    composeTestRule.onNodeWithContentDescription("BASKETBALL").assertDoesNotExist()
    // press edit and add a new interest
    composeTestRule.onNodeWithContentDescription("edit").performClick()
    composeTestRule.waitForIdle()
    // check if things are here :
    composeTestRule
        .onNodeWithContentDescription("add BASKETBALL", useUnmergedTree = true, substring = true)
        .assertExists("add BASKETBALL field not found")
    composeTestRule
        .onNodeWithContentDescription("FOOTBALL", useUnmergedTree = true, substring = true)
        .assertExists("FOOTBALL field not found")
    // select football interest and go back to view
    composeTestRule.onNodeWithContentDescription("add BASKETBALL").performClick()
    // wait for the animation to finish
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithContentDescription("save").performClick()
    composeTestRule.waitForIdle()
    sleep(2000)
    // check if things are here :
    composeTestRule
        .onNodeWithContentDescription("BASKETBALL", useUnmergedTree = true, substring = true)
        .assertExists("BASKETBALL wasn't added")
  }

  @Test
  fun viewProfileTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      ViewProfile(NavigationActions(navController), "TEST")
    }
    sleep(3000)
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
}
