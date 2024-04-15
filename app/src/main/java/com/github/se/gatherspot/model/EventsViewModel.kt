package com.github.se.gatherspot.model

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
  var loadedEvents: MutableList<Event> = mutableListOf()

  init {
    viewModelScope.launch {
      val events = EventFirebaseConnection.fetchNextEvents(PAGESIZE)
      _uiState.value = UIState(events)
      loadedEvents = events.toMutableList()
    }
  }

  suspend fun fetchNext() {
    removeFilter()
    val nextEvents = EventFirebaseConnection.fetchNextEvents(PAGESIZE)
    loadedEvents.addAll(nextEvents)
    _uiState.value = UIState(loadedEvents)
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

  private fun filterInterests() {
    // TO BE CHANGED WHEN ProfileFirebaseConnection DONE
    val userInterests = listOf(Interests.BASKETBALL, Interests.CHESS)
    val newEvents =
        _uiState.value.list
            .filter { event -> event.categories?.any { it in userInterests } ?: false }
            .toMutableList()
    _uiState.value = UIState(newEvents)
  }

  fun removeFilter() {
    _uiState.value = UIState(loadedEvents)
  }
}

data class UIState(val list: MutableList<Event> = mutableListOf())
