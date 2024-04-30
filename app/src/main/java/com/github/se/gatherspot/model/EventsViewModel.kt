package com.github.se.gatherspot.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
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
  private var loadedFilteredEvents: MutableList<Event> = mutableListOf()
  val eventFirebaseConnection = EventFirebaseConnection()
  var previousInterests = mutableListOf<Interests>()

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

  fun displayMyEvents() {
    _uiState.value = UIState(myEvents)
  }

  fun displayMyNewEvent(event: Event) {
    myEvents.add(0, event)
    displayMyEvents()
  }

  fun displayRegisteredTo() {
    _uiState.value = UIState(registeredTo)
  }

  fun displayNewRegistered(event: Event) {
    registeredTo.add(0, event)
    displayRegisteredTo()
  }

  fun editMyEvent(event: Event) {
    for (i in 0 until myEvents.size) {
      if (myEvents[i].id == event.id) {
        myEvents[i] = event
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
}

data class UIState(val list: MutableList<Event> = mutableListOf())
