package com.github.se.gatherspot.model.event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.firebase.RatingFirebaseConnection
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Rating
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventUIViewModel(private val event: Event,private val profileFirebaseConnection: ProfileFirebaseConnection = ProfileFirebaseConnection()) : ViewModel(){
  // Rate !

  // registered as Set
  // On launch (Fetch organizer, Fetch registered)
  private lateinit var _organizer: Profile
  private lateinit var _attendees: List<String>
  private var _rating = MutableLiveData<Rating>()
  private val userID = Firebase.auth.currentUser?.uid ?: "TEST"

  init {
    viewModelScope.launch {
      Log.d(
          "EventUIViewModel",
          "Fetching organizer and registered users organizerID : ${event.organizerID}, eventID : ${event.id}")
      _organizer = profileFirebaseConnection.fetch(event.organizerID)
      _rating.value =
          RatingFirebaseConnection()
              .fetchRating(
                  event.id, profileFirebaseConnection.getCurrentUserUid()!!)
              ?: Rating.UNRATED

      delay(500)
    }
  }

  val rating: LiveData<Rating> = _rating

  fun rateEvent(newRating: Rating) {
    viewModelScope.launch {
      /*if (!isOver(Event)){
         Log.e("RateEvent", "event ${event.id} is not over so cannot be rated")
         return@launch
      }*/
      if (userID == _organizer.id) {
        Log.e(
            "RateEvent",
            "organizer cannot rate its own event; eventID: ${event.id}, organizerID : ${_organizer.id}")
        return@launch
      }
      if (!event.registeredUsers.contains(userID)) {
        Log.e("RateEvent", "User $userID cannot rate the event ${event.id} that he didn't attend")
        return@launch
      } else {
        _rating.value = newRating
        RatingFirebaseConnection().update(event.id, userID, newRating, event.organizerID)
      }
    }
  }

  fun isOrganizer(): Boolean {
    return userID == event.organizerID
  }

  fun canRate(): Boolean {
    return !isOrganizer() &&
        event.registeredUsers.contains(userID) &&
        EventUtils().isEventOver(event)
  }
}
