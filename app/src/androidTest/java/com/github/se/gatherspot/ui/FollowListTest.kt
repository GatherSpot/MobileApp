package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.screens.FollowListScreen
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.ProfileUI
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FollowListTest {
  @get:Rule val composeTestRule = createComposeRule()
  val fb = ProfileFirebaseConnection()

  @Before
  fun setUp() {
    val profileTest =
        Profile("TEST", "Here for testing purposes", "", "TEST", setOf(Interests.FOOTBALL))
    val profileTest2 = Profile("TEST2", "Me too", "", "TEST2", setOf(Interests.NIGHTLIFE))

    fb.add(profileTest)
    fb.add(profileTest2)
    FollowList.follow("TEST", "TEST2")
    FollowList.follow("TEST2", "TEST")
  }

  @After
  fun clean() {
    FollowList.unfollow("TEST", "TEST2")
    FollowList.unfollow("TEST2", "TEST")
    fb.delete("TEST")
    fb.delete("TEST2")
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testFollower() {

    composeTestRule.setContent {
      val nav = rememberNavController()
      ProfileUI(nav = NavigationActions(nav))
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
