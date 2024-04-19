package com.github.se.gatherspot.model

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
        throw IllegalArgumentException("Username cannot be empty")
      }
      if (!regex.matches(value)) {
        throw IllegalArgumentException("Username can only contain letters, spaces, - and _")
      }
      if (value.length > 20) {
        throw IllegalArgumentException("Username too long")
      }
      _userName = value
    }
  var bio: String
    get() = _bio
    set(value) {
      if (value.length > 100) throw IllegalArgumentException("Bio too long")
      _bio = value
    }

  var image: String
    get() = _image
    set(value) {
      // TODO: SANITIZATION
      _image = value
    }

  var interests: Set<Interests>
    get() = _interests
    set(value) {
      _interests = value
    }

  constructor(id: String) : this("", "", "", "id", setOf())
}
