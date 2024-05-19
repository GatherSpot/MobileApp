package com.github.se.gatherspot.ui.profile

import android.net.Uri
import android.net.Uri.EMPTY
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.github.se.gatherspot.firebase.FirebaseImages
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class OwnProfileViewModel : ViewModel() {
  private lateinit var _profile: Profile
  private var _username = MutableLiveData("")
  private var _bio = MutableLiveData("")
  private val _image = MutableLiveData("")
  private val _interests = MutableLiveData<Set<Interests>>()
  private var _userNameError = MutableLiveData("")
  private var _bioError = MutableLiveData("")
  private var _userNameIsUniqueCheck = MutableLiveData(true)
  private var _isEditing = MutableLiveData(false)
  val uid = Firebase.auth.uid ?: "TEST"

  init {
    viewModelScope.launch { _profile = ProfileFirebaseConnection().fetch(uid) { update() } }
  }

  val username: LiveData<String>
    get() = _username

  val bio: LiveData<String>
    get() = _bio

  val image: LiveData<String>
    get() = _image

  val interests: LiveData<Set<Interests>>
    get() = _interests

  val isEditing: LiveData<Boolean>
    get() = _isEditing

  val userNameError: LiveData<String>
    get() = _userNameError

  val bioError: LiveData<String>
    get() = _bioError

  fun edit() {
    _isEditing.value = true
  }

  private fun saveText() {
    if (_userNameError.value == "" &&
        _bioError.value == "" &&
        _userNameIsUniqueCheck.value == true) {
      _profile.userName = _username.value!!
      _profile.bio = _bio.value!!
      _profile.interests = _interests.value!!
      ProfileFirebaseConnection().add(_profile)
    }
  }

  fun update() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
    _image.value = _profile.image
  }

  private fun cancelText() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
  }

  fun updateUsername(userName: String) {
    _username.value = userName
    _userNameIsUniqueCheck.value = false
    Profile.checkUsername(userName, null, _userNameError) { _userNameIsUniqueCheck.value = true }
  }

  fun updateBio(bio: String) {
    _bioError = Profile.checkBio(bio)
    _bio.value = bio
  }

  fun updateProfileImage(url: String) {
    _image.value = url
  }

  private fun uploadProfileImage(imageUri: Uri?) {
    viewModelScope.launch {
      if (imageUri != null && imageUri != EMPTY) {
        val newUrl = FirebaseImages().pushProfilePicture(imageUri, _profile.id)
        if (newUrl.isNotEmpty()) {
          updateProfileImage(newUrl)
          ProfileFirebaseConnection().update(_profile.id, "image", newUrl)
        }
      }
    }
  }

  fun removeProfilePicture() {
    viewModelScope.launch {
      FirebaseImages().removeProfilePicture(_profile.id)
      ProfileFirebaseConnection().update(_profile.id, "image", "")
      updateProfileImage("")
    }
  }

  fun flipInterests(interest: Interests) {
    _interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  private fun saveImage() {
    if (!image.value.isNullOrEmpty()) {
      uploadProfileImage(image.value!!.toUri())
    }
  }

  fun save() {
    saveImage()
    saveText()
    _isEditing.value = false
  }

  fun cancel() {
    cancelText()
    _isEditing.value = false
  }

  fun logout(nav: NavigationActions) {
    Firebase.auth.signOut()
    nav.controller.navigate("auth")
  }
}

class ProfileViewModel(val target: String, private val nav: NavHostController) : ViewModel() {
  private var _profile = MutableLiveData<Profile>()
  private var _isFollowing = MutableLiveData(false)
  val username: LiveData<String>
    get() = _profile.map { it.userName }

  val profile: LiveData<Profile>
    get() = _profile

  val bio: LiveData<String>
    get() = _profile.map { it.bio }

  val image: LiveData<String>
    get() = _profile.map { it.image }

  val interests: LiveData<Set<Interests>>
    get() = _profile.map { it.interests }

  val isFollowing: LiveData<Boolean>
    get() = _isFollowing

  init {
    viewModelScope.launch {
      _profile.postValue(ProfileFirebaseConnection().fetch(target))
      _isFollowing = FollowList.isFollowing(Firebase.auth.uid!!, target)
    }
  }

  // TODO : replace ?: with hilt injection
  fun follow() {
    if (_profile.isInitialized) {
      if (_isFollowing.value == null) return
      if (_isFollowing.value!!) FollowList.unfollow(_profile.value!!.id, target)
      else FollowList.follow(_profile.value!!.id, target)
      _isFollowing.value = !(_isFollowing.value!!)
    }
  }

  fun requestFriend() {
    // TODO : even if implemented this will not be visible until we add a friend request view, hence
    // I prefer to add ViewProfile functionality to other classes first
  }

  fun back() {
    NavigationActions(nav).goBack()
  }
}
