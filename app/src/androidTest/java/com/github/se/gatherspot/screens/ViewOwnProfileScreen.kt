package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ViewOwnProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ViewOwnProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("ViewOwnProfileScaffold") }) {
  val scaffold: KNode = onNode { hasTestTag("ViewOwnProfileScaffold") }
  val content: KNode = onNode { hasTestTag("ProfileScreen") }
  val edit: KNode = onNode { hasTestTag("edit") }
  val columnViewOwnContent = onNode { hasTestTag("columnViewOwnContent") }
  val imageColumn = onNode { hasTestTag("imageColumn") }
  val profileImage = onNode { hasTestTag("profileImage") }
  val editProfilePictureText = onNode { hasTestTag("editProfilePictureText") }
  val interestsShow = onNode { hasTestTag("interestsShow") }
  val usernameInput = onNode { hasTestTag("usernameInput") } // Contains text test
  val bioInput = onNode { hasTestTag("bioInput") } // Contains text test
}
