package com.github.se.gatherspot.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ChatMessagesScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatMessagesScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("ChatUIScreen") }) {
  val chatScaffold: KNode = onNode { hasTestTag("ChatUIScreen") }
  val messagesList: KNode = onNode { hasTestTag("messagesList") }
  val spacer: KNode = onNode { hasTestTag("spacer") }
  val boxChatMessageCard: KNode = onNode { hasTestTag("boxChatMessageCard") }
  val chatMessageCard: KNode = onNode { hasTestTag("chatMessageCard") }
  val chatTopBar: KNode = onNode { hasTestTag("chatTopBar") }
  val inputMessage: KNode = onNode { hasTestTag("inputMessage") }
  val sendButton: KNode = onNode { hasTestTag("sendButton") }
}
