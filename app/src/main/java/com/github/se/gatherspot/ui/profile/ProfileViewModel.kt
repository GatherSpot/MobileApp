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

/** ViewModel for the user's own profile. */
class OwnProfileViewModel : ViewModel() {
  private lateinit var _profile: Profile
  private var _username = MutableLiveData("")
  private var _bio = MutableLiveData("")
  private val _image = MutableLiveData("")
  private val _interests = MutableLiveData<Set<Interests>>(setOf())
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

  /** Toggle the variable coding for whether the profile is being edited */
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

  /** Update the profile with the current values. */
  fun update() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
    _image.value = _profile.image
  }

  /** Cancel the editing of the profile. */
  private fun cancelText() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
  }

  /**
   * Update the username of the profile.
   *
   * @param userName The new username
   */
  fun updateUsername(userName: String) {
    _username.value = userName
    _userNameIsUniqueCheck.value = false
    Profile.checkUsername(userName, null, _userNameError) { _userNameIsUniqueCheck.value = true }
  }

  /**
   * Update the bio of the profile.
   *
   * @param bio The new bio
   */
  fun updateBio(bio: String) {
    _bioError = Profile.checkBio(bio)
    _bio.value = bio
  }

  /**
   * Update the profile image.
   *
   * @param url The new image URL
   */
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

  /** Remove the profile picture. */
  fun removeProfilePicture() {
    viewModelScope.launch {
      FirebaseImages().removeProfilePicture(_profile.id)
      ProfileFirebaseConnection().update(_profile.id, "image", "")
      updateProfileImage("")
    }
  }

  /**
   * Flip an interest.
   *
   * @param interest The interest to flip
   */
  fun flipInterests(interest: Interests) {
    _interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  private fun saveImage() {
    if (!image.value.isNullOrEmpty()) {
      uploadProfileImage(image.value!!.toUri())
    }
  }

  /** Save the edited profile and exit editing mode. */
  fun save() {
    saveImage()
    saveText()
    _isEditing.value = false
  }

  /** Cancel the editing of the profile and exit editing mode. */
  fun cancel() {
    cancelText()
    _isEditing.value = false
  }

  /** Log out the user. */
  fun logout(nav: NavigationActions) {
    Firebase.auth.signOut()
    nav.controller.navigate("auth")
  }
}

/**
 * ViewModel for viewing another user's profile.
 *
 * @param target The target user's ID
 * @param nav The navigation controller
 */
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
      _isFollowing = FollowList.isFollowing(Firebase.auth.uid ?: "TEST", target)
    }
  }

  /** Toggle the following status of the user. */
  fun follow() {
    if (_profile.isInitialized) {
      if (_isFollowing.value == null) return
      if (_isFollowing.value!!) FollowList.unfollow(Firebase.auth.uid ?: "TEST", target)
      else FollowList.follow(Firebase.auth.uid ?: "TEST", target)
      _isFollowing.value = !(_isFollowing.value!!)
    }
  }

  /**
   * NOT IMPLEMENTED
   *
   * Request to be friends with the user.
   */
  fun requestFriend() {}

  /** Navigate back. */
  fun back() {
    NavigationActions(nav).goBack()
  }
}
