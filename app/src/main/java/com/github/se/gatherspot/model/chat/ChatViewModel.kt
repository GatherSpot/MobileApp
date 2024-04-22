package com.github.se.gatherspot.model.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.ChatFirebaseConnection
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

  val PAGE_SIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState


    // This init block needs to be deleted in real use!!!
    // This is only to simulate the data fetching before the user class is well implemented in code
    // and in Firebase
init {
    viewModelScope.launch {
        val chats =
            listOf(
                ChatFirebaseConnection().fetch("-NvgK6Aqo7lV01S27jVp")!!,
                ChatFirebaseConnection().fetch("-NvgLLs9rCbpvRhOeOfx")!!,
                ChatFirebaseConnection().fetch("-NvgLa_oDM6QcBPzsHFo")!!,
            )

        val newChats =
            _uiState.value.list.apply {
                addAll(
                    chats.map {
                        ChatWithIndicator(
                            it,
                            it.messages.count { message -> message.read == false })
                    })
            }
        _uiState.value = ChatUIState(newChats)
    }
}

  suspend fun fetchNext() {

    val chats = ChatFirebaseConnection().fetchNextChats(PAGE_SIZE)
    val newChats =
        _uiState.value.list.apply {
          addAll(
              chats.map {
                ChatWithIndicator(it, it.messages.count { message -> message.read == false })
              })
        }
    _uiState.value = ChatUIState(newChats)
  }
}

data class ChatUIState(val list: MutableSet<ChatWithIndicator> = mutableSetOf())
