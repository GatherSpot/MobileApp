package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

/** Firebase connection for profiles. */
class ProfileFirebaseConnection : FirebaseConnectionInterface<Profile> {

  override val COLLECTION = FirebaseCollection.PROFILES.toString().lowercase()
  override val TAG = "FirebaseConnection" // Used for debugging/logs

  /**
   * Fetches the profile from the database.
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
        .collection(COLLECTION)
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

  /**
   * Gets the UID of the user logged in the current instance.
   *
   * @return the UID of the user logged in the current instance, or null if the user is not logged
   *   in
   */
  fun getCurrentUserUid(): String? {
    return FirebaseAuth.getInstance().currentUser?.uid
  }

  /**
   * Checks if the username exists in the database
   *
   * @param userName the username to check
   * @return true if the username exists, false otherwise
   */
  suspend fun usernameExists(userName: String): Boolean {
    val document =
        Firebase.firestore.collection(COLLECTION).whereEqualTo("userName", userName).get().await()
    return document.documents.isNotEmpty()
  }

  /**
   * Fetches the profile from the database with given username. If two profiles have the same
   * username, one is returned.
   *
   * @param userName the username of the user
   * @return the profile with the given username, or null if the username does not exist
   */
  suspend fun fetchFromUserName(userName: String): Profile? {
    val query =
        Firebase.firestore.collection(COLLECTION).whereEqualTo("userName", userName).get().await()
    return query.documents.firstOrNull()?.let { getFromDocument(it) }
  }

  /**
   * Adds or overrides a profile to the database.
   *
   * @param element the profile to add
   */
  override suspend fun add(element: Profile) {
    val data =
        hashMapOf(
            "userName" to element.userName,
            "bio" to element.bio,
            "image" to element.image,
            "interests" to Interests.toCompressedString(element.interests))
    Firebase.firestore.collection(COLLECTION).document(element.id).set(data).await()
  }

  /**
   * Updates a field of a profile in the database.
   *
   * @param id the id of the profile
   * @param field the field to update : {userName, bio, image, interests}
   * @param value the new value of the field
   */
  override suspend fun update(id: String, field: String, value: Any) {
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
        if (!usernameExists(value as String)) super.update(id, field, value)
        else {
          Log.d(TAG, "Username already exists")
        }
      }
      else -> super.update(id, field, value)
    }
  }

  /**
   * Calls the add function to update the profile in the database
   *
   * @param profile the profile to update
   */
  suspend fun update(profile: Profile) {
    this.add(profile)
  }

  /**
   * Updates the interests of a profile in the database
   *
   * @param id the id of the profile
   * @param interests the new interests of the profile
   */
  private suspend fun updateInterests(id: String, interests: Set<Interests>) {
    Firebase.firestore
        .collection(COLLECTION)
        .document(id)
        .update("interests", Interests.toCompressedString(interests))
        .await()
  }

  /**
   * Converts a document to a profile
   *
   * @param d the document to convert
   * @return the profile
   */
  override fun getFromDocument(d: DocumentSnapshot): Profile? {
    val userName = d.getString("userName") ?: return null
    val bio = d.getString("bio")
    val image = d.getString("image")
    val interests = Interests.fromCompressedString(d.getString("interests") ?: "")
    return Profile(userName, bio ?: "", image ?: "", d.id, interests)
  }
}
