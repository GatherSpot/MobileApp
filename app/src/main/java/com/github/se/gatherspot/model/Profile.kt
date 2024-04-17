package com.github.se.gatherspot.model

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 */
class Profile
private constructor(
    private var _userName: String,
    private var _bio: String,
    private var _image: String,
    private var _interests: Set<Interests>,
    private val uid: String
) {
  var userName: String
    get() = _userName
    set(value) {
      val regex = Regex("^[a-zA-Z_\\-\\s]*$")
      if (value.isEmpty()) {
        Log.d("Profile", "Username is empty")
        return
      }
      if (!regex.matches(value)) {
        Log.d("Profile", "Username contains special characters")
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
      if (value.length > 100) throw IllegalArgumentException("Bio too long")
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

  fun save(userName: String, bio: String, image: String, interests: Set<Interests>) {
    this.userName = userName
    this.bio = bio
    this.image = image
    this._interests = interests
    saveToFirebase()
  }

  private val db = Firebase.firestore
  private val tag = "profiles"

  private fun saveToFirebase() {
    val data =
        hashMapOf(
            "userName" to userName,
            "bio" to bio,
            "image" to image,
            // TODO : change interests to make it more compact, maybe do it in its own class and not
            // here
            "interests" to Interests.toCompressedString(interests))
    db.collection(tag)
        .document(uid)
        .set(data)
        .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(tag, "Error writing document", e) }
  }

  fun updateFromFirebase(uid: String, update: () -> Unit) {
    val doc =
        db.collection(tag)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
              if (document != null) {
                userName = document.get("userName") as String
                bio = document.get("bio") as String
                image = document.get("image") as String
                interests = Interests.fromCompressedString(document.get("interests") as String)
                update()
                Log.d(tag, "DocumentSnapshot data: ${document.data}")
              } else {
                Log.d(tag, "No such document")
              }
            }
            .addOnFailureListener { exception -> Log.d(tag, "get failed with :", exception) }
  }

  fun Delete() {
    db.collection(tag)
        .document(uid)
        .delete()
        .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(tag, "Error deleting document", e) }
  }

  companion object {
    /**
     * Factory method to fetch a profile given a certain UID
     *
     * @param uid the unique identifier of the user
     * @param update a function to update the profile view when fetched from firebase, this is used
     *   to avoid locking, the profile will be empty until the update function is called. Might try
     *   to find a simpler solution.
     * @return a profile object
     */
    fun fromUID(uid: String, update: () -> Unit): Profile {
      if (uid.isEmpty()) throw IllegalArgumentException("UID cannot be empty")
      val profile = emptyProfile(uid)
      profile.updateFromFirebase(uid, update)
      return profile
    }

    /**
     * Factory method to create an empty profile useful creating a new profile on signup
     *
     * @param uid to get from firebase when creating new account
     * @return a profile object
     */
    private fun emptyProfile(uid: String): Profile {
      return Profile("", "", "", emptySet(), uid)
    }

    /**
     * Factory method to create a dummy profile useful for testing and prototyping
     *
     * @return a profile object
     */
    fun dummyProfile(): Profile {
      return Profile("John Doe", "I am not a bot", "", setOf(Interests.FOOTBALL), "TEST_UID")
    }
  }
}
