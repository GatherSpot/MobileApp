package com.github.se.gatherspot.authentification

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.screens.ProfileScreen
import com.github.se.gatherspot.screens.SetUpScreen
import com.github.se.gatherspot.sql.AppDatabase
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScaffold
import com.github.se.gatherspot.ui.topLevelDestinations.SetUpProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration

@RunWith(AndroidJUnit4::class)
class SetUpTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var db: AppDatabase

  @Before
  fun setUp() = runBlocking {
    testLogin()
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    ProfileFirebaseConnection()
        .add(
            com.github.se.gatherspot.model.Profile(
                userName = "SetUpTest",
                bio = "",
                image = "",
                id = Firebase.auth.uid!!,
                interests = setOf()))
  }

  @After
  fun cleanUp() = runBlocking {
    testLoginCleanUp()
    db.close()
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun setUpTest() = runTest(timeout = Duration.parse("20s")) {
    val string = "123bioText"
    composeTestRule.setContent {
      val navController = rememberNavController()
      val nav = NavigationActions(navController)
      NavHost(navController, startDestination = "setup") {
        composable("home") {
          ProfileScaffold(NavigationActions(navController), viewModel { OwnProfileViewModel(db) })
        }
        composable("setup") { SetUpProfile(nav) }
      }
    }
    ComposeScreen.onComposeScreen<SetUpScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      setUpInterests { assertExists() }
      addBasketball { performClick() }
      removeBasketball { assertExists() }
      next { performClick() }
      composeTestRule.waitForIdle()
      setUpBio { assertExists() }
      bioInput { performTextInput(string) }
      next { performClick() }
      composeTestRule.waitForIdle()
      setUpImage { assertExists() }
      // TODO: maybe add image test when it will be implemented
      next { performClick() }
      composeTestRule.waitForIdle()
      setUpDone { assertExists() }
      composeTestRule.waitUntilAtLeastOneExists(isEnabled())
      done { performClick() }
    }
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      composeTestRule.waitUntilAtLeastOneExists(hasText(string), 10000)
      usernameInput { assertExists() }
    }
  }
}
