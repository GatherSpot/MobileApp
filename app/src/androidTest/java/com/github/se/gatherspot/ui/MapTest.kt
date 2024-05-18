package com.github.se.gatherspot.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.screens.MapScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.topLevelDestinations.Map
import com.google.android.gms.maps.model.LatLng
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {
  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun TestExistence() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        navigation(startDestination = "map", route = "home") {
          composable("map") { Map(NavigationActions(navController), LatLng(0.0, 0.0)) }
        }
      }
    }
    ComposeScreen.onComposeScreen<MapScreen>(composeTestRule) {
      // wait for update
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("GoogleMap"), 10000)
      // check if things are here :
      googleMap {
        assertExists()
        assertIsDisplayed()
      }
      positionButton {
        assertExists()
        assertIsDisplayed()
        assertHasClickAction()
      }
      topBar {
        assertExists()
        assertIsDisplayed()
      }
    }
  }
}
