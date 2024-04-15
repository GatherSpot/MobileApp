package com.github.se.gatherspot.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.ProfileFirebaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

  val PAGESIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState

  init {
    viewModelScope.launch {
      val nextChats = ProfileFirebaseConnection.fetchProfile(MainActivity.uid).chats
      _uiState.value = ChatUIState(nextChats.toMutableSet())
    }
  }

  suspend fun fetchNext() {
    val nextChats = ProfileFirebaseConnection.fetchProfile(MainActivity.uid).chats
    val newEvents = _uiState.value.list.apply { addAll(nextChats) }
    _uiState.value = ChatUIState(newEvents)
  }
}

data class ChatUIState(val list: MutableSet<ChatWithIndicator> = mutableSetOf())
