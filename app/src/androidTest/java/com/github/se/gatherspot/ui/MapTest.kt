package com.github.se.gatherspot.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.model.MapViewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    MainActivity.mapAccess = true
  }

  @Composable
  @Test
  fun TestMap() {
    val nav = NavigationActions(rememberNavController())
    composeTestRule.setContent { Map(MapViewModel(MainActivity().application), nav) }
  }
}
