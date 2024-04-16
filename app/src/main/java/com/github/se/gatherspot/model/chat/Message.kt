package com.github.se.gatherspot.model.chat

import com.google.firebase.Timestamp

class Message(
    val messageID: String,
    val senderID: String,
    val content: String,
    val timestamp: Timestamp,
    var read: Boolean? = false
) {

  fun markAsRead() {
    read = true
  }

  fun markAsUnread() {
    read = false
  }
}
