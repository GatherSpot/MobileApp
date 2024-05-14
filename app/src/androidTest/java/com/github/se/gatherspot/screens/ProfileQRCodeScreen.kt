package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ProfileQRCodeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ProfileQRCodeScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("ProfileQRCode") }) {

  // Structural elements of the UI
  val column: KNode = onNode { hasTestTag("ProfileQRCode") }
  val image: KNode = onNode { hasTestTag("QRCodeImage") }
}
