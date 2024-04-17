package com.github.se.gatherspot.model.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

  val ProfileFirebaseConnection = ProfileFirebaseConnection()

  val PAGESIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState

  init {
    viewModelScope.launch {
      val nextChats = (ProfileFirebaseConnection.fetch(MainActivity.uid) as Profile).chats
      _uiState.value = ChatUIState(nextChats.map { ChatWithIndicator(it, it.messages.count { message -> message.read == false })}.toMutableSet())
    }
  }

  suspend fun fetchNext() {
    val nextChats = (ProfileFirebaseConnection.fetch(MainActivity.uid) as Profile).chats
    val newChats = _uiState.value.list.apply { addAll(nextChats.map { ChatWithIndicator(it, it.messages.count { message -> message.read == false }) }) }
    _uiState.value = ChatUIState(newChats)
  }
}

data class ChatUIState(val list: MutableSet<ChatWithIndicator> = mutableSetOf())
