package com.github.se.gatherspot.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.event.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for the list of chats.
 *
 * @property PAGE_SIZE Number of chats to load at a time.
 * @property _uiState MutableStateFlow<ChatUIState> The current state of the UI.
 * @property eventFirebaseConnection EventFirebaseConnection Connection to the Firebase database for
 *   events.
 */
class ChatsListViewModel : ViewModel() {

  val PAGE_SIZE: Long = 9
  private var _uiState = MutableStateFlow(ChatUIState())
  val uiState: StateFlow<ChatUIState> = _uiState
  private var loadedEvents: MutableList<Event> = mutableListOf()
  private var eventsIDS = listOf<String>()
  val eventFirebaseConnection = EventFirebaseConnection()

  // TEMPORARY
  // val listEvents = listOf("-NwJSmLmQDUlF9booiq7")
  //

  /**
   * Fetches the next set of events from the database.
   *
   * @param uid String? The user ID.
   */
  suspend fun fetchNext(uid: String?) {

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

/**
 * Represents the state of the UI.
 *
 * @property list MutableList<Event> The list of events.
 */
data class ChatUIState(val list: MutableList<Event> = mutableListOf())
