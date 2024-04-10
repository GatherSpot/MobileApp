package com.github.se.gatherspot.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile

class OwnProfileViewModel : ViewModel() {
  private var _profile: Profile
  private val _username = MutableLiveData<String>()
  private val _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _interests = MutableLiveData<Set<Interests>>()
  val username: LiveData<String>
    get() = _username

  val bio: LiveData<String>
    get() = _bio

  val image: LiveData<String>
    get() = _image

  val interests: LiveData<Set<Interests>>
    get() = _interests

  init {
    _profile = Profile("John Doe", "I am not a bot", "", "", hashSetOf())
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
  }

  fun save() {
    _profile = Profile(username.value ?: "", bio.value ?: "", image.value ?: "", "", interests.value)
    // next: THIS NEEDS SANITIZATION
    ProfileFirebaseConnection.addProfile(_profile)
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

  fun edit() {}


}


class ProfileViewModel(profile: Profile) {
  val username: String = profile.userName
  val bio: String = profile.bio
  val image: String = profile.image
}
