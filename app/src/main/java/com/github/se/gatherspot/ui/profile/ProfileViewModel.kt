package com.github.se.gatherspot.ui.profile

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile

//Note : This warning is properly taken care of
@SuppressLint("MutableCollectionMutableState")
class OwnProfileViewModel : ViewModel() {
  private var _profile: Profile = ProfileFirebaseConnection().dummyFetch()
  private var _username by mutableStateOf(_profile.userName)
  private var _bio by mutableStateOf(_profile.bio)
  private var _image by mutableStateOf(_profile.image)
  private var _interests by mutableStateOf(_profile.interests.toMutableSet())
  val username: String
    get() = _username

  val bio: String
    get() = _bio

  val image: String
    get() = _image
  val interests: MutableSet<Interests>
    get() = _interests

  fun save() {
    _profile = Profile(_username, _bio, _image, "", _interests)
    // next: THIS NEEDS SANITIZATION
    ProfileFirebaseConnection().dummySave(_profile)
  }

  fun cancel() {
    _username = _profile.userName
    _bio = _profile.bio
    _image = _profile.image
    _interests = _profile.interests.toMutableSet()
  }

  fun updateUsername(userName: String) {
    _username = userName
  }

  fun updateBio(bio: String) {
    _bio = bio
  }

  fun updateProfileImage(image: String) {
    _image = image
  }

  fun swapInterest(interest: Interests, selected: Boolean) {
    val copy = _interests
    if (selected) copy.remove(interest)
    else copy.add(interest)
    _interests = copy
    println("Interests: $_interests")

  }
}

class ProfileViewModel(profile: Profile) {
  val username: String = profile.userName
  val bio: String = profile.bio
  val image: String = profile.image
}
