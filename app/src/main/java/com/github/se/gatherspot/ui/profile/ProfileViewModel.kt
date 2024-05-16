package com.github.se.gatherspot.ui.profile

import android.net.Uri
import android.net.Uri.EMPTY
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.FirebaseImages
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import kotlinx.coroutines.launch

class OwnProfileViewModel(private val profileFirebaseConnection: ProfileFirebaseConnection) :
    ViewModel() {
  private var _profile = MutableLiveData<Profile>()
  private var _username = MutableLiveData<String>()
  private var _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _interests = MutableLiveData<Set<Interests>>()

  init {
    viewModelScope.launch {
      _profile.postValue(
          profileFirebaseConnection.fetch(profileFirebaseConnection.getCurrentUserUid()!!))
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

  private fun saveText() {
    if (_profile.isInitialized) {
      _profile.value!!.userName = _username.value!!
      _profile.value!!.bio = _bio.value!!
      _profile.value!!.interests = _interests.value!!
      viewModelScope.launch { profileFirebaseConnection.add(_profile.value!!) }
    }
  }

  private fun cancelText() {
    if (_profile.isInitialized) {
      _username.value = _profile.value!!.userName
      _bio.value = _profile.value!!.bio
      _interests.value = _profile.value!!.interests
    }
  }

  // TODO : add sanitization to these function !!!
  fun updateUsername(userName: String) {
    _username.value = userName
  }

  fun updateBio(bio: String) {
    _bio.value = bio
  }

  private fun updateProfileImage(newImageUrl: String) {
    _image.value = newImageUrl
  }

  private fun uploadProfileImage(newImageUri: Uri?) {
    viewModelScope.launch {
      if (newImageUri != null || newImageUri != EMPTY) {
        Log.d("New image uri : ", newImageUri.toString())
        val newUrl = FirebaseImages().pushProfilePicture(newImageUri!!, _profile.value!!.id)
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
      FirebaseImages().removeProfilePicture(_profile.value!!.id)
      ProfileFirebaseConnection().update(_profile.value!!.id, "image", "")
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
    saveText()
    saveImage()
  }

  fun cancel() {
    cancelText()
    cancelImage()
  }
}

class ProfileViewModel(
    private val _target: String,
    private val nav: NavigationActions,
    private val profileFirebaseConnection: ProfileFirebaseConnection,
    private val followList: FollowList
) : ViewModel() {
  private var _profile = MutableLiveData<Profile>()
  private val _id = profileFirebaseConnection.getCurrentUserUid()!!
  private val _isFollowing = followList.isFollowing(_id, _target)
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
    viewModelScope.launch { _profile.value = profileFirebaseConnection.fetch(_target) }
  }

  // TODO : replace ?: with hilt injection
  fun follow() {
    if (_isFollowing.isInitialized) {
      if (_isFollowing.value!!) followList.unfollow(_id, _target)
      else followList.follow(_id, _target)
      _isFollowing.value = !(_isFollowing.value!!)
    }
  }

  fun requestFriend() {
    // TODO : even if implemented this will not be visible until we add a friendrequest view, hence
    // I prefer to add ViewProfile functionality to other classes first
  }

  fun back() {
    nav.goBack()
  }
}
