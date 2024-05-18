package com.github.se.gatherspot.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.event.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatsListViewModel : ViewModel() {

  val PAGE_SIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState
  private var loadedEvents: MutableList<Event> = mutableListOf()
  private var eventsIDS = listOf<String>()
  val eventFirebaseConnection = EventFirebaseConnection()

  suspend fun fetchNext(uid: String?) {

    if (loadedEvents.size <= PAGE_SIZE && loadedEvents.size > 0) {
      return
    }

    if (uid == null) {
      return
    }
    val idlist = IdList.fromFirebase(uid, FirebaseCollection.REGISTERED_EVENTS) {}

    eventsIDS = idlist.elements
    Log.e("IDS", eventsIDS.toString())

    _uiState.value = ChatUIState(loadedEvents)

    val events = eventFirebaseConnection.fetchNextEvents(idlist, PAGE_SIZE)
    loadedEvents.addAll(events)
    _uiState.value = ChatUIState(loadedEvents)
  }
}

data class ChatUIState(val list: MutableList<Event> = mutableListOf())
