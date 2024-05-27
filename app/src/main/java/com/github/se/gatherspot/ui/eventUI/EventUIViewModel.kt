package com.github.se.gatherspot.ui.eventUI

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.firebase.RatingFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import com.github.se.gatherspot.model.event.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel class for handling event UI logic
 *
 * @param event the event
 * @property ownRating the rating of the user
 * @property organizerRating the rating of the organizer
 * @property eventRating the rating of the event
 * @property organizer the organizer of the event
 */
class EventUIViewModel(private val event: Event) :
    EventRegistrationViewModel(event.registeredUsers) {

  private val _organizer = MutableLiveData<Profile>()
  private val _displayAlertAttend = MutableLiveData(false)
  private val _ownRating = MutableLiveData(Rating.UNRATED)
  private val _organizerRating = MutableLiveData<Double>()
  private val _eventRating = MutableLiveData<Double>()
  private val userID = Firebase.auth.currentUser?.uid ?: "TEST"
  private val ratingFirebaseConnection = RatingFirebaseConnection()
  private val _attended = MutableLiveData<Boolean>()

  init {
    viewModelScope.launch {
      Log.d(
          "EventUIViewModel",
          "Fetching organizer and registered users organizerID : ${event.organizerID}, eventID : ${event.id}")
      _ownRating.value =
          ratingFirebaseConnection.fetchRating(
              event.id, (Firebase.auth.currentUser?.uid ?: Profile.testParticipant().id))
              ?: Rating.UNRATED
      _organizerRating.value =
          ratingFirebaseConnection.fetchOrganizerGlobalRating(event.organizerID) ?: 0.0
      _eventRating.value = ratingFirebaseConnection.fetchEventGlobalRating(event.id) ?: 0.0
      _organizer.value = ProfileFirebaseConnection().fetch(event.organizerID)
      _attended.value = event.finalAttendees?.contains(userID) ?: false

      delay(500)
    }
  }

  val ownRating: LiveData<Rating> = _ownRating
  val organizerRating: LiveData<Double> = _organizerRating
  val eventRating: LiveData<Double> = _eventRating
  val organizer: LiveData<Profile> = _organizer
  val attended: LiveData<Boolean> = _attended
  val displayAlertAttend: LiveData<Boolean> = _displayAlertAttend

  /**
   * Rate the event
   *
   * @param newRating the new rating
   */
  fun rateEvent(newRating: Rating) {
    viewModelScope.launch {
      /*if (!isOver(Event)){
         Log.e("RateEvent", "event ${event.id} is not over so cannot be rated")
         return@launch
      }*/
      if (userID == event.organizerID) {
        Log.e(
            "RateEvent",
            "organizer cannot rate its own event; eventID: ${event.id}, organizerID : ${event.organizerID}")
        return@launch
      }
      if (!event.registeredUsers.contains(userID)) {
        Log.e("RateEvent", "User $userID cannot rate the event ${event.id} that he didn't attend")
        return@launch
      } else {
        _ownRating.value = newRating
        ratingFirebaseConnection.update(event.id, userID, newRating, event.organizerID)
        delay(1000)
        _eventRating.value = ratingFirebaseConnection.fetchEventGlobalRating(event.id) ?: 0.0
        _organizerRating.value =
            ratingFirebaseConnection.fetchOrganizerGlobalRating(organizerID = event.organizerID)
                ?: 0.0
      }
    }
  }

  /**
   * Check if the user is the organizer of the event
   *
   * @return true if the user is the organizer of the event, false otherwise
   */
  fun isOrganizer(): Boolean {
    return userID == event.organizerID
  }

  /**
   * Check if the user can rate the event
   *
   * @return true if the user can rate the event, false otherwise
   */
  fun canRate(): Boolean {
    return !isOrganizer() && (attended.value == true) && EventUtils().isEventOver(event)
  }

  /**
   * Check if the user can attend the event
   *
   * @return true if the user can attend the event, false otherwise
   */
  fun canAttend(): Boolean {
    return !isOrganizer() &&
        event.registeredUsers.contains(userID) &&
        EventUtils().isEventStarted(event)
  }

  /** Attend the event */
  fun attendEvent() {
    viewModelScope.launch {
      if (event.organizerID == userID) {
        Log.e("AttendEvent", "Organizer cannot attend its own event")
        return@launch
      }
      if (event.finalAttendees?.contains(userID) == true) {
        Log.e("AttendEvent", "User $userID is already attending the event ${event.id}")
        return@launch
      }
      if (!event.registeredUsers.contains(userID)) {
        Log.e("AttendEvent", "User $userID is not registered for the event ${event.id}")
        return@launch
      }
      _attended.value = true
      event.finalAttendees?.plus(userID)
      EventFirebaseConnection().addFinalAttendee(event.id, userID)
      _displayAlertAttend.value = true
    }
  }

  override fun dismissAlert() {
    super.dismissAlert()
    _displayAlertAttend.value = false
  }
}
