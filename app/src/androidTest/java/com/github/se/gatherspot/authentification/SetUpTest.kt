package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.ui.SetUpProfile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetUpTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun before() {
    ProfileFirebaseConnection().add(Profile("John Doe", "", "", "TEST", Interests.new()))
  }

  @After
  fun after() {
    ProfileFirebaseConnection().delete("TEST")
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun setUp() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "setUp") {
        navigation(route = "setUp", startDestination = "setUpProfile") {
          composable("setUpProfile") { SetUpProfile(NavigationActions(navController)) }
          composable("home") {
            com.github.se.gatherspot.ui.Profile(NavigationActions(navController))
          }
        }
      }
    }

    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      setUpInterests { assertExists() }
      addBasketball {
        assertExists()
        performClick()
      }
      next {
        assertExists()
        performClick()
      }
      setUpBio { assertExists() }
      bioInput {
        assertExists()
        performTextInput("I love basketball")
      }
      next {
        assertExists()
        performClick()
      }
      setUpImage { assertExists() }
      next {
        assertExists()
        performClick()
      }
      done {
        assertExists()
        performClick()
      }
      var lock = true
      val profile = ProfileFirebaseConnection().fetch("TEST") { lock = false }
      // wait for fetch and check if the profile has the good values
      while (lock) {
        {}
      }
      assert(profile.userName == "John Doe")
      assert(profile.bio == "I love basketball")
      assert(profile.interests.contains(Interests.BASKETBALL))
    }
  }
}
