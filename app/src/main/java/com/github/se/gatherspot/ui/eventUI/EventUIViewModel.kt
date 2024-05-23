package com.github.se.gatherspot.ui.eventUI

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

  private var _organizer = MutableLiveData<Profile>()
  private lateinit var _attendees: List<String>
  private var _ownRating = MutableLiveData<Rating>()
  private var _organizerRating = MutableLiveData<Double>()
  private var _eventRating = MutableLiveData<Double>()
  private val userID = Firebase.auth.currentUser?.uid ?: "TEST"
  private val ratingFirebaseConnection = RatingFirebaseConnection()

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

      delay(500)
    }
  }

  val ownRating: LiveData<Rating> = _ownRating
  val organizerRating: LiveData<Double> = _organizerRating
  val eventRating: LiveData<Double> = _eventRating
  val organizer: LiveData<Profile> = _organizer

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
    return !isOrganizer() &&
        event.registeredUsers.contains(userID) &&
        EventUtils().isEventOver(event)
  }
}
