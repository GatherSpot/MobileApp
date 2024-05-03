package com.github.se.gatherspot.ui.profile

import android.net.Uri
import android.net.Uri.EMPTY
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gatherspot.FirebaseImages
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

  init {
    // TODO: replace this with hilt injection
    _profile = ProfileFirebaseConnection().fetch(Firebase.auth.uid ?: "TEST") { update() }
  }

  val username: LiveData<String>
    get() = _username

  val bio: LiveData<String>
    get() = _bio

  val image: LiveData<String>
    get() = _image

  val interests: LiveData<Set<Interests>>
    get() = _interests

  fun saveText() {
    _profile.userName = _username.value!!
    _profile.bio = _bio.value!!
    _profile.interests = _interests.value!!
    ProfileFirebaseConnection().add(_profile)
  }

  fun cancelText() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
  }

  fun update() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
    _image.value = _profile.image
  }

  // TODO : add sanitization to these function !!!
  fun updateUsername(userName: String) {
    _username.value = userName
  }

  fun updateBio(bio: String) {
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

  fun isInterestsSelected(interest: Interests): Boolean {
    return interest in _interests.value!!
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
}

class ProfileViewModel(private val _target: String, private val nav: NavigationActions) {
  private var _profile: Profile
  private val _username = MutableLiveData<String>()
  private val _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _interests = MutableLiveData<Set<Interests>>()
  private val _id = Firebase.auth.uid ?: "TEST"
  private val _isFollowing = FollowList.isFollowing(_id, _target)
  val username: LiveData<String>
    get() = _username

  val bio: LiveData<String>
    get() = _bio

  val image: LiveData<String>
    get() = _image

  val interests: LiveData<Set<Interests>>
    get() = _interests

  val isFollowing: LiveData<Boolean>
    get() = _isFollowing

  init {
    let { _profile = ProfileFirebaseConnection().fetch(_target) { update() } }
  }

  private fun update() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
    _interests.value = _profile.interests.toMutableSet()
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _interests.value = _profile.interests
  }

  // TODO : replace ?: with hilt injection
  fun follow() {
    println("follow clicked")
    // unsure we disable functionality if we didn't fetch data yet, makes null asserted safe as a
    // bonus
    if (_isFollowing.isInitialized) {
      println("follow clicked 2")
      if (_isFollowing.value!!) FollowList.unfollow(_id, _target)
      else FollowList.follow(_id, _target)
      _isFollowing.value = !(_isFollowing.value!!)
    }
  }

  fun requestFriend() {
    // TODO : even if implemented this will not be visible until we add a friendrequest view, hence
    // I prefer to add ViewProfile functionality to other classes first
  }

  fun back() {
    // TODO : need to test this with either end to end test or manually when someone actually uses
    // this class
    nav.goBack()
  }
}
