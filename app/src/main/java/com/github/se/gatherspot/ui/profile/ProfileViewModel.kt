package com.github.se.gatherspot.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.data.Profile

class OwnProfileViewModel : ViewModel() {
  private lateinit var _profile: Profile
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
    // next: fetch on firebase (dummy data for now)
    _profile = Profile("John Doe", "I am not a bot", "")
    _username.value = _profile.getUserName()
    _bio.value = _profile.getBio()
    _image.value = _profile.getImage()
    _edit.value = false
  }

  fun toggleEdit() {
    _edit.value = !_edit.value!!
  }

  fun save() {
    _profile = Profile(_username.value ?: "", bio.value ?: "", image.value ?: "")
    // next: Save on firebase
    // next: guard against empty as this means something went wrong except for image
    _username.value = _profile.getUserName()
    _bio.value = _profile.getBio()
    _image.value = _profile.getImage()
  }

  fun cancel() {
    _username.value = _profile.getUserName()
    _bio.value = _profile.getBio()
    _image.value = _profile.getImage()
  }

  fun updateUsername(userName: String) {
    _username.value = userName
  }

  fun updateBio(bio: String) {
    _bio.value = bio
  }
}

class ProfileViewModel(private val _profile: Profile) {
  val username: String = _profile.getUserName()
  val bio: String = _profile.getBio()
  val image: String = _profile.getImage()
}
