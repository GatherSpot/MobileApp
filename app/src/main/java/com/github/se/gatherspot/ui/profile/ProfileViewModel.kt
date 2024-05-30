package com.github.se.gatherspot.ui.profile

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.FirebaseImages
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.sql.AppDatabase
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** ViewModel for the user's own profile. */
class OwnProfileViewModel(private val db: AppDatabase) : ViewModel() {
  private var _profile =
      MutableLiveData<Profile?>(Profile("", "", "", Firebase.auth.currentUser?.uid ?: "", setOf()))
  private var _oldProfile: Profile? = null

  private var _userNameError = MutableLiveData("")
  private var _bioError = MutableLiveData("")
  private var _userNameIsUniqueCheck = MutableLiveData(true)
  private var _isEditing = MutableLiveData(false)
  val uid = Firebase.auth.uid ?: "TEST"

  init {
    viewModelScope.launch(Dispatchers.IO) {
      // For retro compatibility reasons, we still have to add a fetch from firebase fallback,
      // unless we delete every account
      val profileFromRoom = db.ProfileDao().get(uid)
      if (profileFromRoom != null) {
        _profile.postValue(profileFromRoom)
        _oldProfile = profileFromRoom.copy()
      } else {
        val profileFromFirebase = ProfileFirebaseConnection().fetch(uid)
        profileFromFirebase?.let {
          db.ProfileDao().insert(profileFromFirebase)
          _profile.postValue(profileFromFirebase)
          _oldProfile = profileFromFirebase.copy()
        }
      }
    }
  }

  val username: LiveData<String>
    get() = _profile.map { it?.userName ?: "" }

  val bio: LiveData<String>
    get() = _profile.map { it?.bio ?: "" }

  val image: LiveData<String>
    get() = _profile.map { it?.image ?: "" }

  val interests: LiveData<Set<Interests>>
    get() = _profile.map { it?.interests ?: setOf() }

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

  /**
   * Update the username of the profile.
   *
   * @param userName The new username
   */
  fun updateUsername(userName: String) {
    _profile.value?.userName = userName
    _profile.value.apply {
      this?.userName = userName
      _profile.value = this
    }
    viewModelScope.launch {
      try {
        Profile.checkUsername(userName, _oldProfile?.userName, _userNameError)
        _userNameIsUniqueCheck.postValue(
            true) // userName might not be unique depending on how checkUsername goes. usernameError
        // == "" already signifies uniqueness of username
      } catch (e: Exception) {
        // TODO show error dialog
      }
    }
  }

  /**
   * Update the bio of the profile.
   *
   * @param bio The new bio
   */
  fun updateBio(bio: String) {
    Profile.checkBio(bio, _bioError)
    _profile.value?.apply {
      this.bio = bio
      _profile.value = this
    }
  }

  /**
   * Update the profile image.
   *
   * @param url The new image URL
   */
  fun updateProfileImage(url: String) {
    _profile.value?.image = url
    _profile.value?.apply {
      this.image = url
      _profile.value = this
    }
  }

  /** Remove the profile picture. */
  fun removeProfilePicture() {
    viewModelScope.launch(Dispatchers.IO) {
      _profile.value?.let {
        FirebaseImages().removeProfilePicture(it.id)
        ProfileFirebaseConnection().update(it.id, "image", "")
        db.ProfileDao().insert(_profile.value!!.withNewImage(""))
      }
    }
    updateProfileImage("")
  }

  /**
   * Flip an interest.
   *
   * @param interest The interest to flip
   */
  fun flipInterests(interest: Interests) {
    _profile.value?.apply {
      this.interests = Interests.flipInterest(interests, interest)
      _profile.value = this
    }
  }

  private fun noErrors(): Boolean {
    return _userNameError.value == "" &&
        _bioError.value == "" &&
        _userNameIsUniqueCheck.value ==
            true // usernameIsUniquecheck is used in conjunction with userNameError which is
    // sufficient on its own
  }

  /** Save the edited profile and exit editing mode. */
  fun save() {
    if (noErrors()) {
      viewModelScope.launch(Dispatchers.IO) {
        try {
          // only do it if profile is initialized
          _profile.value?.let {
            // get new image if needed
            val img = _profile.value?.image
            var newUrl: String? = null
            if (!img.isNullOrEmpty() && img != _oldProfile!!.image) {
              newUrl = FirebaseImages().pushProfilePicture(img.toUri(), uid)
            }
            val newProfile = _profile.value!!.withNewImage(newUrl)
            ProfileFirebaseConnection().add(newProfile)
            db.ProfileDao().insert(newProfile)
            _oldProfile = newProfile.copy()
          }
        } catch (e: Exception) {
          // TODO show error dialog
          Log.d("Profile", "${e.message}")
          _profile.postValue(_oldProfile)
        }
      }
    } else
      cancel()
  }

  /** Cancel the editing of the profile and exit editing mode. */
  fun cancel() {
    _oldProfile?.let { _profile.value = it }
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
class ProfileViewModel(
    val target: String,
    private val nav: NavHostController,
    private val db: AppDatabase
) : ViewModel() {
  private var _profile = MutableLiveData<Profile>()
  private var _isFollowing = MutableLiveData(false)
  private var uid = Firebase.auth.uid ?: "TEST"
  val username: LiveData<String>
    get() = _profile.map { it?.userName ?: "" }

  val profile: LiveData<Profile>
    get() = _profile

  val bio: LiveData<String>
    get() = _profile.map { it?.bio ?: "" }

  val image: LiveData<String>
    get() = _profile.map { it?.image ?: "" }

  val interests: LiveData<Set<Interests>>
    get() = _profile.map { it?.interests ?: setOf() }

  val isFollowing: LiveData<Boolean>
    get() = _isFollowing

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _profile.postValue(ProfileFirebaseConnection().fetch(target))
      _isFollowing.postValue(FollowList.isFollowing(uid, target))
    }
  }

  /** Toggle the following status of the user. */
  fun follow() {
    if (_profile.isInitialized) {
      if (_isFollowing.value == null) return
      viewModelScope.launch(Dispatchers.IO) {
        if (_isFollowing.value!!) {
          FollowList.unfollow(uid, target)
        } else {
          FollowList.follow(Firebase.auth.uid ?: "TEST", target)
        }
        _isFollowing.postValue(!(_isFollowing.value!!))
        // update the local database with online value
        db.IdListDao().insert(IdList.fromFirebase(uid, FirebaseCollection.FOLLOWING))
      }
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
