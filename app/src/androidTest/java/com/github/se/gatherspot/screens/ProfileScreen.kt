package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EditOwnProfileScreen") }) {
  // Structural elements of the UI
  val edit: KNode = onNode { hasTestTag("edit") }
  val cancel: KNode = onNode { hasTestTag("cancel") }
  val save: KNode = onNode { hasTestTag("save") }
  val usernameInput: KNode = onNode { hasTestTag("usernameInput") }
  val bioInput: KNode = onNode { hasTestTag("bioInput") }
  val profileImage: KNode = onNode { hasTestTag("profileImage") }
  val addBasketball: KNode = onNode { hasTestTag("add BASKETBALL") }
  val removeBasketball: KNode = onNode { hasTestTag("remove BASKETBALL") }
  val basketball: KNode = onNode { hasTestTag("BASKETBALL") }
  val football: KNode = onNode { hasTestTag("FOOTBALL") }
}
