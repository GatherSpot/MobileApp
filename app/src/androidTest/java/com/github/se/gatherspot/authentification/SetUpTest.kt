package com.github.se.gatherspot.authentification

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetUpTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()



  @Before
  fun before() {
    var lock = true
    ProfileFirebaseConnection().add(Profile("John Doe", "", "", "TEST", Interests.new())) {
      lock = false
    }
    while (lock) {
      {}
    }
  }

  @Test
  fun testScreens(){
    composeTestRule.setContent {
      val navController = rememberNavController()
      val nav = NavigationActions(navController)
      val navHostViewModelStoreOwner = LocalViewModelStoreOwner.current!!
      val viewModel = viewModel<SetUpViewModel>(viewModelStoreOwner = navHostViewModelStoreOwner)
      NavHost(navController, startDestination = "Interests") {
        composable("Interests") { SetUpInterests(viewModel, nav, "Bio") }
        composable("Bio") { SetUpBio(viewModel, nav, "Image") }
        composable("Image") { SetUpImage(viewModel, nav, "Done") }
        composable("Done") { SetUpDone(viewModel, nav, "Interests") }
      }}
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule){
      setUpInterests { assertExists() }
      addBasketball { performClick() }
      removeBasketball { assertExists() }
      next { performClick() }
      setUpBio { assertExists() }
      bioInput { performTextInput("I like haskell")}
      next { performClick() }
      setUpImage { assertExists() }
      next { performClick() }
      done { performClick() }

    }
    var lock = true
    val profile = ProfileFirebaseConnection().fetch("TEST") { lock = false }
    while (lock) {
      {}
    }
    assert(profile.bio == "I like haskell")
    assert(profile.interests.contains(Interests.BASKETBALL))
  }

}
