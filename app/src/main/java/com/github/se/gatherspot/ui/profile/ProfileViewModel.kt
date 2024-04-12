package com.github.se.gatherspot.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import java.util.BitSet

class OwnProfileViewModel : ViewModel() {
  private var _profile: Profile
  private val _username = MutableLiveData<String>()
  private val _bio = MutableLiveData<String>()
  private val _image = MutableLiveData<String>()
  private val _interests = MutableLiveData<BitSet?>(null)
  val username: LiveData<String>
    get() = _username

  val bio: LiveData<String>
    get() = _bio

  val image: LiveData<String>
    get() = _image

  val interests: LiveData<BitSet?>
    get() = _interests

  init {
    _profile = ProfileFirebaseConnection().getDummyProfile()
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
    _interests.value = _profile.interests
  }

  fun save() {
    _profile =
        Profile(
            _username.value ?: "",
            bio.value ?: "",
            image.value ?: "",
            _interests.value ?: Interests.newBitset(),
            "")
    // next: THIS NEEDS SANITIZATION
    ProfileFirebaseConnection().updateDummyProfile(_profile)
  }

  fun cancel() {
    _username.value = _profile.userName
    _bio.value = _profile.bio
    _image.value = _profile.image
    _interests.value = _profile.interests
  }

  fun updateUsername(userName: String) {
    _username.value = userName
  }

  fun updateBio(bio: String) {
    _bio.value = bio
  }

  fun updateProfileImage(image: String) {
    _image.value = image
  }

  fun swapBit(ordinal: Int) {
    println("Swapping bit $ordinal")
    val newInterests = _interests.value?.clone() as BitSet
    newInterests.flip(ordinal)
    _interests.value = newInterests
    println("New bitset: ${_interests.value}")
  }
}

class ProfileViewModel(profile: Profile) {
  val username: String = profile.userName
  val bio: String = profile.bio
  val image: String = profile.image
  val interests: BitSet = profile.interests
}
