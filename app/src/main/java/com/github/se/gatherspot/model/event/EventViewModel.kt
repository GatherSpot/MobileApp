package com.github.se.gatherspot.model.event

import androidx.compose.runtime.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
      if (event.registeredUsers != null) {
        if (event.attendanceMaxCapacity != null) {
          if (event.registeredUsers.size < event.attendanceMaxCapacity) {
              // add profil to registeredUser
              // add event to profile
              event.registeredUsers.add(profile)
              // push everything to the dataBase
              // Change the state

          }
        }
      }
      _registrationState.value =
          RegistrationState.Success // or RegistrationState.Error(errorMessage)
    }
  }
}

sealed class RegistrationState {
  object Success : RegistrationState()

  data class Error(val message: String) : RegistrationState()
}
