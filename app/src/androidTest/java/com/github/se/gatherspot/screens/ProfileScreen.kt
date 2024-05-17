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
  val back: KNode = onNode { hasTestTag("back") }
  val edit: KNode = onNode { hasTestTag("edit") }
  val save: KNode = onNode { hasTestTag("save") }
  val cancel: KNode = onNode { hasTestTag("cancel") }
  val usernameInput: KNode = onNode { hasTestTag("usernameInput") }
  val bioInput: KNode = onNode { hasTestTag("bioInput") }
  val profileImage: KNode = onNode { hasTestTag("profileImage") }
  val follow: KNode = onNode { hasTestTag("follow") }
  val unfollow: KNode = onNode { hasTestTag("unfollow") }
  val addFriend: KNode = onNode { hasTestTag("addFriend") }
  val removeFriend: KNode = onNode { hasTestTag("removeFriend") }
}
