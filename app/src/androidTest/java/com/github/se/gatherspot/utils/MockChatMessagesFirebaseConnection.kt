package com.github.se.gatherspot.utils

import com.github.se.gatherspot.firebase.ChatMessagesFirebaseConnection
import com.github.se.gatherspot.model.chat.ChatMessage
import java.time.LocalDateTime
class MockChatMessagesFirebaseConnection : ChatMessagesFirebaseConnection() {
  override suspend fun fetchMessages(eventId: String, number: Long): MutableList<ChatMessage> {
    return mutableListOf(
      ChatMessage(
        id = "1",
        senderId = "1",
        eventId = eventId,
        message = "Hello",
        timestamp = LocalDateTime.now()),
      ChatMessage(
        id = "2",
        senderId = "2",
        eventId = eventId,
        message = "Hi",
        timestamp = LocalDateTime.now()),
      ChatMessage(
        id = "3",
        senderId = "1",
        eventId = eventId,
        message = "How are you?",
        timestamp = LocalDateTime.now()))
  }

  /** Adds a chat message under a specific event. */
  override fun addMessage(eventId: String, message: ChatMessage) {
    return
  }

  /** Removes a chat message from a specific event. */
  override fun removeMessage(eventId: String, messageId: String) {
    return
  }
}
