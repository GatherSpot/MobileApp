package com.github.se.gatherspot.ui.profile

import android.net.Uri
import android.net.Uri.EMPTY
import android.util.Log
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
  private var _username = MutableLiveData<String>()
  private var _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _interests = MutableLiveData<Set<Interests>>()
  var userNameError = MutableLiveData("")
  var bioError = MutableLiveData("")
  private var _saved = MutableLiveData<Boolean>()
  private var userNameIsUniqueCheck = MutableLiveData(true)
  private var _isEditing = MutableLiveData(false)
  var uid = Firebase.auth.uid ?: "TEST" // TODO: remove elvis when emulator is in

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

  val saved: LiveData<Boolean>
    get() = _saved

  val isEditing: LiveData<Boolean>
    get() = _isEditing

  fun resetSaved() {
    _saved.value = false
  }

  fun edit() {
    _isEditing.value = true
  }

  fun saveText() {
    if (userNameError.value == "" && bioError.value == "" && userNameIsUniqueCheck.value == true) {
      _profile.userName = _username.value!!
      _profile.bio = _bio.value!!
      _profile.interests = _interests.value!!
      ProfileFirebaseConnection().add(_profile)
      _saved.value = true
    }
  }

  fun update() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
    _image.value = _profile.image
  }

  fun cancelText() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
  }

  fun updateUsername(userName: String) {
    _username.value = userName
    userNameIsUniqueCheck.value = false
    Profile.checkUsername(userName, null, userNameError) { userNameIsUniqueCheck.value = true }
  }

  fun updateBio(bio: String) {
    bioError = Profile.checkBio(bio)
    _bio.value = bio
  }

  fun updateProfileImage(newImageUrl: String) {
    _image.value = newImageUrl
  }

  fun uploadProfileImage(newImageUri: Uri?) {
    viewModelScope.launch {
      if (newImageUri != null || newImageUri != EMPTY) {
        Log.d("New image uri : ", newImageUri.toString())
        val newUrl = FirebaseImages().pushProfilePicture(newImageUri!!, _profile.id)
        if (newUrl.isNotEmpty()) {
          Log.d("Successfully uploaded: ", newUrl)
          updateProfileImage(newUrl)
          ProfileFirebaseConnection().update(_profile.id, "image", newUrl)
        }
      }
      imageEditAction.value = ImageEditAction.NO_ACTION
    }
  }

  fun removeProfilePicture() {
    viewModelScope.launch {
      FirebaseImages().removeProfilePicture(_profile.id)
      ProfileFirebaseConnection().update(_profile.id, "image", "")
      updateProfileImage("")
      imageEditAction.value = ImageEditAction.NO_ACTION
    }
  }

  fun flipInterests(interest: Interests) {
    _interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
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

  fun saveImage() {
    when (imageEditAction.value) {
      ImageEditAction.UPLOAD -> uploadProfileImage(localImageUriToUpload.value)
      ImageEditAction.REMOVE -> removeProfilePicture()
      else -> {}
    }
  }

  fun cancelImage() {
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
    // TODO : need to test this with either end to end test or manually when someone actually uses
    // this class
    NavigationActions(nav).goBack()
  }
}
