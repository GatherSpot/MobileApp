package com.github.se.gatherspot.model.chat

import java.time.LocalDateTime

/**
 * Represents a message in a chat.
 *
 * @property id ID of the message.
 * @property senderId ID of the sender of the message.
 * @property eventId ID of the event the message is associated with.
 * @property message Content of the message.
 * @property timestamp Time the message was sent.
 */
data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val eventId: String = "",
    val message: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now()
)
