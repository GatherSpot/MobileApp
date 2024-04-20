package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFirebaseConnection {

  private val db = Firebase.firestore
  private val tag = FirebaseCollection.PROFILES.name

  /**
   * Fetches the profile from the database
   *
   * @param uid the id of the user
   * @param update the function to update the profile
   * @return the profile NOTE : The profile will be initially empty, to use it in a view, you need
   *   to update the view using with a lambda function that updates the view
   */
  fun updateFromFirebase(uid: String, update: () -> Unit): Profile {
    val profile = Profile("", "", "", uid, Interests.new())
    db.collection(tag)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document != null) {
            profile.userName = document.get("userName") as String
            profile.bio = document.get("bio") as String
            profile.image = document.get("image") as String
            profile.interests = Interests.fromCompressedString(document.get("interests") as String)
            update()
            Log.d(tag, "DocumentSnapshot data: ${document.data}")
          } else {
            Log.d(tag, "No such document")
          }
        }
        .addOnFailureListener { exception -> Log.d(tag, "get failed with :", exception) }
    return profile
  }

  /**
   * Saves the profile to the database
   *
   * @param profile the profile to save
   */
  fun saveToFirebase(profile: Profile) {
    val data =
        hashMapOf(
            "userName" to profile.userName,
            "bio" to profile.bio,
            "image" to profile.image,
            "interests" to Interests.toCompressedString(profile.interests))
    db.collection(tag)
        .document(profile.id)
        .set(data)
        .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(tag, "Error writing document", e) }
  }

  /**
   * Deletes the profile from the database
   *
   * @param id the id of the profile to delete
   */
  fun deleteFromFirebase(id: String) {
    // TODO : WE WILL NEED TO REMOVE EVERYTHING RELATED TO THE USER, LIKE REGISTERED_EVENTS, etc...
    // probably want to do that at a later date in the project, when the picture is complete
    db.collection(tag)
        .document(id)
        .delete()
        .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(tag, "Error deleting document", e) }
  }
}
