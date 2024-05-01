package com.github.se.gatherspot.model.chat

import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.internal.canParseAsIpAddress

class ChatsListViewModel : ViewModel() {

  val PAGE_SIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState
  private var loadedEvents: MutableList<Event> = mutableListOf()
  private var eventsIDS = listOf<String>()
  val eventFirebaseConnection = EventFirebaseConnection()

    //TEMPORARY
    //val listEvents = listOf("-NwJSmLmQDUlF9booiq7")
    //
  suspend fun fetchNext(uid: String) {

    val idlist = IdListFirebaseConnection().fetchFromFirebase(uid, FirebaseCollection.REGISTERED_EVENTS) {}

     eventsIDS = idlist.events

      _uiState.value = ChatUIState(loadedEvents)

    val events = eventFirebaseConnection.fetchNextEvents(uid, PAGE_SIZE)
      loadedEvents.addAll(events)
      _uiState.value = ChatUIState(loadedEvents)

  }
}

data class ChatUIState(val list: MutableList<Event> = mutableListOf())
