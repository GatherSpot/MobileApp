package com.github.se.gatherspot.ui.setUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SetUpViewModel(val nav: NavigationActions) : ViewModel() {
  var profile = ProfileFirebaseConnection().fetch(Firebase.auth.uid ?: "TEST") {}
  var interests = MutableLiveData(Interests.new())
  var currentState = MutableLiveData(Phases.INTERESTS)
  var bio = MutableLiveData("")
  var image = MutableLiveData("")

  enum class Phases {
    INTERESTS,
    BIO,
    IMAGE,
    DONE
  }
  // TODO : change this from the setter of the model after I refactor it :)
  fun flipInterests(interest: Interests) {
    interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  fun next() {
    when (currentState.value) {
      Phases.INTERESTS -> currentState.value = Phases.BIO
      Phases.BIO -> currentState.value = Phases.IMAGE
      Phases.IMAGE -> currentState.value = Phases.DONE
      else -> {}
    }
  }
  // TODO : change this from the setter of the model after I refactor it :)
  fun setBio(string: String) {
    bio.value = string
  }

  fun done() {
    profile.interests = interests.value!!
    profile.bio = bio.value!!
    profile.image = image.value!!
    ProfileFirebaseConnection().add(profile)
    nav.controller.navigate("home")
  }
}
