package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class ProfileFirebaseConnection : FirebaseConnectionInterface<Profile> {

  override val COLLECTION = FirebaseCollection.PROFILES.toString().lowercase()
  override val TAG = "FirebaseConnection" // Used for debugging/logs

  /**
   * Fetches the profile from the database
   *
   * @param id the id of the user
   * @param onSuccess lambda returned when fetched, useful to update the viewModel
   * @return the profile NOTE : The profile will be initially empty, to use it in a view, you need
   *   to update the view using with a lambda function that updates the view
   */
  fun fetch(id: String, onSuccess: () -> Unit): Profile {
    Log.d(TAG, "id: $id")
    val profile = Profile("", "", "", id, Interests.new())
    Firebase.firestore
        .collection(TAG)
        .document(id)
        .get()
        .addOnSuccessListener { document ->
          if (document != null) {
            Log.d(TAG, "Document is empty")
            profile.userName = document.get("userName") as String
            profile.bio = document.get("bio") as String
            profile.image = document.get("image") as String
            profile.interests = Interests.fromCompressedString(document.get("interests") as String)
            onSuccess()
            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
          } else {
            Log.d(TAG, "No such document")
          }
        }
        .addOnFailureListener { exception -> Log.d(TAG, "get failed with :", exception) }
    return profile
  }

  fun ifUsernameExists(userName: String, onComplete: (Boolean) -> Unit) {

    var res = false
    Firebase.firestore
        .collection(COLLECTION)
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if (document.get("userName") == userName) {
              res = true
            }
            if (res) {
              break
            }
          }
          onComplete(res)
        }
        .addOnFailureListener { onComplete(true) }
  }

  fun fetchFromUserName(userName: String): Profile? {
    var profile: Profile? = null
    Firebase.firestore
        .collection(COLLECTION)
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if (document.get("userName") == userName) {
              profile = getFromDocument(document)
            }
          }
        }
        .addOnFailureListener { Log.d(TAG, "Error getting documents: ", it) }
    return profile
  }

  override fun add(element: Profile) {
    val data =
        hashMapOf(
            "userName" to element.userName,
            "bio" to element.bio,
            "image" to element.image,
            "interests" to Interests.toCompressedString(element.interests))
    Firebase.firestore
        .collection(COLLECTION)
        .document(element.id)
        .set(data)
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
  }

  override fun update(id: String, field: String, value: Any) {
    when (field) {
      "interests" -> {
        when (value) {
          is Set<*> -> {
            updateInterests(id, value as Set<Interests>)
            return
          }
          is List<*> -> { // This is already a misuse case please don't land here
            Log.d(TAG, "Please use a Set instead of a List when updating interests of a profile")
            updateInterests(id, value.toSet() as Set<Interests>)
            return
          }
          is String -> {
            super.update(id, field, value)
            return
          }
        }
      }
      "userName" -> {
        ifUsernameExists(value as String) { exists ->
          if (exists) {
            Log.d(TAG, "Username already exists")
            return@ifUsernameExists
          }
        }
      }
        "registeredEvents" -> {
          updateRegisteredEvents(id, value as Set<String>)
            return
        }
    }

    super.update(id, field, value)
  }

  fun update(profile: Profile) {
    this.add(profile)
  }

  fun updateInterests(id: String, interests: Set<Interests>) {
    Firebase.firestore
        .collection(TAG)
        .document(id)
        .update("interests", Interests.toCompressedString(interests))
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
  }

    fun updateRegisteredEvents(id: String, eventIDs: Set<String>) {
        Firebase.firestore
            .collection(TAG)
            .document(id)
            .update("registeredEvents", FieldValue.arrayUnion(eventIDs))
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }


  override fun delete(id: String) {
    // delete associated data from other collection TODO
    super.delete(id)
  }

  override fun getFromDocument(d: DocumentSnapshot): Profile? {
    val userName = d.getString("userName") ?: return null
    val bio = d.getString("bio")
    val image = d.getString("image")
    val interests = Interests.fromCompressedString(d.getString("interests") ?: "")
    return Profile(userName, bio ?: "", image ?: "", d.id, interests)
  }
}
