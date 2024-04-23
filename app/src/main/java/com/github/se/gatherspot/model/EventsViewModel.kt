package com.github.se.gatherspot.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

  val PAGESIZE: Long = 9
  private var _uiState = MutableStateFlow(UIState())
  val uiState: StateFlow<UIState> = _uiState
  private var loadedEvents: MutableList<Event> = mutableListOf()
  private var loadedFilteredEvents: MutableList<Event> = mutableListOf()
  val eventFirebaseConnection = EventFirebaseConnection()
  var previousInterests = mutableListOf<Interests>()

  init {
    viewModelScope.launch {
      val events = eventFirebaseConnection.fetchNextEvents(PAGESIZE)
      _uiState.value = UIState(events)
      loadedEvents = events.toMutableList()
    }
  }

  suspend fun fetchNext(l: List<Interests>) {
    //removeFilter()
    Log.d(TAG, "previous$previousInterests")
    Log.d(TAG, "current$l")
    val newRequest = l != previousInterests
    Log.d(TAG, newRequest.toString())
    if(newRequest){
      Log.d(TAG, "new request")
      eventFirebaseConnection.offset = null
      loadedFilteredEvents = mutableListOf()
    }
    previousInterests = l.toMutableList()
    if(l.isEmpty()) {
      val nextEvents = eventFirebaseConnection.fetchNextEvents(PAGESIZE)
      loadedEvents.addAll(nextEvents)
      _uiState.value = UIState(loadedEvents)
    }
    else{
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

    val newEvents = loadedEvents.filter { event -> event.categories?.any { it in s } ?: false }.toMutableList()
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
