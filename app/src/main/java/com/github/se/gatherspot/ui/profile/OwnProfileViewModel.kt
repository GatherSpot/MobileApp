package com.github.se.gatherspot.ui.profile

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Note : This warning is properly taken care of
@SuppressLint("MutableCollectionMutableState")
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
    runBlocking {

      _profile = ProfileFirebaseConnection().fetch(Firebase.auth.currentUser!!.uid)!!
      _username.value = _profile.userName
      _bio.value = _profile.bio
      _image.value = _profile.image
    }
  }

  fun save() {
    _profile =
        Profile(
            username.value ?: "",
            bio.value ?: "",
            image.value ?: "",
            "",
            interests.value ?: mutableSetOf())
    // next: THIS NEEDS SANITIZATION
    ProfileFirebaseConnection().dummySave(_profile)
  }

  fun cancel() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
    _interests.value = _profile.interests.toMutableSet()
  }

  fun updateUsername(userName: String) {
    _username.value = userName
    println("Username: $userName")
  }

  fun updateBio(bio: String) {
    _bio.value = bio
  }

  fun updateProfileImage(image: String) {
    _image.value = image
  }

  fun swapInterest(interest: Interests, selected: Boolean) {
    val copy = interests.value?.toMutableSet() ?: mutableSetOf()
    if (selected) copy.remove(interest) else copy.add(interest)
    _interests.value = copy
    println("Interests: $_interests")
  }
}

class ProfileViewModel(profile: Profile) {
  val username: String = profile.userName
  val bio: String = profile.bio
  val image: String = profile.image
  val interests: Set<Interests> = profile.interests
}
