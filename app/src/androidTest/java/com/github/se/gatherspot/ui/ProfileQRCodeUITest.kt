package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.defaults.DefaultProfiles
import com.github.se.gatherspot.screens.ProfileQRCodeScreen
import com.github.se.gatherspot.ui.qrcode.ProfileQRCodeUI
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileQRCodeUITest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val profile = DefaultProfiles.trivial
      ProfileQRCodeUI(profile = profile)
    }
    ComposeScreen.onComposeScreen<ProfileQRCodeScreen>(composeTestRule) {
      column.assertExists()
      column.assertIsDisplayed()
      image.assertExists()
      image.assertIsDisplayed()
    }
  }
}
