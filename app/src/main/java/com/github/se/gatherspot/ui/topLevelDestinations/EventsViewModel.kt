package com.github.se.gatherspot.ui.topLevelDestinations

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.utils.UtilsForTests
import com.github.se.gatherspot.sql.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventsViewModel(private val localDataBase: AppDatabase) : ViewModel() {
  private val eventDao = localDataBase.EventDao()
  private val uid = Firebase.auth.uid!!
  val PAGESIZE: Long = 9
  private var _allEvents = MutableLiveData<List<Event>>(listOf())
  private var _myEvents = MutableLiveData<List<Event>>(listOf())
  private var _registeredTo = MutableLiveData<List<Event>>(listOf())
  private var _fromFollowedUsers = MutableLiveData<List<Event>>(listOf())
  private val viewModelJob = SupervisorJob()
  private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
  private var fetchJob: Job? = null

  val eventFirebaseConnection = EventFirebaseConnection()
  // To synchronize between screens, it is initialized with the selected interests from MainActivity
  private var _interests = MutableLiveData(MainActivity.selectedInterests.value!!)
  private var _showFilterDialog = MutableLiveData(false)
  val tabList = listOf("Mine", "Feed", "Planned", "Follows")
  //we start at Feed, so default is 1 !!!

  val allEvents: LiveData<List<Event>> = _allEvents
  val myEvents: LiveData<List<Event>> = _myEvents
  val registeredTo: LiveData<List<Event>> = _registeredTo
  val fromFollowedUsers: LiveData<List<Event>> = _fromFollowedUsers
  val showFilterDialog: LiveData<Boolean> = _showFilterDialog
// fetch all event lists on activity start.
  init {
    fetchWithInterests()
    fetchMyEvents()
    fetchRegisteredTo()
    fetchFromFollowedUsers()
  }
  fun fetchMyEvents() {
    viewModelScope.launch(Dispatchers.IO) {
      _myEvents.postValue(eventDao.getAllFromOrganizerId(uid))
      try {
        val events = eventFirebaseConnection.fetchMyEvents()
        _myEvents.postValue(events)
        eventDao.insert(*events.toTypedArray())
      } catch (_: Exception) {
      }
    }
  }

  fun fetchRegisteredTo() {
    viewModelScope.launch(Dispatchers.IO) {
      _registeredTo.postValue(eventDao.getAllWhereIdIsRegistered(uid))
      lateinit var events: List<Event>
      try {
        events = eventFirebaseConnection.fetchRegisteredTo()
        _registeredTo.postValue(events)
        eventDao.insert(*events.toTypedArray())
      } catch (_: Exception) {
      }
    }
  }
  fun fetchFromFollowedUsers() {
    // TODO implement fetch thoses ids from localdatabase, as they are never stale locally
    viewModelScope.launch(Dispatchers.IO) {
      val ids =
      FollowList.following(
        FirebaseAuth.getInstance().currentUser?.uid ?: UtilsForTests.testLoginId
      )
      Log.d(TAG, "ids from viewModel ${ids.elements}")
      val events = eventFirebaseConnection.fetchEventsFromFollowedUsers(ids.elements)
      _fromFollowedUsers.postValue(events)
    }
  }

  fun fetchWithInterests(){
    fetchJob?.cancel()
    fetchJob = viewModelScope.launch(Dispatchers.IO) {
      val newEvents = eventFirebaseConnection.fetchEventsBasedOnInterests(PAGESIZE,_interests.value!!.toList())
      val events = _allEvents.value!!.plus(newEvents)
      _allEvents.postValue(events)
    }
  }

  fun setFilter(s: Set<Interests>) {
    // check if change
    if (s == _interests.value){return}
    // change -> reset offset and update interests
    _interests.value = s
    resetOffset()
    fetchWithInterests()
  }

  fun revertFilter() { MainActivity.selectedInterests.value = _interests.value; dismissDialog() }
  fun applyFilter() { setFilter(MainActivity.selectedInterests.value!!); dismissDialog() }
  fun showDialog() { _showFilterDialog.value = true }
  private fun dismissDialog() { _showFilterDialog.value = false }
  fun removeFilter() {
    dismissDialog()
    MainActivity.selectedInterests.value = setOf()
    _interests.value = setOf()
    resetOffset()
    fetchWithInterests()
  }
  private fun resetOffset(){
    eventFirebaseConnection.offset = null
    _allEvents.value = listOf()
  }
  fun getEventTiming(event: Event): EventTiming{
    val today = LocalDate.now()
    return when {
      event.eventStartDate!!.isBefore(today) -> EventTiming.PAST
      event.eventStartDate!!.isEqual(today) -> EventTiming.TODAY
      else -> EventTiming.FUTURE
    }
  }
  fun isOrganizer(event: Event) = event.organizerID == uid
  fun isRegistered(event: Event) = event.registeredUsers.contains(uid)

  enum class EventTiming {
    PAST,
    TODAY,
    FUTURE,
  }
}