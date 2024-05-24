package com.github.se.gatherspot.ui.topLevelDestinations

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.utils.UtilsForTests
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** ViewModel for the events screen. */
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

  init {
    viewModelScope.launch {
      val events = eventFirebaseConnection.fetchNextEvents(PAGESIZE)
      loadedEvents = events.toMutableList()
      _uiState.value = UIState(loadedEvents)
    }
  }

  /** Fetches the events that the user has created and update viewModel. */
  suspend fun fetchMyEvents() {
    myEvents = eventFirebaseConnection.fetchMyEvents()
  }

  /** Fetches the events that the user has registered to and update viewModel. */
  suspend fun fetchRegisteredTo() {
    registeredTo = eventFirebaseConnection.fetchRegisteredTo()
  }

  /** Fetches the events from the followed users and update viewModel. */
  suspend fun fetchEventsFromFollowedUsers() {
    val ids =
        FollowList.following(
            FirebaseAuth.getInstance().currentUser?.uid ?: UtilsForTests.testLoginId)
    Log.d(TAG, "ids from viewModel ${ids.elements}")
    fromFollowedUsers = eventFirebaseConnection.fetchEventsFromFollowedUsers(ids.elements)
  }

  /** Display the events that the user has created. */
  fun displayMyEvents() {
    _uiState.value = UIState(myEvents)
  }

  /** Display the events that the user has registered to. */
  fun displayRegisteredTo() {
    _uiState.value = UIState(registeredTo)
  }

  /** Display the events from the followed users. */
  fun displayEventsFromFollowedUsers() {
    _uiState.value = UIState(fromFollowedUsers)
  }

  /**
   * Update the registration status of an event in the loaded events.
   *
   * @param event The event to update
   */
  fun updateNewRegistered(event: Event) {
    updateLoaded(event)
    updateFiltered(event)
  }

  /**
   * Edit an event in the myEvents list.
   *
   * @param event The event to edit
   */
  fun editMyEvent(event: Event) {
    for (i in 0 until myEvents.size) {
      if (myEvents[i].id == event.id) {
        myEvents[i] = event
        displayMyEvents()
        return
      }
    }
  }

  /**
   * Fetches the next events and updates the viewModel.
   *
   * @param l The list of interests to filter by
   */
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

  /**
   * Filter the events based on the interests.
   *
   * @param s The list of interests to filter by
   */
  fun filter(s: List<Interests>) {
    if (s.isEmpty()) {
      removeFilter()
      return
    }

    val newEvents =
        loadedEvents.filter { event -> event.categories?.any { it in s } ?: false }.toMutableList()
    _uiState.value = UIState(newEvents)
  }

  /** Remove the filter and display all events. */
  fun removeFilter() {
    _uiState.value = UIState(loadedEvents)
  }

  /**
   * Get the list of loaded events.
   *
   * @return The list of loaded events
   */
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

  // should not be used anymore
  /*
   fun addToLocalDatabase(eventDao: EventDao?, event: Event) {
     viewModelScope.launch(Dispatchers.IO) { eventDao?.insert(event) }
   }

   fun updateLocalDatabase(eventDao: EventDao?, event: Event) {
     viewModelScope.launch(Dispatchers.IO) { eventDao?.update(event) }
   }

   fun deleteFromLocalDatabase(eventDao: EventDao?, event: Event) {
     viewModelScope.launch(Dispatchers.IO) { eventDao?.delete(event) }

  }

    */
}

data class UIState(val list: MutableList<Event> = mutableListOf())
