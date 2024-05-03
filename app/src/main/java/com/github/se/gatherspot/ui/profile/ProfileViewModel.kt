package com.github.se.gatherspot.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class OwnProfileViewModel : ViewModel() {
  private lateinit var _profile: Profile
  private var _username = MutableLiveData<String>()
  private var _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _interests = MutableLiveData<Set<Interests>>()
  private var _usernameValid = MutableLiveData<String>()
  private var _bioValid = MutableLiveData<String>()

  init {
    // TODO: replace this with hilt injection
    _profile = ProfileFirebaseConnection().fetch(Firebase.auth.uid ?: "TEST") { update() }
  }

  val username: LiveData<String>
    get() = _username

  val userNameValid: LiveData<String>
    get() = _usernameValid

  val bio: LiveData<String>
    get() = _bio

  val bioValid: LiveData<String>
    get() = _bioValid

  val image: LiveData<String>
    get() = _image

  val interests: LiveData<Set<Interests>>
    get() = _interests

  fun save() {
    _profile.userName = _username.value!!
    _profile.bio = _bio.value!!
    _profile.image = _image.value ?: ""
    _profile.interests = _interests.value!!
    ProfileFirebaseConnection().add(_profile)
  }

  fun update() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
    _interests.value = _profile.interests
  }

  // TODO : add sanitization to these function !!!
  fun updateUsername(userName: String) {
    _username.value = userName
    _usernameValid = Profile.checkUsername(userName, _profile.userName)
  }

  fun updateBio(bio: String) {
    _bioValid = Profile.checkBio(bio)
    _bio.value = bio
  }

  fun updateProfileImage(image: String) {
    _image.value = image
  }

  fun flipInterests(interest: Interests) {
    _interests.value = Interests.flipInterest(interests.value ?: setOf(), interest)
  }

  fun isInterestsSelected(interest: Interests): Boolean {
    return interest in _interests.value!!
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
