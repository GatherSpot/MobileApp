package com.github.se.gatherspot.model

import java.util.BitSet

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 */
class Profile(
    private var _userName: String,
    private var _bio: String,
    private var _image: String,
    private var _interests: BitSet,
    private var _uid: String
) {

  var userName: String = _userName
  var bio: String = _bio
  var image: String = _image
  var interests: BitSet = _interests

  constructor() : this("", "", "", Interests.newBitset(), "")
}
