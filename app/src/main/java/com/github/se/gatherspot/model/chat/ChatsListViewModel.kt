package com.github.se.gatherspot.model.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.event.Event
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * ViewModel for the list of chats.
 *
 * @property PAGE_SIZE Number of chats to load at a time.
 * @property _uiState MutableStateFlow<ChatUIState> The current state of the UI.
 * @property eventFirebaseConnection EventFirebaseConnection Connection to the Firebase database for
 *   events.
 */
class ChatsListViewModel : ViewModel() {

  private val PAGE_SIZE: Long = 9
  private var _allEvents = MutableLiveData<List<Event>>(listOf())
  val allEvents: LiveData<List<Event>> = _allEvents
  private val eventFirebaseConnection = EventFirebaseConnection()
  private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "None"

  init {
    fetchNextEvents()
  }

  /** Fetches the next set of events from the database. */
  fun fetchNextEvents() {
    viewModelScope.launch {
      val eventIds = IdList.fromFirebase(uid, FirebaseCollection.REGISTERED_EVENTS) {}
      val events = eventFirebaseConnection.fetchNextEvents(eventIds, PAGE_SIZE)
      val all = _allEvents.value!!.plus(events)
      _allEvents.postValue(all)
    }
  }

  /** Reset offset when a user wants to refresh */
  fun resetOffset() {
    eventFirebaseConnection.offset = null
    _allEvents.postValue(listOf())
    fetchNextEvents()
  }
}

/**
 * Represents the state of the UI.
 *
 * @property list MutableList<Event> The list of events.
 */
data class ChatUIState(val list: MutableList<Event> = mutableListOf())
