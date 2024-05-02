package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.setUp.SetUpBio
import com.github.se.gatherspot.ui.setUp.SetUpDone
import com.github.se.gatherspot.ui.setUp.SetUpImage
import com.github.se.gatherspot.ui.setUp.SetUpInterests
import com.github.se.gatherspot.ui.setUp.SetUpViewModel
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.resume

@RunWith(AndroidJUnit4::class)
class SetUpTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() = runBlocking {
    suspendCancellableCoroutine { continuation ->
      ProfileFirebaseConnection().add(Profile.testOrganizer()) { continuation.resume(Unit) }
    }
  }

  @Test
  fun setUpInterestsTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val nav = NavigationActions(navController)
      val navHostViewModelStoreOwner = LocalViewModelStoreOwner.current!!
      val viewModel = viewModel<SetUpViewModel>(viewModelStoreOwner = navHostViewModelStoreOwner)
      NavHost(navController, startDestination = "Interests") {
        composable("Interests") { SetUpInterests(viewModel, nav, "Interests") }
      }
    }
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      setUpInterests { assertExists() }
      addBasketball { performClick() }
      removeBasketball { assertExists() }
      next { performClick() }
    }
  }

    @Test
    fun setUpBioTest() {
      composeTestRule.setContent {
        val navController = rememberNavController()
        val nav = NavigationActions(navController)
        val navHostViewModelStoreOwner = LocalViewModelStoreOwner.current!!
        val viewModel = viewModel<SetUpViewModel>(viewModelStoreOwner =
   navHostViewModelStoreOwner)
        NavHost(navController, startDestination = "bio") {
          composable("bio") { SetUpBio(viewModel, nav, "bio") }
        }
      }
      ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
        setUpBio { assertExists() }
        bioInput { performTextInput("I like haskell") }
        next { performClick() }
      }
    }

  @Test
  fun setUpImageTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val nav = NavigationActions(navController)
      val navHostViewModelStoreOwner = LocalViewModelStoreOwner.current!!
      val viewModel = viewModel<SetUpViewModel>(viewModelStoreOwner = navHostViewModelStoreOwner)
      NavHost(navController, startDestination = "image") {
        composable("image") { SetUpImage(viewModel, nav, "image") }
      }
    }
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      setUpImage { assertExists() }
      next { performClick() }
    }
  }

  @Test
  fun setUpDoneTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val nav = NavigationActions(navController)
      val navHostViewModelStoreOwner = LocalViewModelStoreOwner.current!!
      val viewModel = viewModel<SetUpViewModel>(viewModelStoreOwner = navHostViewModelStoreOwner)
      NavHost(navController, startDestination = "done") {
        composable("done") { SetUpDone(viewModel, nav, "done") }
      }
    }
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      setUpDone { assertExists() }
      done { assertHasClickAction() } // do not click here as it has not valid data
    }
  }
}