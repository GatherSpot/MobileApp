package com.github.se.gatherspot.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile

class OwnProfileViewModel : ViewModel() {
  private var _profile: Profile
  private val _username = MutableLiveData<String>()
  private val _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _edit = MutableLiveData<Boolean>()
  val username: LiveData<String>
    get() = _username

  val bio: LiveData<String>
    get() = _bio

  val image: LiveData<String>
    get() = _image

  val edit: LiveData<Boolean>
    get() = _edit

  init {
    _profile = Profile("John Doe", "I am not a bot", "", "")
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
    _edit.value = false
  }

  fun toggleEdit() {
    _edit.value = !_edit.value!!
  }

  fun save() {
    _profile = Profile(_username.value ?: "", bio.value ?: "", image.value ?: "", "")
    // next: THIS NEEDS SANITIZATION
    ProfileFirebaseConnection().updateProfile(_profile)
  }

  fun cancel() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
  }

  fun updateUsername(userName: String) {
    _username.value = userName
  }

  fun updateBio(bio: String) {
    _bio.value = bio
  }

  fun updateProfileImage(image: String) {
    _image.value = image
  }
}

class ProfileViewModel(profile: Profile) {
  val username: String = profile.userName
  val bio: String = profile.bio
  val image: String = profile.image
}
