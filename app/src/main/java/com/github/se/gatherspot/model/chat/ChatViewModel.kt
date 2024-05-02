package com.github.se.gatherspot.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.ChatMessagesFirebaseConnection
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(val eventId: String) : ViewModel() {
  val chatMessagesFirebase = ChatMessagesFirebaseConnection()
  private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val messages: StateFlow<List<ChatMessage>> = _messages
  var event: Event? = null

  init {
    fetchMessagesAndEvent()
    listenToMessages()
  }

  private fun fetchMessagesAndEvent() {
    viewModelScope.launch {
      try {
        val fetchedMessages =
            withContext(Dispatchers.IO) {
              chatMessagesFirebase.fetchMessages(
                  eventId,
                  100) // Fetch the latest 100 messages since fetching every x messages does not
              // work with listeners
            }
        val eventValue = withContext(Dispatchers.IO) { EventFirebaseConnection().fetch(eventId) }
        _messages.value = fetchedMessages
        event = eventValue
      } catch (e: Exception) {
        // Handle exceptions
        Log.w("ChatViewModel", "Failed to fetch messages", e)
      }
    }
  }

  fun listenToMessages() {
    FirebaseFirestore.getInstance()
        .collection(chatMessagesFirebase.CHATS)
        .document(eventId)
        .collection(chatMessagesFirebase.MESSAGES)
        .orderBy("timestamp")
        .addSnapshotListener { snapshot, e ->
          if (e != null) {
            Log.w("ChatViewModel", "Listen failed.", e)
            return@addSnapshotListener
          }

          val fetchedMessages = mutableListOf<ChatMessage>()
          snapshot?.documents?.forEach { document ->
            document.let { fetchedMessages.add(chatMessagesFirebase.getFromDocument(it)!!) }
          }
          _messages.value = fetchedMessages
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
