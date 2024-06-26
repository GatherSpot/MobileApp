package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EditProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EditOwnProfileScreen") }) {
  // Structural elements of the UI
  val edit: KNode = onNode { hasTestTag("edit") }
  val back: KNode = onNode { hasTestTag("back") }
  val cancel: KNode = onNode { hasTestTag("cancel") }
  val save: KNode = onNode { hasTestTag("save") }
  val usernameInput: KNode = onNode { hasTestTag("usernameInput") }
  val bioInput: KNode = onNode { hasTestTag("bioInput") }
  val profileImage: KNode = onNode { hasTestTag("profileImage") }
  val follow: KNode = onNode { hasTestTag("follow") }
  val followersButton: KNode = onNode { hasTestTag("followersButton") }
  val followingButton: KNode = onNode { hasTestTag("followingButton") }
}
