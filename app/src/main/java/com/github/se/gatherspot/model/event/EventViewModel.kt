package com.github.se.gatherspot.model.event

import androidx.compose.runtime.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
  // LiveData for holding registration state
  private val _registrationState = MutableLiveData<RegistrationState>()
  val registrationState: LiveData<RegistrationState> = _registrationState

  // Function to register for the event
  fun registerForEvent(event: Event, profile: Profile) {
    // Perform registration logic here, such as making network requests
    viewModelScope.launch {
      // Simulate network request delay
        if (event.attendanceMaxCapacity != null) {
          if (event.registeredUsers.size == event.attendanceMaxCapacity) {
            _registrationState.value = RegistrationState.Error("Event is full")
            return@launch

          }
        }
        event.registeredUsers.add(profile)
        profile.registeredEvents.add(event.eventID)
        // Update the event in the database
        EventFirebaseConnection.addNewEvent(event)
        // Update the profile in the database. Not working yet
        ProfileFirebaseConnection().updateProfile(profile)
        // Notify the UI that registration was successful
        _registrationState.value = RegistrationState.Success

      }
      _registrationState.value =
          RegistrationState.Success // or RegistrationState.Error(errorMessage)
    }
}


sealed class RegistrationState {
  object Success : RegistrationState()

  data class Error(val message: String) : RegistrationState()
}
