package com.github.se.gatherspot.model.chat

import com.github.se.gatherspot.CollectionClass
import com.google.firebase.Timestamp

class Message(
    override val id: String,
    val senderID: String,
    val content: String,
    val timestamp: Timestamp,
    var read: Boolean? = false
) : CollectionClass() {

  fun markAsRead() {
    read = true
  }

  fun markAsUnread() {
    read = false
  }
}
