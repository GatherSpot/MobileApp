package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class NFCScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<NFCScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("NFCScreen") }) {
  val nfcScreen = onNode { hasTestTag("nfc") }
  val text = onNode { hasTestTag("text") }
}
