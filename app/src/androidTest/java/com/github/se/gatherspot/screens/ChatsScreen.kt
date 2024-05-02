package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ChatsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("ChatsScreen") }) {
  val createText: KNode = onNode { hasTestTag("AddChatText") }
  val createMenu: KNode = onNode { hasTestTag("createChatMenu") }
  val eventsList: KNode = onNode { hasTestTag("chatsList") }
  val empty: KNode = onNode { hasTestTag("emptyText") }
}
