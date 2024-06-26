package com.github.se.gatherspot.ui.setUp

import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.FirebaseImages
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/** ViewModel for the user's profile setup. */
class SetUpViewModel : ViewModel() {
  var profile = ProfileFirebaseConnection().fetch(Firebase.auth.uid ?: "TEST") {}
  var interests = MutableLiveData(Interests.new())
  var bio = MutableLiveData("")
  var bioError = MutableLiveData("")
  var image = MutableLiveData("")
  var isDone = MutableLiveData(false)
  var doneButton = MutableLiveData(false)
  var currentStep = MutableLiveData("Interests")

  fun setImage(uri: String) {
    image.value = uri
  }

  fun deleteImage() {
    image.value = ""
  }

  fun flipInterests(interest: Interests) {
    interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  fun setBio(string: String) {
    bio.value = string
    Profile.checkBio(string, bioError)
  }

  private fun done() {
    profile.bio = bio.value!!
    profile.image = image.value!!
    profile.interests = interests.value!!
    doneButton.value = true
    viewModelScope.launch {
      try {
        ProfileFirebaseConnection().add(profile)
        if (!image.value.isNullOrEmpty()) {
          FirebaseImages().pushProfilePicture(image.value!!.toUri(), profile.id)
        }
      } catch (e: Exception) {
        // TODO show error dialog
      }
    }
    isDone.postValue(true)
  }

  /** Move to the next step in the setup process. */
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
