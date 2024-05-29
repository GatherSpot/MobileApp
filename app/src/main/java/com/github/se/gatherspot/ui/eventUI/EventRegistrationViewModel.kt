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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** ViewModel class for handling event registration logic */
open class EventRegistrationViewModel(registered: List<String>) : ViewModel() {
  // TODO : use hilt injection instead of hardcoded userId to remove this test handle in production
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

  /** Registers the user for the given event */
  fun registerForEvent(event: Event) {
    // Perform registration logic here, such as making network requests
    viewModelScope.launch {
      // Simulate network request delay
      if (event.attendanceMaxCapacity != null) {
        if (event.registeredUsers.size >= event.attendanceMaxCapacity) {
          _registrationState.value = RegistrationState.Error("Event is full")
          return@launch
        }
      }
      // Check if the user is already registered for the event
      if (event.registeredUsers.contains(userId)) {
        _registrationState.value = RegistrationState.Error("Already registered for this event")
        Log.e("EventRegistrationViewModel", "${registrationState.value}")
        return@launch
      }
      if (!(event.registeredUsers.contains(userId))) {
        event.registeredUsers.add(userId)
        FirebaseMessaging.getInstance().subscribeToTopic("event_$userId").addOnSuccessListener {
          Log.d("EventRegistrationViewModel", "Subscribed to topic")
        }
        eventFirebaseConnection.addRegisteredUser(event.id, userId)
        registeredEventsList.add(event.id)
        _registrationState.value = RegistrationState.Success
        return@launch
      }
    }
  }

  fun clickRegisterButton() {
    _displayAlertRegistration.value = true
  }

  fun clickDeleteButton() {
    _displayAlertDeletion.value = true
  }

  fun dismissAlert() {
    _displayAlertRegistration.value = false
    _displayAlertDeletion.value = false
  }
}

sealed class RegistrationState {
  data object Success : RegistrationState()

  data class Error(val message: String) : RegistrationState()

  data object NoError : RegistrationState()
}
