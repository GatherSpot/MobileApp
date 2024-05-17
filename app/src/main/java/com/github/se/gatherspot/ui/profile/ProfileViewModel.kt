package com.github.se.gatherspot.ui.profile

import android.net.Uri
import android.net.Uri.EMPTY
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.firebase.FirebaseImages
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class OwnProfileViewModel : ViewModel() {
  private var profileCopy: Profile? = null
  private var _profile = MutableLiveData<Profile>()
  private var _usernameValid = MutableLiveData("")
  private var _bioValid = MutableLiveData("")
  private var _saved = MutableLiveData<Boolean>()
  private var _userNameIsUniqueCheck = MutableLiveData(true)
  private var _isEditing = MutableLiveData(false)

  init {
    viewModelScope.launch {
      profileCopy = async { ProfileFirebaseConnection().fetch(Firebase.auth.uid!!) }.await()
      profileCopy?.let { _profile.postValue(profileCopy!!) }
    }
  }

  val profile: LiveData<Profile>
    get() = _profile

  val username: LiveData<String>
    get() = _profile.map { it.userName }

  val userNameValid: LiveData<String>
    get() = _usernameValid

  val bio: LiveData<String>
    get() = _profile.map { it.bio }

  val bioValid: LiveData<String>
    get() = _bioValid

  val image: LiveData<String>
    get() = _profile.map { it.image }

  val interests: LiveData<Set<Interests>>
    get() = _profile.map { it.interests }

  val saved: LiveData<Boolean>
    get() = _saved

  val isEditing: LiveData<Boolean>
    get() = _isEditing

  fun resetSaved() {
    _saved.value = false
  }

  private fun saveText() {
    if (_usernameValid.value == "" &&
        _bioValid.value == "" &&
        _userNameIsUniqueCheck.value == true) {
      _profile.value?.let { ProfileFirebaseConnection().add(it) }
      _saved.value = true
    }
  }

  private fun cancelText() {
    profileCopy?.let { _profile.value = it }
  }

  // TODO : add sanitization to these function !!!
  fun updateUsername(userName: String) {
    _profile.value?.userName = userName
    _userNameIsUniqueCheck.value = false
    _usernameValid =
        Profile.checkUsername(userName, _profile.value?.userName) {
          _userNameIsUniqueCheck.value = true
        }
  }

  fun updateBio(bio: String) {
    _bioValid = Profile.checkBio(bio)
    _profile.value?.bio = bio
  }

  private fun updateProfileImage(newImageUrl: String) {
    _profile.value?.image = newImageUrl
  }

  private fun uploadProfileImage(newImageUri: Uri?) {
    viewModelScope.launch {
      if (newImageUri != null && newImageUri != EMPTY && _profile.isInitialized) {
        Log.d("New image uri : ", newImageUri.toString())
        val newUrl = FirebaseImages().pushProfilePicture(newImageUri, _profile.value!!.id)
        if (newUrl.isNotEmpty()) {
          Log.d("Successfully uploaded: ", newUrl)
          updateProfileImage(newUrl)
          ProfileFirebaseConnection().update(_profile.value!!.id, "image", newUrl)
        }
      }
      imageEditAction.value = ImageEditAction.NO_ACTION
    }
  }

  private fun removeProfilePicture() {
    viewModelScope.launch {
      if (_profile.isInitialized) FirebaseImages().removeProfilePicture(_profile.value!!.id)
      ProfileFirebaseConnection().update(_profile.value!!.id, "image", "")
      updateProfileImage("")
      imageEditAction.value = ImageEditAction.NO_ACTION
    }
  }

  fun flipInterests(interest: Interests) {
    _profile.value?.interests = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  enum class ImageEditAction {
    NO_ACTION,
    UPLOAD,
    REMOVE
  }

  var localImageUriToUpload = MutableLiveData(Uri.EMPTY)

  fun setLocalImageUriToUpload(uri: Uri) {
    localImageUriToUpload.value = uri
  }

  var imageEditAction = MutableLiveData(ImageEditAction.NO_ACTION)

  fun setImageEditAction(newImageEditAction: ImageEditAction) {
    if (newImageEditAction == ImageEditAction.REMOVE) {
      localImageUriToUpload.value = Uri.EMPTY
    }
    imageEditAction.value = newImageEditAction
  }

  private fun saveImage() {
    when (imageEditAction.value) {
      ImageEditAction.UPLOAD -> uploadProfileImage(localImageUriToUpload.value)
      ImageEditAction.REMOVE -> removeProfilePicture()
      else -> {}
    }
  }

  private fun cancelImage() {
    imageEditAction.value = ImageEditAction.NO_ACTION
    localImageUriToUpload.value = Uri.EMPTY
  }

  fun save() {
    saveImage()
    saveText()
    _isEditing.value = false
  }

  fun cancel() {
    cancelText()
    cancelImage()
    _isEditing.value = false
  }

  fun edit() {
    _isEditing.value = true
  }

  fun logout(nav: NavigationActions) {
    Firebase.auth.signOut()
    nav.controller.navigate("auth")
  }
}

class ProfileViewModel(private val _target: String, private val nav: NavigationActions) :
    ViewModel() {
  var _profile = MutableLiveData<Profile>()
  private var _isFollowing = MutableLiveData(false)
  val username: LiveData<String>
    get() = _profile.map { it.userName }

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
      runBlocking { _profile.postValue(ProfileFirebaseConnection().fetch(_target)) }
      _isFollowing = FollowList.isFollowing(_profile.value!!.id, _target)
    }
  }

  // TODO : replace ?: with hilt injection
  fun follow() {
    if (_profile.isInitialized) {
      if (_isFollowing.value!!) FollowList.unfollow(_profile.value!!.id, _target)
      else FollowList.follow(_profile.value!!.id, _target)
      _isFollowing.value = !(_isFollowing.value!!)
    }
  }

  fun requestFriend() {
    // TODO : even if implemented this will not be visible until we add a friend request view, hence
    // I prefer to add ViewProfile functionality to other classes first
  }

  fun back() {
    // TODO : need to test this with either end to end test or manually when someone actually uses
    // this class
    nav.goBack()
  }
}
