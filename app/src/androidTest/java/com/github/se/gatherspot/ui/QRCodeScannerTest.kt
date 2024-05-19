package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.screens.QRCodeScannerScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.qrcode.QRCodeScanner
import com.github.se.gatherspot.ui.qrcode.analyseAppQRCode
import io.github.kakaocup.compose.node.element.ComposeScreen
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QRCodeScannerTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val navigationActions = NavigationActions(rememberNavController())
      QRCodeScanner(navigationActions)
    }
    ComposeScreen.onComposeScreen<QRCodeScannerScreen>(composeTestRule) {
      cameraPermissionButton.assertExists()
      cameraPermissionButton.assertIsDisplayed()
      cameraPreview.assertExists()
      cameraPreview.assertIsDisplayed()
      scaffold.assertExists()
      scaffold.assertIsDisplayed()
      goBackButton.assertExists()
      goBackButton.assertIsDisplayed()
    }
  }

  @Test
  fun testAnalysis() {
    val event = "event/123"
    val profile = "profile/123"
    val invalid = "invalid"
    val invalid2 = "invalid/123"

    val eventWorks = analyseAppQRCode(event)
    assertEquals(eventWorks, "event/123")
    val profileWorks = analyseAppQRCode(profile)
    assertEquals(profileWorks, "viewProfile/123")
    val invalidReturnsEmpty = analyseAppQRCode(invalid)
    assertEquals(invalidReturnsEmpty, "")
    val invalid2ReturnsEmpty = analyseAppQRCode(invalid2)
    assertEquals(invalid2ReturnsEmpty, "")
  }
}
