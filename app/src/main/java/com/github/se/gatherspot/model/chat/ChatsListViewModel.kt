package com.github.se.gatherspot.model.chat

import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait

class ChatsListViewModel : ViewModel() {

    val PAGE_SIZE: Long = 9
    private var _uiState = MutableStateFlow(ChatUIState())
    val uiState: StateFlow<ChatUIState> = _uiState

    suspend fun fetchNext(uid: String) {
        val chats = emptySet<String>()
            val profile = ProfileFirebaseConnection().fetch(uid)

                val events = profile?.registeredEvents ?: setOf<String>().union(
                ProfileFirebaseConnection()
                    .fetch(uid)?.organizingEvents?: setOf())
        val newChats = _uiState.value.list.apply { addAll(chats) }
        _uiState.value = ChatUIState(newChats)
    }
}

data class ChatUIState(val list: MutableSet<String> = mutableSetOf())
