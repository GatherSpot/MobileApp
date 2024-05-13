package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EditOwnProfileScreen") }) {
  val edit: KNode = onNode { hasTestTag("edit") }
  val back: KNode = onNode { hasTestTag("back") }
  val cancel: KNode = onNode { hasTestTag("cancel") }
  val save: KNode = onNode { hasTestTag("save") }
  val usernameInput: KNode = onNode { hasTestTag("usernameInput") }
  val bioInput: KNode = onNode { hasTestTag("bioInput") }
  val profileImage: KNode = onNode { hasTestTag("profileImage") }
  val addFriend: KNode = onNode { hasTestTag("addFriend") }
  val follow: KNode = onNode { hasTestTag("follow") }
  val basketball: KNode = onNode { hasTestTag("BASKETBALL") }
}
