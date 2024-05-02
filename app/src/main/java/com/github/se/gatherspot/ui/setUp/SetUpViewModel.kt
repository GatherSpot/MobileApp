package com.github.se.gatherspot.ui.setUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SetUpViewModel() : ViewModel() {
  var profile = ProfileFirebaseConnection().fetch(Firebase.auth.uid ?: "TEST") {}
  var interests = MutableLiveData(Interests.new())
  var bio = MutableLiveData("")
  var image = MutableLiveData("")
  var isDone = MutableLiveData(false)
  // TODO : change this from the setter of the model after I refactor it :)
  fun flipInterests(interest: Interests) {
    interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  // TODO : change this from the setter of the model after I refactor it :)
  fun setBio(string: String) {
    bio.value = string
  }
  // TODO: try to call done earlier to make it more responsive
  fun done() {
    profile.bio = bio.value!!
    profile.image = image.value!!
    profile.interests = interests.value!!
    ProfileFirebaseConnection().add(profile) { isDone.value = true }
  }
}
