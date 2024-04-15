package com.github.se.gatherspot.model

import androidx.compose.runtime.produceState
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

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
    private var _interests: Set<Interests>
) {
  var userName: String
    get() = _userName
    set(value) {
      val regex = Regex("^[a-zA-Z_\\-\\s]*$")
      if (value.isEmpty())
        throw IllegalArgumentException("Username cannot be empty")
      if (!regex.matches(value))
        throw IllegalArgumentException("Username cannot contain special characters")
      if (value.length > 20)
        throw IllegalArgumentException("Username too long")
      _userName = value
    }
  var bio: String
    get() = _bio
    set(value) {
      if (value.length > 100)
        throw IllegalArgumentException("Bio too long")
      _bio = value
    }
  var image: String
    get() = _image
    set(value) = TODO()
  var interests: Set<Interests>
    get() = _interests
    set(value) { _interests = value }
  fun addInterest(interest: Interests) {
    _interests = _interests.plus(interest)
  }
  fun removeInterest(interest: Interests) {
    _interests = _interests.minus(interest)
  }
  fun swapInterest(interest: Interests) {
    if (_interests.contains(interest))
      removeInterest(interest)
    else
      addInterest(interest)
  }
  fun save(userName: String, bio: String, image: String, interests: Set<Interests>) {
    this.userName = userName
    this.bio = bio
    this.image = image
    this._interests = interests
    updateToFirebase()
  }
  fun toJson() : String {
    val gson = Gson()
    return gson.toJson(this)
  }
  fun fromJson(json: String) : Profile {
    val gson = Gson()
    return gson.fromJson(json, Profile::class.java)
  }
  //TODO : handle situation when there is no internet connection, or it is slow
  //TODO : handle situation when there is no profile on firebase with such uid
  private fun updateToFirebase(){
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("profiles")

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val json = this.toJson()

    if (uid != null) {
      myRef.child(uid).setValue(json)
    }
  }
  private fun updateFromFirebase(uid : String){
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("profiles")
    myRef.child(uid).get().addOnSuccessListener {
      val profile = fromJson(it.value.toString())
      _userName = profile.userName
      _bio = profile.bio
      _image = profile.image
      _interests = profile.interests
    }
  }
  companion object {
    /**
     * Factory method to fetch a profile given a certain UID
     * @param uid the unique identifier of the user
     * @return a profile object
     */
    fun fromUID(uid: String): Profile {
      if(uid.isEmpty())
        throw IllegalArgumentException("UID cannot be empty")
      val profile = emptyProfile(uid)
      profile.updateFromFirebase(uid)
      return profile
    }
    /**
     * Factory method to create an empty profile
     * useful creating a new profile on signup
     * @param uid to get from firebase when creating new account
     * @return a profile object
     */
    fun emptyProfile(uid: String): Profile {
      return Profile("", "", "", emptySet())
    }
    /**
     * Factory method to create a dummy profile
     * useful for testing and prototyping
     * @return a profile object
     */
    fun dummyProfile(): Profile {
      return Profile("John Doe", "I am not a bot", "", setOf(Interests.FOOTBALL))
    }
  }
}