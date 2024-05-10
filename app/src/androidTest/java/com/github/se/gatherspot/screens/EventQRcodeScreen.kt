package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventQRcodeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventQRcodeScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("EventQRCode") }) {

  // Structural elements of the UI
  val eventColumn: KNode = onNode { hasTestTag("EventQRCode") }
  val image: KNode = onNode { hasTestTag("QRCodeImage") }
}
