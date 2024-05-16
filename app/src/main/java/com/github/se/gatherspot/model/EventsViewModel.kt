package com.github.se.gatherspot.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.utils.UtilsForTests
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

  val PAGESIZE: Long = 9
  private var _uiState = MutableStateFlow(UIState())
  val uiState: StateFlow<UIState> = _uiState
  private var loadedEvents: MutableList<Event> = mutableListOf()
  private var myEvents: MutableList<Event> = mutableListOf()
  private var registeredTo: MutableList<Event> = mutableListOf()
  private var fromFollowedUsers: MutableList<Event> = mutableListOf()
  private var loadedFilteredEvents: MutableList<Event> = mutableListOf()
  val eventFirebaseConnection = EventFirebaseConnection()
  var previousInterests = mutableListOf<Interests>()

  // This is the id of the of the user logged in by default during tests.

  init {
    viewModelScope.launch {
      val events = eventFirebaseConnection.fetchNextEvents(PAGESIZE)
      loadedEvents = events.toMutableList()
      _uiState.value = UIState(loadedEvents)
    }
  }

  suspend fun fetchMyEvents() {
    myEvents = eventFirebaseConnection.fetchMyEvents()
  }

  suspend fun fetchRegisteredTo() {
    registeredTo = eventFirebaseConnection.fetchRegisteredTo()
  }

  suspend fun fetchEventsFromFollowedUsers() {
    val ids =
        FollowList.following(
            FirebaseAuth.getInstance().currentUser?.uid ?: UtilsForTests.testLoginId)
    Log.d(TAG, "ids from viewModel ${ids.events}")
    fromFollowedUsers = eventFirebaseConnection.fetchEventsFromFollowedUsers(ids.events)
  }

  fun displayMyEvents() {
    _uiState.value = UIState(myEvents)
  }

  fun displayRegisteredTo() {
    _uiState.value = UIState(registeredTo)
  }

  fun displayEventsFromFollowedUsers() {
    _uiState.value = UIState(fromFollowedUsers)
  }

  fun updateNewRegistered(event: Event) {
    updateLoaded(event)
    updateFiltered(event)
  }

  fun editMyEvent(event: Event) {
    for (i in 0 until myEvents.size) {
      if (myEvents[i].id == event.id) {
        myEvents[i] = event
        displayMyEvents()
        return
      }
    }
  }

  suspend fun fetchNext(l: MutableList<Interests>) {

    val newRequest = l != previousInterests

    if (newRequest) {
      eventFirebaseConnection.offset = null
      loadedFilteredEvents = mutableListOf()
    }
    previousInterests = l.toMutableList()
    if (l.isEmpty()) {
      val nextEvents = eventFirebaseConnection.fetchNextEvents(PAGESIZE)
      loadedEvents.addAll(nextEvents)
      _uiState.value = UIState(loadedEvents)
    } else {
      val nextEvents = eventFirebaseConnection.fetchEventsBasedOnInterests(PAGESIZE, l)
      loadedFilteredEvents.addAll(nextEvents)
      _uiState.value = UIState(loadedFilteredEvents)
    }
  }

  fun filter(s: List<Interests>) {
    if (s.isEmpty()) {
      removeFilter()
      return
    }

    val newEvents =
        loadedEvents.filter { event -> event.categories?.any { it in s } ?: false }.toMutableList()
    _uiState.value = UIState(newEvents)
  }

  fun removeFilter() {
    _uiState.value = UIState(loadedEvents)
  }

  fun getLoadedEvents(): MutableList<Event> {
    return loadedEvents
  }

  private fun updateLoaded(event: Event) {
    for (i in 0 until loadedEvents.size) {
      if (event.id == loadedEvents[i].id) {
        loadedEvents[i] = event
        break
      }
    }
  }

  private fun updateFiltered(event: Event) {
    for (i in 0 until loadedFilteredEvents.size) {
      if (event.id == loadedFilteredEvents[i].id) {
        loadedFilteredEvents[i] = event
        break
      }
    }
  }
}

data class UIState(val list: MutableList<Event> = mutableListOf())
