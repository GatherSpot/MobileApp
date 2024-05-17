package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("ProfileScreen") }) {
  val followersButton: KNode = onNode { hasTestTag("followersButton") }
  val followingButton: KNode = onNode { hasTestTag("followingButton") }
  val edit: KNode = onNode { hasTestTag("edit") }
  val profileImage: KNode = onNode { hasTestTag("profileImage") }
  val usernameInput: KNode = onNode { hasTestTag("usernameInput") }
  val bioInput: KNode = onNode { hasTestTag("bioInput") }
  val save: KNode = onNode { hasTestTag("save") }
  val cancel: KNode = onNode { hasTestTag("cancel") }
  val follow = onNode { hasTestTag("follow") }
  val addFriend = onNode { hasTestTag("addFriend") }
  val logout = onNode { hasTestTag("logout") }
}
