package com.github.se.gatherspot.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.gatherspot.model.Profile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OwnProfileViewModel : ViewModel() {
    private var _profile: Profile = Profile.fromUID(Firebase.auth.uid!!)
    private val _username = MutableLiveData<String>()
    private val _bio = MutableLiveData<String>()
    private val _image = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    val bio: LiveData<String>
        get() = _bio

    val image: LiveData<String>
        get() = _image

    init {
        _username.value = _profile.userName
        _bio.value = _profile.bio
        _image.value = _profile.image
    }

    fun save() {
        _profile.save(_username.value ?: "", bio.value ?: "", image.value ?: "", emptySet())
        // next: THIS NEEDS SANITIZATION
    }

    fun cancel() {
        _username.value = _profile.userName
        _bio.value = _profile.bio
        _image.value = _profile.image
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
}

class ProfileViewModel(profile: Profile) {
    val username: String = profile.userName
    val bio: String = profile.bio
    val image: String = profile.image
}
