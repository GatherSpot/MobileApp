package com.github.se.gatherspot.ui.setUp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SetUpViewModel() : ViewModel() {
  var profile = ProfileFirebaseConnection().fetch(Firebase.auth.uid ?: "TEST") {}
  var interests = MutableLiveData(Interests.new())
  var bio = MutableLiveData("")
  var bioError = MutableLiveData("")
  var image = MutableLiveData("")
  var isDone = MutableLiveData(false)
  var doneButton = MutableLiveData(false)
  var currentStep = MutableLiveData("Interests")

  fun flipInterests(interest: Interests) {
    interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  // TODO : change this from the setter of the model after I refactor it :)
  fun setBio(string: String) {
    bio.value = string
    bioError = Profile.checkBio(string)
  }
  // TODO: make sure this function ends even if scope ends
  private fun done() {
    profile.bio = bio.value!!
    profile.image = image.value!!
    profile.interests = interests.value!!
    doneButton.value = true
    viewModelScope.launch {
      async { ProfileFirebaseConnection().add(profile) }
          .invokeOnCompletion { isDone.postValue(true) }
    }
  }

  fun next() {
    when (currentStep.value) {
      "Interests" -> currentStep.value = "Bio"
      "Bio" -> currentStep.value = "Image"
      "Image" -> {
        currentStep.value = "Done"
        done()
      }
      else -> {}
    }
  }
}
