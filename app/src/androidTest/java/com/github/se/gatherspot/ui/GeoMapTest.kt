package com.github.se.gatherspot.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.model.location.Location
import com.github.se.gatherspot.ui.TopLevelDestinations.GeoMap
import org.junit.Rule
import org.junit.Test

class GeoMapTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun isMapDisplayedTest() {
    val userCoordinates = Location(46.518567549767575, 6.568562923656716, "")
    val interestsCoordinates = emptyList<Location>()
    val modifier = Modifier.fillMaxWidth().height(300.dp).padding(4.dp)

    composeTestRule.setContent { GeoMap(userCoordinates = null, interestsCoordinates) }
    composeTestRule.onNodeWithTag("Google Map").assertExists()
  }

  @Test
  fun isMapDisplayedTest_WithoutParameters() {
    val userCoordinates = null
    val interestsCoordinates = emptyList<Location>()
    val modifier = Modifier.fillMaxWidth().height(300.dp).padding(4.dp)

    composeTestRule.setContent { GeoMap(userCoordinates = null, interestsCoordinates) }
    composeTestRule.onNodeWithTag("Google Map").assertExists()
  }

  @Test
  fun isMapDisplayedTest_WithAllParameters() {
    val userCoordinates = Location(46.518567549767575, 6.568562923656716, "")
    val interestsCoordinates =
        listOf(
            Location(46.52464786510155, 6.575147894055152, "Vortex"),
            Location(46.51878838760822, 6.5619011030383, "IC BC"),
            Location(46.523127173515185, 6.564655092362486, "swiss tech convention center"))
    val modifier = Modifier.fillMaxWidth().height(300.dp).padding(4.dp)

    composeTestRule.setContent { GeoMap(userCoordinates = null, interestsCoordinates) }
    composeTestRule.onNodeWithTag("Google Map").assertExists()
  }

  @Test
  fun isMapDisplayedTest_WithNoUserAndMap() {
    val userCoordinates = null
    val interestsCoordinates =
        listOf(
            Location(46.52464786510155, 6.575147894055152, "Vortex"),
            Location(46.51878838760822, 6.5619011030383, "IC BC"),
            Location(46.523127173515185, 6.564655092362486, "swiss tech convention center"))
    val modifier = Modifier.fillMaxWidth().height(300.dp).padding(4.dp)

    composeTestRule.setContent { GeoMap(userCoordinates = null, interestsCoordinates) }
    composeTestRule.onNodeWithTag("Google Map").assertExists()
  }
}
