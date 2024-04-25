package com.github.se.gatherspot.model.chat

import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {

  val PAGE_SIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState

  // This init block needs to be deleted in real use!!!
  // This is only to simulate the data fetching before the user class is well implemented in code
  // and in Firebase
  //  init {
  //    viewModelScope.launch {
  //      val chats =
  //          listOf(
  //              ChatFirebaseConnection().fetch("-NvgK6Aqo7lV01S27jVp")!!,
  //              ChatFirebaseConnection().fetch("-NvgLLs9rCbpvRhOeOfx")!!,
  //              ChatFirebaseConnection().fetch("-NvgLa_oDM6QcBPzsHFo")!!,
  //          )
  //
  //      val newChats =
  //          _uiState.value.list.apply {
  //            addAll(
  //                chats.map {
  //                  ChatWithIndicator(it, it.messages.count { message -> message.read == false })
  //                })
  //          }
  //      _uiState.value = ChatUIState(newChats)
  //    }
  //  }

  suspend fun fetchNext() {
    val chats =
        ProfileFirebaseConnection()
            .fetch(FirebaseAuth.getInstance().currentUser!!.uid)!!
            .registeredEvents
            .union(
                ProfileFirebaseConnection()
                    .fetch(FirebaseAuth.getInstance().currentUser!!.uid)!!
                    .organizingEvents)
    val newChats = _uiState.value.list.apply { addAll(chats) }
    _uiState.value = ChatUIState(newChats)
  }
}

data class ChatUIState(val list: MutableSet<String> = mutableSetOf())
