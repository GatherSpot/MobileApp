package com.github.se.gatherspot.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.ChatMessagesFirebaseConnection
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val eventId: String) : ViewModel() {
  private val chatMessagesFirebase = ChatMessagesFirebaseConnection()
  private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val messages: StateFlow<List<ChatMessage>> = _messages

  init {
    fetchMessages()
  }

  private fun fetchMessages() {
    viewModelScope.launch {
      try {
        val fetchedMessages =
            withContext(Dispatchers.IO) {
              chatMessagesFirebase.fetchMessages(eventId, 50) // Fetch the latest 50 messages
            }
        _messages.value = fetchedMessages
      } catch (e: Exception) {
        // Handle exceptions
        Log.w("ChatViewModel", "Failed to fetch messages", e)
      }
    }
  }

  fun addMessage(messageId: String, senderId: String, messageText: String) {
    val newMessage =
        ChatMessage(
            id = messageId,
            senderId = senderId,
            eventId = eventId,
            message = messageText,
            timestamp = LocalDateTime.now())
    _messages.value = _messages.value.plus(newMessage)
    viewModelScope.launch {
      try {
        chatMessagesFirebase.addMessage(eventId, newMessage)
      } catch (e: Exception) {
        Log.e("ChatViewModel", "Failed to add message", e)
      }
    }
  }

  fun removeMessage(messageId: String) {
    viewModelScope.launch {
      try {
        _messages.value = _messages.value.filter { it.id != messageId }
        withContext(Dispatchers.IO) { chatMessagesFirebase.removeMessage(eventId, messageId) }
      } catch (e: Exception) {
        Log.e("ChatViewModel", "Failed to remove message", e)
      }
    }
  }
}
