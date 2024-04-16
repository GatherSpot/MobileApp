package com.github.se.gatherspot.model.chat

import com.github.se.gatherspot.ChatFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event

data class Chat(
    val chatID: String = ChatFirebaseConnection.getNewChatID(),
    val people: List<Profile>,
    val event: Event,
    val messages: List<Message>
) {

  fun sendMessage(message: Message) {
    ChatFirebaseConnection.addMessage(message, chatID)
  }

  fun markAllAsRead() {
    messages.forEach { it.markAsRead() }
  }
}
