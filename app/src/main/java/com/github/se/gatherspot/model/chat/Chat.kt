package com.github.se.gatherspot.model.chat

import com.github.se.gatherspot.ChatFirebaseConnection
import com.github.se.gatherspot.CollectionClass
import com.github.se.gatherspot.model.event.Event

data class Chat(
    override val id: String = ChatFirebaseConnection().getNewID(),
    val peopleIDs: List<String>,
    val eventID: String,
    val messages: List<Message>
) : CollectionClass() {

  fun sendMessage(message: Message) {
    ChatFirebaseConnection().addMessage(message, id)
  }

  fun markAllAsRead() {
    messages.forEach { it.markAsRead() }
  }
}
