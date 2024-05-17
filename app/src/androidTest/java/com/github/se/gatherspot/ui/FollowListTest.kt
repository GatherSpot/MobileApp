package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.screens.FollowListScreen
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.utils.MockFollowList
import com.github.se.gatherspot.utils.MockProfileFirebaseConnection
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

class FollowListTest {
  @get:Rule val composeTestRule = createComposeRule()
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testFollower() {

    composeTestRule.setContent {
      val nav = rememberNavController()
      Profile(nav = NavigationActions(nav),MockFollowList(), MockProfileFirebaseConnection())
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      followersButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
    }

    ComposeScreen.onComposeScreen<FollowListScreen>(composeTestRule) {
      back {
        assertIsDisplayed()
        assertHasClickAction()
      }

      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("FollowList"), 10000)

      back { performClick() }
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      followingButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
    }

    ComposeScreen.onComposeScreen<FollowListScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("FollowList"), 10000)
    }
  }
}
