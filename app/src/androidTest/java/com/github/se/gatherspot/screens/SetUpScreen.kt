package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SetUpScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SetUpScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("setUpScreen") }) {

  // Structural elements of the UI
  val next: KNode = onNode { hasTestTag("nextButton") }
  val done: KNode = onNode { hasTestTag("doneButton") }
  val bio: KNode = onNode { hasTestTag("bio") }
  val setUpInterests: KNode = onNode { hasTestTag("setUpInterests") }
  val setUpBio: KNode = onNode { hasTestTag("setUpBio") }
  val bioInput: KNode = onNode { hasTestTag("bioInput") }
  val addBasketball: KNode = onNode { hasTestTag("add BASKETBALL") }
  val removeBasketball: KNode = onNode { hasTestTag("remove BASKETBALL") }
  val setUpImage: KNode = onNode { hasTestTag("setUpImage") }
}
