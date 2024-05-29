package com.github.se.gatherspot.model.chat

import android.content.ContentValues.TAG
import android.util.Log
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
  private var _isSearchEnabled = MutableLiveData(false)
  val isSearchEnabled: LiveData<Boolean> = _isSearchEnabled
  private var _allEvents = MutableLiveData<List<Event>>(listOf())
  val allEvents: LiveData<List<Event>> = _allEvents
  private val eventFirebaseConnection = EventFirebaseConnection()
  private var listOfList: MutableList<List<String>> = mutableListOf()
  private var eventsCopy: List<Event> = listOf()
  private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "None"

  init {
    fetchNextEvents()
  }

  /** Fetches the next set of events from the database. */
  fun fetchNextEvents() {
    viewModelScope.launch {
      val eventIds = IdList.fromFirebase(uid, FirebaseCollection.REGISTERED_EVENTS)
      Log.d(TAG, "from vm of interest $eventIds")
      val events = eventFirebaseConnection.fetchNextEvents(eventIds, PAGE_SIZE)
      eventsCopy = eventsCopy.plus(events)
      _allEvents.value = eventsCopy
      listOfList = mutableListOf()
      eventsCopy.forEach { event ->
        listOfList.add(event.title.split(" ").map { s -> s.lowercase() })
      }
      _isSearchEnabled.value = true
    }
  }

  /** Reset offset when a user wants to refresh */
  fun resetOffset() {
    _isSearchEnabled.value = false
    eventFirebaseConnection.offset = null
    eventsCopy = emptyList()
    _allEvents.value = listOf()
    fetchNextEvents()
  }

  /**
   * Filter events with title including a word starting with filter or the inverse
   *
   * @param filter: String to apply
   */
  fun filter(filter: String) {
    val filteredEvents = mutableListOf<Event>()
    listOfList.forEachIndexed { index, l ->
      if (l.any { s -> s.startsWith(filter.lowercase()) || filter.lowercase().startsWith(s) }) {
        filteredEvents.add(eventsCopy[index])
      }
    }
    _allEvents.value = filteredEvents
  }
}
