package com.github.se.gatherspot.ui.topLevelDestinations

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.utils.UtilsForTests
import com.github.se.gatherspot.sql.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EventsViewModel(private val localDataBase: AppDatabase) : ViewModel() {
  private val eventDao = localDataBase.EventDao()
  private val idListDao = localDataBase.IdListDao()
  private val uid = Firebase.auth.uid!!
  val PAGESIZE: Long = 12
  private var _allEvents = MutableLiveData<List<Event>>(listOf())
  private var _myEvents = MutableLiveData<List<Event>>(listOf())
  private var _upComing = MutableLiveData<List<Event>>(listOf())
  private var _attended = MutableLiveData<List<Event>>(listOf())
  private var _fetching = MutableLiveData(false)
  private var _fetchingUpcoming = MutableLiveData(false)
  private var _fetchingAttended = MutableLiveData(false)
  private var _fetchingFollowed = MutableLiveData(false)
  private var _fetchingMine = MutableLiveData(false)
  private var _fromFollowedUsers = MutableLiveData<List<Event>>(listOf())
  private val viewModelJob = SupervisorJob()
  private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
  private var fetchJob: Job? = null

  val eventFirebaseConnection = EventFirebaseConnection()
  // To synchronize between screens, it is initialized with the selected interests from MainActivity
  private var _interests = MutableLiveData(MainActivity.selectedInterests.value!!)
  private var _showFilterDialog = MutableLiveData(false)
  val tabList = listOf("Mine", "Feed", "Planned", "Follows", "Attended")
  // we start at Feed, so default is 1 !!!

  val allEvents: LiveData<List<Event>> = _allEvents
  val myEvents: LiveData<List<Event>> = _myEvents
  val upComing: LiveData<List<Event>> = _upComing
  val attended: LiveData<List<Event>> = _attended
  val fromFollowedUsers: LiveData<List<Event>> = _fromFollowedUsers
  val showFilterDialog: LiveData<Boolean> = _showFilterDialog
  val fetching: LiveData<Boolean> = _fetching
  val fetchingUpcoming: LiveData<Boolean> = _fetchingUpcoming
  val fetchingAttended: LiveData<Boolean> = _fetchingAttended
  val fetchingFollowed: LiveData<Boolean> = _fetchingFollowed
  val fetchingMine: LiveData<Boolean> = _fetchingMine

  // fetch all event lists on activity start.
  init {
    fetchWithInterests()
    fetchMyEvents()
    fetchUpComing()
    fetchFromFollowedUsers()
    fetchAttended()
    fetchMyEvents()
  }
  /** Fetch my events from the local database and update the live data with the new events. */
  fun fetchMyEvents() {
    viewModelScope.launch(Dispatchers.IO) {
      _fetchingMine.postValue(true)
      _myEvents.postValue(eventDao.getAllFromOrganizerId(uid))
      try {
        val events = eventFirebaseConnection.fetchMyEvents()
        _myEvents.postValue(events)
        eventDao.insert(*events.toTypedArray())
      } catch (_: Exception) {}
      _fetchingMine.postValue(false)
    }
  }

  /**
   * Fetch events that the user is registered to and that are not over from the local database and
   * update the live data with the new events.
   */
  fun fetchAttended() {
    viewModelScope.launch(Dispatchers.IO) {
      _fetchingAttended.postValue(true)
      _upComing.postValue(eventDao.getAllPastWhereIdIsRegistered(uid))
      lateinit var events: List<Event>
      try {
        events = eventFirebaseConnection.fetchAttended()
        _attended.postValue(events)
        eventDao.insert(*events.toTypedArray())
      } catch (_: Exception) {}
      _fetchingAttended.postValue(false)
    }
  }

  /**
   * Fetch events that the user is registered to and that are not over from the local database and
   * update the live data with the new events.
   */
  fun fetchUpComing() {
    viewModelScope.launch(Dispatchers.IO) {
      _fetchingUpcoming.postValue(true)
      _upComing.postValue(eventDao.getAllUpcomingWhereIdIsRegistered(uid))
      lateinit var events: List<Event>
      try {
        events = eventFirebaseConnection.fetchUpComing()
        _upComing.postValue(events)
        eventDao.insert(*events.toTypedArray())
      } catch (_: Exception) {}
      _fetchingUpcoming.postValue(false)
    }
  }
  /**
   * Fetch events from the users that the current user follows and update the live data with the new
   * events.
   */
  fun fetchFromFollowedUsers() {
    viewModelScope.launch(Dispatchers.IO) {
      _fetchingFollowed.postValue(true)
      val ids =
          FollowList.following(
              FirebaseAuth.getInstance().currentUser?.uid ?: UtilsForTests.testLoginId)
      Log.d(TAG, "ids from viewModel ${ids.elements}")
      val events = eventFirebaseConnection.fetchEventsFrom(ids.elements)
      _fromFollowedUsers.postValue(events)
      // Try to get from local first
      val localIds = idListDao.get(FirebaseCollection.FOLLOWING, uid)
      if (localIds?.elements?.isNotEmpty() == true) {
        val localEvents = eventDao.getAllFromOrganizerId(*localIds.elements.toTypedArray())
        if (!localEvents.isNullOrEmpty()) {
          _fromFollowedUsers.postValue(localEvents!!)
        }
      }
      // Try to get from firebase and update local
      try {
        val ids =
            FollowList.following(
                FirebaseAuth.getInstance().currentUser?.uid ?: UtilsForTests.testLoginId)
        Log.d(TAG, "ids from viewModel ${ids.elements}")
        val events = eventFirebaseConnection.fetchEventsFrom(ids.elements)
        eventDao.insert(*events.toTypedArray())
        _fromFollowedUsers.postValue(events)
      } catch (_: Exception) {}
      _fetchingFollowed.postValue(false)
    }
  }

  /**
   * Fetch events from the database based on the interests of the user. Calling it again will fetch
   * additional ones, unless we use resetOffset.
   */
  fun fetchWithInterests() {
    fetchJob?.cancel()
    fetchJob =
        viewModelScope.launch(Dispatchers.IO) {
          _fetching.postValue(true)
          val newEvents =
              eventFirebaseConnection.fetchEventsBasedOnInterests(
                  PAGESIZE, _interests.value!!.toList())
          val events = _allEvents.value!!.plus(newEvents)
          _allEvents.postValue(events)
          _fetching.postValue(false)
        }
  }
  /**
   * Refresh the events. This is used when we want to fetch the events from the beginning again.
   * This is needed because the fetchWith interest will fetch additional events at each call.
   */
  fun refresh() {
    resetOffset()
    fetchWithInterests()
  }
  /**
   * Used to do the needed logic when changing the filter
   *
   * @Param s: Set of interests to filter the events
   */
  private fun setFilter(s: Set<Interests>) {
    // check if change
    if (s == _interests.value) {
      return
    }
    // change -> reset offset and update interests
    _interests.value = s
    refresh()
  }
  /**
   * Set to cancel changes and revert to the previous filter from the view It is actually used when
   * we dismiss the dialog
   */
  fun revertFilter() {
    MainActivity.selectedInterests.value = _interests.value
    dismissDialog()
  }
  /** Apply the filter from the view Used when we commit the actual values from the dialog */
  fun applyFilter() {
    setFilter(MainActivity.selectedInterests.value!!)
    dismissDialog()
  }
  /** Show the dialog to change the filter */
  fun showDialog() {
    _showFilterDialog.value = true
  }
  /** Dismiss the dialog to change the filter */
  private fun dismissDialog() {
    _showFilterDialog.value = false
  }
  /** Remove the filter and show all events */
  fun removeFilter() {
    dismissDialog()
    MainActivity.selectedInterests.value = setOf()
    _interests.value = setOf()
    resetOffset()
    fetchWithInterests()
  }
  /**
   * Start fetching events from the beginning again. This is needed because the fetchWith interest
   * will fetch additional events at each call. So we need to reset it if we fetch with a new
   * filter.
   */
  private fun resetOffset() {
    eventFirebaseConnection.offset = null
    _allEvents.value = listOf()
  }
  /**
   * Get the timing of the event
   *
   * @Param event: Event to get the timing of
   */
  fun getEventTiming(event: Event): EventTiming {
    val today = LocalDate.now()
    return when {
      event.eventStartDate!!.isBefore(today) -> EventTiming.PAST
      event.eventStartDate!!.isEqual(today) -> EventTiming.TODAY
      else -> EventTiming.FUTURE
    }
  }
  /**
   * Check if the user is the organizer of the event
   *
   * @Param event: Event to check if the user is the organizer of
   */
  fun isOrganizer(event: Event) = event.organizerID == uid

  /**
   * Check if the user is registered to the event
   *
   * @Param event: Event to check if the user is registered to
   */
  fun isRegistered(event: Event) = event.registeredUsers.contains(uid)

  enum class EventTiming {
    PAST,
    TODAY,
    FUTURE,
  }

  companion object {
    const val MINE = 0
    const val FEED = 1
    const val PLANNED = 2
    const val FOLLOWS = 3
    const val ATTENDED = 4
  }
}
