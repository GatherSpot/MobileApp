package com.github.se.gatherspot.ui.eventUI

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.sql.EventDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel class for handling event registration logic
 *
 * @param registered the list of registered users
 * @property registrationState the registration state
 * @property displayAlertRegistration boolean that tells if the alert dialog for the registration
 *   should be displayed
 * @property displayAlertDeletion boolean that tells if the alert dialog for the deletion should be
 *   displayed
 */
open class EventRegistrationViewModel(registered: List<String>) : ViewModel() {
  private val userId = ProfileFirebaseConnection().getCurrentUserUid() ?: "TEST"

  // LiveData for holding registration state
  private val _registrationState: MutableLiveData<RegistrationState> =
      if (FirebaseAuth.getInstance().currentUser == null) {
        MutableLiveData(RegistrationState.Success)
      } else if (registered.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
        MutableLiveData(RegistrationState.Success)
      } else {
        MutableLiveData(RegistrationState.NoError)
      }

  val registrationState: LiveData<RegistrationState> = _registrationState

  // LiveData for displaying the alert dialog for the registration
  private val _displayAlertRegistration = MutableLiveData(false)
  val displayAlertRegistration: LiveData<Boolean> = _displayAlertRegistration

  // LiveData for displaying the alert dialog for the deletion
  private val _displayAlertDeletion = MutableLiveData(false)
  val displayAlertDeletion: LiveData<Boolean> = _displayAlertDeletion

  // Profile of the user, is needed to add the event to the user's registered events
  private var registeredEventsList: IdList =
      IdList.empty(userId, FirebaseCollection.REGISTERED_EVENTS)

  init {
    viewModelScope.launch {
      registeredEventsList =
          IdListFirebaseConnection().fetch(userId, FirebaseCollection.REGISTERED_EVENTS) {}
      delay(2000)
    }
  }

  private val eventFirebaseConnection = EventFirebaseConnection()

  /**
   * Registers the user for the given event
   *
   * @param event the event to register for
   */
  fun registerForEvent(event: Event, eventDao: EventDao? = null) {
    // Perform registration logic here, such as making network requests
    viewModelScope.launch(Dispatchers.IO) {
      // Simulate network request delay
      if (event.attendanceMaxCapacity != null) {
        if (event.registeredUsers.size >= event.attendanceMaxCapacity) {
          _registrationState.postValue(RegistrationState.Error("Event is full"))
          return@launch
        }
      }
      // Check if the user is already registered for the event
      if (event.registeredUsers.contains(userId)) {
        _registrationState.postValue(RegistrationState.Error("Already registered for this event"))
        Log.e("EventRegistrationViewModel", "${registrationState.value}")
        return@launch
      }
      if (!(event.registeredUsers.contains(userId))) {
        event.registeredUsers.add(userId)
        eventDao?.insert(event)
        eventFirebaseConnection.addRegisteredUser(event.id, userId)
        registeredEventsList.add(event.id)
        _registrationState.postValue(RegistrationState.Success)
        return@launch
      }
    }
    _displayAlertRegistration.postValue(true)
  }

  fun clickDeleteButton() {
    _displayAlertDeletion.value = true
  }

  fun dismissAlert() {
    _displayAlertRegistration.value = false
    _displayAlertDeletion.value = false
  }
}

/** Sealed class for holding the registration state */
sealed class RegistrationState {
  data object Success : RegistrationState()

  data class Error(val message: String) : RegistrationState()

  data object NoError : RegistrationState()
}
