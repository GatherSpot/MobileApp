package com.github.se.gatherspot.ui.eventUI

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.sql.EventDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel class for handling event registration logic
 *
 * @param event the event the view model acts on
 * @property registrationState the registration state
 * @property displayAlertRegistration boolean that tells if the alert dialog for the registration
 *   should be displayed
 * @property displayAlertDeletion boolean that tells if the alert dialog for the deletion should be
 *   displayed
 */
open class EventRegistrationViewModel(private val event: Event) : ViewModel() {
  private val userId = ProfileFirebaseConnection().getCurrentUserUid() ?: "TEST"

  // LiveData for holding registration state
  private val _registrationState: MutableLiveData<RegistrationState> =
      when {
        EventUtils().isRegistrationOver(event) ->
            MutableLiveData(RegistrationState.Error("Registration Over"))
        event.registeredUsers.contains(userId) -> MutableLiveData(RegistrationState.Registered)
        else -> {
          when {
            event.attendanceMaxCapacity == null -> MutableLiveData(RegistrationState.Unregistered)
            event.registeredUsers.size >= event.attendanceMaxCapacity ->
                MutableLiveData(RegistrationState.Error("Full event"))
            else -> MutableLiveData(RegistrationState.Unregistered)
          }
        }
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
    viewModelScope.launch(Dispatchers.IO) {
      try {
        registeredEventsList =
            IdListFirebaseConnection().fetch(userId, FirebaseCollection.REGISTERED_EVENTS)
      } catch (_: Exception) {}
    }
  }

  private val eventFirebaseConnection = EventFirebaseConnection()

  /** Calls the register or unregistered function depending on the current state */
  fun toggleRegistrationStatus(eventDao: EventDao? = null) {
    _registrationState.value?.let { state ->
      when (state) {
        RegistrationState.Registered -> unregister(eventDao)
        else -> register(eventDao)
      }
    }
  }

  /**
   * Registers the user for the given event
   *
   * @param eventDao: the local database for events
   */
  private fun register(eventDao: EventDao? = null) {
    // Perform registration logic here, such as making network requests
    viewModelScope.launch(Dispatchers.IO) {
      event.registeredUsers.add(userId)
      eventDao?.insert(event)
      eventFirebaseConnection.addRegisteredUser(event.id, userId)
      registeredEventsList.add(event.id)
      _registrationState.postValue(RegistrationState.Registered)
      _displayAlertRegistration.postValue(true)
    }
  }

  /**
   * Unregister user for the event (remove it from list of registered, etc)
   *
   * @param eventDao: local database for events
   */
  private fun unregister(eventDao: EventDao? = null) {
    viewModelScope.launch(Dispatchers.IO) {
      event.registeredUsers.remove(userId)
      eventDao?.delete(event)
      eventFirebaseConnection.removeRegisteredUser(event.id, userId)
      registeredEventsList.remove(event.id)
      _registrationState.postValue(RegistrationState.Unregistered)
      _displayAlertRegistration.postValue(true)
    }
  }

  fun clickDeleteButton() {
    _displayAlertDeletion.value = true
  }

  // Self-explanatory
  open fun dismissAlert() {
    _displayAlertRegistration.value = false
    _displayAlertDeletion.value = false
  }
}

/** Sealed class for holding the registration state */
sealed class RegistrationState {
  data object Registered : RegistrationState()

  data object Unregistered : RegistrationState()

  data class Error(val message: String) : RegistrationState()
}
