package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.screens.ViewOwnProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileView
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

class ViewOwnProfileTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testComponentsExist() {
    composeTestRule.setContent {
      val nav = rememberNavController()
      ProfileView().ViewOwnProfile(NavigationActions(nav), OwnProfileViewModel(), nav)
    }
    ComposeScreen.onComposeScreen<ViewOwnProfileScreen>(composeTestRule) {
      scaffold.assertExists()
      content.assertExists()
      edit.assertExists()
      columnViewOwnContent.assertExists()
      imageColumn.assertExists()
      profileImage.assertExists()
      editProfilePictureText.assertDoesNotExist()
      interestsShow.assertExists()
      usernameInput.assertExists()
      bioInput.assertExists()

      scaffold.assertIsDisplayed()
      content.assertIsDisplayed()
      edit.assertIsDisplayed()
      columnViewOwnContent.assertIsDisplayed()
      imageColumn.assertIsDisplayed()
      profileImage {
        performScrollTo()
        assertIsDisplayed()
      }
      editProfilePictureText.assertIsNotDisplayed()
      interestsShow {
        performScrollTo()
        assertIsDisplayed()
      }
      usernameInput {
        performScrollTo()
        assertIsDisplayed()
      }
      bioInput {
        performScrollTo()
        assertIsDisplayed()
      }
    }
  }
}
