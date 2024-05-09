// package com.github.se.gatherspot.ui
//
// import android.app.Application
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.test.ExperimentalTestApi
// import androidx.compose.ui.test.hasTestTag
// import androidx.compose.ui.test.junit4.createAndroidComposeRule
// import androidx.compose.ui.test.junit4.createComposeRule
// import androidx.navigation.compose.NavHost
// import androidx.navigation.compose.composable
// import androidx.navigation.compose.rememberNavController
// import androidx.navigation.navigation
// import androidx.test.espresso.intent.rule.IntentsTestRule
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.github.se.gatherspot.MainActivity
// import com.github.se.gatherspot.model.MapViewModel
// import com.github.se.gatherspot.screens.MapScreen
// import com.github.se.gatherspot.ui.navigation.NavigationActions
// import com.google.firebase.Firebase
// import com.google.firebase.auth.auth
// import io.github.kakaocup.compose.node.element.ComposeScreen
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.runner.RunWith
//
// @RunWith(AndroidJUnit4::class)
// class MapTest {
//  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
//  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)
//
//  @OptIn(ExperimentalTestApi::class)
//  @Composable
//  @Test
//  fun TestExistence() {
//
//    composeTestRule.setContent {
//      val navController = rememberNavController()
//      NavHost(navController = navController, startDestination = "home") {
//        navigation(startDestination = "map", route = "home") {
//          composable("map") {
//            Map(NavigationActions(navController), MapViewModel(application = MainActivity.app))
//          }
//        }
//      }
//    }
//    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
//      // wait for update
//      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("map"), 6000)
//      // check if things are here :
//      googleMap {
//       assertExists()
//       assertIsDisplayed()}
//    }
//  }
// }
