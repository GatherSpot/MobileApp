package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SetUpScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SetUpScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("setUpScreen") }) {

  // Structural elements of the UI
  val save: KNode = child { hasTestTag("saveButton") }
  val emailText: KNode = child { hasTestTag("verifEmailText") }
}
