package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.screens.QRCodeScannerScreen
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.qrcode.QRCodeScanner
import io.github.kakaocup.compose.node.element.ComposeScreen
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
}
