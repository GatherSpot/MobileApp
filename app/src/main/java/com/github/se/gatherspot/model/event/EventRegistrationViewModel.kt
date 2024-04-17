package com.github.se.gatherspot.model.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.ProfileFirebaseConnection
import kotlinx.coroutines.launch

/** ViewModel class for handling event registration logic */
class EventRegistrationViewModel : ViewModel() {
  // LiveData for holding registration state
  private val _registrationState = MutableLiveData<RegistrationState>()
  val registrationState: LiveData<RegistrationState> = _registrationState
  private val profile = ProfileFirebaseConnection().fetchProfile(MainActivity.uid)

  /** Registers the user for the given event */
  fun registerForEvent(event: Event) {
    // Perform registration logic here, such as making network requests
    viewModelScope.launch {
      // Simulate network request delay
      if (event.attendanceMaxCapacity != null) {
        if (event.registeredUsers.size == event.attendanceMaxCapacity) {
          _registrationState.value = RegistrationState.Error("Event is full")
          return@launch
        }
      }
      // Check if the user is already registered for the event
      if (event.registeredUsers.contains(profile._uid)) {
        _registrationState.value = RegistrationState.Error("Already registered for this event")
        return@launch
      }
      event.registeredUsers.add(profile._uid)
      profile.registeredEvents.add(event.eventID)
      // Update the event in the database
      EventFirebaseConnection.addNewEvent(event)
      // Update the profile in the database. Not working yet
      ProfileFirebaseConnection().updateProfile(profile)
      // Notify the UI that registration was successful
      _registrationState.value = RegistrationState.Success
    }
  }
}

sealed class RegistrationState {
  data object Success : RegistrationState()

  data class Error(val message: String) : RegistrationState()
}
