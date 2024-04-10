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


    init {
        viewModelScope.launch {
            val events = EventFirebaseConnection.fetchNextEvents(PAGESIZE)
            _uiState.value = UIState(events)
        }
    }

    suspend fun fetchNext(){
        val nextEvents = EventFirebaseConnection.fetchNextEvents(PAGESIZE)
        val newEvents = _uiState.value.list.apply { addAll(nextEvents) }
        _uiState.value = UIState(newEvents)
    }

}


data class UIState(val list: MutableList<Event> = mutableListOf())