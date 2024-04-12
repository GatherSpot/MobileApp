package com.github.se.gatherspot.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.screens.EventUIScreen
import com.google.android.gms.maps.model.LatLng
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

class GeoMapTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun isMapDisplayedTest(){
        val userCoordinates = LatLng(46.518567549767575, 6.568562923656716)
        val interestsCoordinates = emptyList<GeoMapInterestPoint>()
        val modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(4.dp)

        composeTestRule.setContent {
            GeoMap(
                userCoordinates = null,
                interestsCoordinates
            )
        }

        composeTestRule.onNodeWithTag("Google Map").assertExists()
    }

    @Test
    fun areInterestDisplayed(){
        val userCoordinates = LatLng(46.518567549767575, 6.568562923656716)
        val interestCoordinates = listOf(
            GeoMapInterestPoint(LatLng(46.52464786510155, 6.575147894055152), "Vortex"),
            GeoMapInterestPoint(LatLng(46.51878838760822, 6.5619011030383), "IC BC"),
            GeoMapInterestPoint(LatLng(46.523127173515185, 6.564655092362486), "swiss tech convention center")
        )
        val modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(4.dp)

        composeTestRule.setContent {
            GeoMap(
                userCoordinates = null,
                interestCoordinates,
                modifier
            )
        }

        val map =  composeTestRule.onNodeWithTag("Google Map")
    }
}