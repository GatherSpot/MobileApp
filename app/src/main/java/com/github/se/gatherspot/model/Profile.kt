package com.github.se.gatherspot.model

import android.util.Log
import com.github.se.gatherspot.CollectionClass

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
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
        Log.d("Profile", "Username cannot be empty")
        return
      }
      if (!regex.matches(value)) {
        Log.d("Profile", "Username can only contain letters, spaces, - and _")
        return
      }
      if (value.length > 20) {
        Log.d("Profile", "Username too long")
        return
      }
      _userName = value
    }
  var bio: String
    get() = _bio
    set(value) {
      if (value.length > 100) {
        Log.d("Profile", "Bio too long")
        return
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
    fun test(): Profile {
      return Profile("John Doe", "I am not a bot", "", "TEST", setOf(Interests.FOOTBALL))
    }
  }

  constructor(id: String) : this("", "", "", "id", setOf())
}
