package com.github.se.gatherspot.model.chat

import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatsListViewModel : ViewModel() {

  val PAGE_SIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState

  suspend fun fetchNext(uid: String) {

      val profile : Profile?
    try {
        profile = ProfileFirebaseConnection().fetch(uid)
    }
    catch (e: Exception) {
        println("Error fetching chats: $e")
        return
    }
    val chats =
        profile?.registeredEvents
            ?: setOf<String>()
                .union(ProfileFirebaseConnection().fetch(uid)?.organizingEvents ?: setOf())
    val newChats = _uiState.value.list.apply { addAll(chats) }
    _uiState.value = ChatUIState(newChats)
  }
}

data class ChatUIState(val list: MutableSet<String> = mutableSetOf())
