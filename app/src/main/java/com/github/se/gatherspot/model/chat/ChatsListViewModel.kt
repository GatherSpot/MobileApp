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
