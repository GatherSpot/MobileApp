package com.github.se.gatherspot.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gatherspot.defaults.DefaultEvents
import com.github.se.gatherspot.screens.EventQRcodeScreen
import com.github.se.gatherspot.ui.qrcode.EventQRCodeUI
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventQRCodeUITest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val event = DefaultEvents.trivialEvent1
      EventQRCodeUI(event = event)
    }
    ComposeScreen.onComposeScreen<EventQRcodeScreen>(composeTestRule) {
      eventColumn.assertExists()
      eventColumn.assertIsDisplayed()
      image.assertExists()
      image.assertIsDisplayed()
    }
  }
}
