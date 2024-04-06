package com.github.se.gatherspot.data
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 */
class Profile(private var _userName: String, private var _bio: String, private var _image: String) :
  DataType<Profile>() {
  /** Profile data object Creates a empty profile. */
  constructor() : this("", "", "")
  /**
   * Username getter
   *
   * @return username
   */
  fun getUserName(): String {
    return _userName
  }
  /**
   * Username setter
   *
   * @param name username non-alphanumeric characters are removed
   */
  fun setUserName(name: String) {
    val sanitized = name.replace("[^A-Za-z0-9 ]".toRegex(), "")
    _userName = sanitized
  }
  /**
   * Bio getter
   *
   * @return the bio of the profile
   */
  fun getBio(): String {
    return _bio
  }
  /**
   * Bio setter
   *
   * @param bio the bio of the profile
   */
  fun setBio(bio: String) {
    _bio = bio
  }

  /**
   * Image getter
   *
   * @return the image of the profile, may be empty if no image
   */
  fun getImage(): String {
    return _image
  }
  /**
   * Image setter
   *
   * @param image the image of the profile, may be empty if no image
   */
  fun setImage(image: String) {
    _image = image
  }
}
