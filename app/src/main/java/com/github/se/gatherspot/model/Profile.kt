package com.github.se.gatherspot.model

//NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 */
class Profile(private var _userName: String,private var _bio: String,private var _image: String, private val _uid: String) {

  var userName: String = _userName
  var bio: String = _bio
  var image: String = _image

  constructor() : this("", "", "","")
}