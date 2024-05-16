package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EditOwnProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditOwnProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EditOwnProfileScaffold") }) {
  val scaffold: KNode = onNode { hasTestTag("EditOwnProfileScaffold") }
  val content: KNode = onNode { hasTestTag("EditOwnProfileContent") }
  val saveCancelButtons = onNode { hasTestTag("SaveCancelButtons") }
  val columnEditOwnContent = onNode { hasTestTag("columnEditOwnContent") }
  val profileImage = onNode { hasTestTag("profileImage") }
  val editProfilePictureText = onNode { hasTestTag("editProfilePictureText") }
  val interestsShow = onNode { hasTestTag("interestsEdit") }
  val usernameInput = onNode { hasTestTag("usernameInput") } // Contains text test
  val bioInput = onNode { hasTestTag("bioInput") } // Contains text test
}
