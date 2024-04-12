package com.github.se.gatherspot.model

import com.github.se.gatherspot.ProfileFirebaseConnection

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 */
class Profile private constructor(
    private var _userName: String,
    private var _bio: String,
    private var _image: String,
    private var _uid: String,
    private var _interests: Set<Interests>
) {
  val firebase = ProfileFirebaseConnection()
  companion object {
    /**
     * Factory method to fetch a profile given a certain UID
     * @param uid the unique identifier of the user
     * @return a profile object
     */
    fun fromUID(uid: String): Profile {
      if(uid.isEmpty())
        throw IllegalArgumentException("UID cannot be empty")
      return ProfileFirebaseConnection().fetchProfile(uid)
    }
    /**
     * Factory method to create an empty profile
     * useful creating a new profile on signup
     * @param uid to get from firebase when creating new account
     * @return a profile object
     */
    fun emptyProfile(uid: String): Profile {
      return Profile("", "", "", uid, emptySet())
    }
    /**
     * Factory method to create a dummy profile
     * useful for testing
     * @return a profile object
     */
    fun dummyProfile(): Profile {
      return Profile("John Doe", "I am not a bot", "", "", emptySet())
    }
  }
  var userName: String = _userName
  var bio: String = _bio
  var image: String = _image
  var interests: Set<Interests> = _interests
}
fun test(){
  val profile = Profile.fromUID("")

}
