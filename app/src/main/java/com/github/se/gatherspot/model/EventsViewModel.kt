package com.github.se.gatherspot.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

  val EventFirebaseConnection = EventFirebaseConnection()

  val PAGESIZE: Long = 9
  private var _uiState = MutableStateFlow(EventsUIState())
  val uiState: StateFlow<EventsUIState> = _uiState

  init {
    viewModelScope.launch {
      val events = EventFirebaseConnection.fetchNextEvents(PAGESIZE)
      _uiState.value = EventsUIState(events)
    }
  }

  suspend fun fetchNext() {
    val nextEvents = EventFirebaseConnection.fetchNextEvents(PAGESIZE)
    val newEvents = _uiState.value.list.apply { addAll(nextEvents) }
    _uiState.value = EventsUIState(newEvents)
  }
}

data class EventsUIState(val list: MutableList<Event> = mutableListOf())
