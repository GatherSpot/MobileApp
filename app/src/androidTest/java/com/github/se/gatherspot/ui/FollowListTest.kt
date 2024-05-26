package com.github.se.gatherspot.ui

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.screens.FollowListScreen
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.sql.AppDatabase
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScaffold
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FollowListTest {
  @get:Rule val composeTestRule = createComposeRule()
  val fb = ProfileFirebaseConnection()
  private lateinit var db: AppDatabase

  @Before
  fun setUp() = runTest {
    testLogin()
    val profileTest =
        Profile("TEST", "Here for testing purposes", "", "TEST", setOf(Interests.FOOTBALL))
    val profileTest2 = Profile("TEST2", "Me too", "", "TEST2", setOf(Interests.NIGHTLIFE))

    fb.add(profileTest)
    fb.add(profileTest2)
    FollowList.follow("TEST", "TEST2")
    FollowList.follow("TEST2", "TEST")

    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
  }

  @After
  fun clean() = runTest {
    FollowList.unfollow("TEST", "TEST2")
    FollowList.unfollow("TEST2", "TEST")
    fb.delete("TEST")
    fb.delete("TEST2")

    db.close()
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testFollower() {
    testLogin()
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "profile") {
        composable("profile") {
          ProfileScaffold(NavigationActions(navController), OwnProfileViewModel(db))
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
