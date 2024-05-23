package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginUID
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.screens.FollowListScreen
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScaffold
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FollowListTest {
  @get:Rule val composeTestRule = createComposeRule()
  val fb = ProfileFirebaseConnection()
  val loggedInProfile = Profile("neverDeleted", "I am never deleted", "", testLoginUID, setOf())
  val profileTest =
      // Profile("TEST", "Here for testing purposes", "", "TEST", setOf(Interests.FOOTBALL))
      loggedInProfile
  val profileTest2 = Profile("TEST2", "Me too", "", "TEST2", setOf(Interests.NIGHTLIFE))

  @Before
  fun setUp() {
    testLogin()
    fb.add(profileTest)
    fb.add(profileTest2)
    FollowList.follow("TEST", "TEST2")
    FollowList.follow("TEST2", "TEST")
    Thread.sleep(2000)
  }

  @After
  fun clean() {
    FollowList.unfollow(profileTest.id, profileTest2.id)
    FollowList.unfollow(profileTest2.id, profileTest.id)
    fb.delete(profileTest.id)
    fb.delete(profileTest2.id)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testFollower() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "profile") {
        composable("profile") {
          ProfileScaffold(NavigationActions(navController), viewModel<OwnProfileViewModel>())
        }
        composable("followers") {
          FollowListUI(navController, title = "Followers") { FollowList.followers("TEST") }
        }
        composable("following") {
          FollowListUI(navController, title = "Following") { FollowList.following("TEST") }
        }
      }
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

      Thread.sleep(2000)

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
