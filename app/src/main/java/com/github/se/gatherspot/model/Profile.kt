package com.github.se.gatherspot.model

import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.CollectionClass
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 * @param _interests the interests of the user
 * @param id the id of the user
 */
class Profile(
    private var _userName: String,
    private var _bio: String,
    private var _image: String,
    override val id: String,
    private var _interests: Set<Interests>,
) : CollectionClass() {
  var userName: String
    get() = _userName
    set(value) {
      val regex = Regex("^[a-zA-Z_\\-\\s]*$")
      if (value.isEmpty()) {
        throw IllegalArgumentException("Username cannot be empty")
      }
      if (!regex.matches(value)) {
        throw IllegalArgumentException(
            "Username can only contain letters, spaces, hyphens, and underscores")
      }
      if (value.length > 20) {
        throw IllegalArgumentException("Username cannot be longer than 20 characters")
      }
      _userName = value
    }

  var bio: String
    get() = _bio
    set(value) {
      if (value.length > 100) {
        throw IllegalArgumentException("Bio cannot be longer than 100 characters")
      }
      _bio = value
    }

  var image: String
    get() = _image
    set(value) {
      _image = value
    }

  var interests: Set<Interests>
    get() = _interests
    set(value) {
      _interests = value
    }

  companion object {
    fun testOrganizer(): Profile {
      return Profile("John Doe", "I am not a bot", "", "TEST", setOf(Interests.FOOTBALL))
    }

    fun testParticipant(): Profile {
      return Profile("Steeve", "I play pokemon go", "", "TEST2", setOf(Interests.FOOTBALL))
    }

    fun fromFirebase(id: String, onSuccess: () -> Unit) {
      ProfileFirebaseConnection().fetch(id) { onSuccess() }
    }

    fun empty(id: String) = Profile("", "", "", id, setOf())

    /**
     * Check if a username is valid
     * an old username can be optionally given to avoid telling we can't use our own username
     * @param newName the new username
     * @param oldName the old username
     * @return a string with the error message if there is one
     */
    fun checkUsername(newName: String, oldName: String?): MutableLiveData<String> {
      val res = MutableLiveData("")
      val regex = Regex("^[a-zA-Z0-9_\\-\\s]*$")
      if (newName.isEmpty()) {
        res.value = "Username cannot be empty"
      }
      if (!regex.matches(newName)) {
        res.value = "Username can only contain letters, numbers, spaces, hyphens, and underscores"
      }
      if (newName.length > 20) {
        res.value = "Username cannot be longer than 20 characters"
      }
      if (newName != oldName) {
        ProfileFirebaseConnection().ifUsernameExists(newName) {
          if (it) res.value = "Username already taken"
          }
        }
        return res
    }
      fun checkBio(bio: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        if (bio.length > 100) {
          res.value = "Bio cannot be longer than 100 characters"
        }
        return res
      }
  }
}
