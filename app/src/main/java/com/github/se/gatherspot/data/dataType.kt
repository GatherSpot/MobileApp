package com.github.se.gatherspot.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Note: every data object should inherit from DataType to make it consistent, and group data
// objects at the same place
// Warn : DO SANITIZATION AND USE SETTERS AND GETTERS

abstract class DataType<T> {
  /**
   * Converts a DataType object to a json string
   *
   * @return the json string
   */
  fun toJson(): String {
    return Gson().toJson(this)
  }
  /**
   * Converts a json string to a DataType object
   *
   * @param json the json string to convert
   * @return the DataType object
   */
  fun fromJson(json: String): T {
    val type = object : TypeToken<Profile>() {}.type
    return Gson().fromJson(json, type)
  }
}

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
    val sanitized = name.replace("[^A-Za-z0-9]".toRegex(), "")
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
   * @param bio the bio of the profile accepts null
   */
  fun setBio(bio: String) {
    val sanitized = bio.replace("[^A-Za-z0-9 .,?!]".toRegex(), "")
    val shortened = sanitized.take(40)
    _bio = shortened
  }

  /**
   * Image getter
   *
   * @return the image of the profile
   */
  fun getImage(): String {
    return _image
  }
  /**
   * Image setter
   *
   * @param image the image of the profile accepts null
   */
  fun setImage(image: String) {
    // Todo: check if link is correct and file size and format,...
    _image = image
  }
}
