package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.screens.EditOwnProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileView
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

class EditOwnProfileTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testComponentsExist() {
    composeTestRule.setContent {
      val nav = rememberNavController()
      val model = OwnProfileViewModel()
      ProfileView().EditOwnProfile(NavigationActions(nav), model, nav)
    }

    ComposeScreen.onComposeScreen<EditOwnProfileScreen>(composeTestRule) {
      scaffold.assertExists()
      content.assertExists()
      saveCancelButtons.assertExists()
      columnEditOwnContent.assertExists()
      profileImage.assertExists()
      editProfilePictureText.assertExists()
      interestsShow.assertExists()
      usernameInput.assertExists()
      bioInput.assertExists()

      scaffold.assertIsDisplayed()
      content.assertIsDisplayed()
      saveCancelButtons.assertIsDisplayed()
      columnEditOwnContent.assertIsDisplayed()
      profileImage {
        performScrollTo()
        assertIsDisplayed()
      }
      editProfilePictureText.assertIsDisplayed()
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
