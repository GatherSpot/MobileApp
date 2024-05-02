package com.github.se.gatherspot.model.chat

import java.time.LocalDateTime

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val eventId: String = "",
    val message: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now()
)
