package com.github.se.gatherspot.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class OwnProfileViewModel : ViewModel() {
  private lateinit var _profile: Profile
  private var _username = MutableLiveData<String>()
  private var _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _interests = MutableLiveData<Set<Interests>>()

  init {
    // TODO : Change this to hilt injection
    Firebase.auth.currentUser?.uid?.let { uid ->
      _profile = ProfileFirebaseConnection().updateFromFirebase(uid) { update() }
    }
        ?: run {
          _profile = Profile.testOrganizer()
          update()
        }
  }

  val username: LiveData<String>
    get() = _username

  val bio: LiveData<String>
    get() = _bio

  val image: LiveData<String>
    get() = _image

  val interests: LiveData<Set<Interests>>
    get() = _interests

  fun save() {
    _profile.userName = _username.value!!
    _profile.bio = _bio.value!!
    _profile.image = _image.value!!
    _profile.interests = _interests.value!!
    ProfileFirebaseConnection().saveToFirebase(_profile)
  }

  fun update() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
    _interests.value = _profile.interests
  }

  // TODO : add sanitization to these function !!!
  fun updateUsername(userName: String) {
    _username.value = userName
  }

  fun updateBio(bio: String) {
    _bio.value = bio
  }

  fun updateProfileImage(image: String) {
    _image.value = image
  }

  fun flipInterests(interest: Interests) {
    _interests.value =
        if (interest in _interests.value!!) {
          interests.value!!.minus(interest)
        } else {
          interests.value!!.plus(interest)
        }
  }

  fun isInterestsSelected(interest: Interests): Boolean {
    return interest in _interests.value!!
  }
}

class ProfileViewModel(profile: Profile) {
  val username: String = profile.userName
  val bio: String = profile.bio
  val image: String = profile.image
  val interests: Set<Interests> = profile.interests
}