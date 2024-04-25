package com.github.se.gatherspot.model.chat

/**
 * Represents a chat between multiple people in an event.
 *
 * @property peopleIDs List of profile IDs in the chat.
 * @property eventID ID of the event the chat is associated with.
 * @property messages List of messages in the chat.
 */
data class Chat(val peopleIDs: List<String>, val eventID: String, val messages: List<ChatMessage>)
