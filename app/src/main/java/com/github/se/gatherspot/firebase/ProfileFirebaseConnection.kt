package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

open class ProfileFirebaseConnection : FirebaseConnectionInterface<Profile> {

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
  override suspend fun fetch(id: String): Profile? {
    val document = Firebase.firestore.collection(COLLECTION).document(id).get().await()
    return getFromDocument(document)
  }


  /**
   * @return the UID of the user logged in the current instance, or null if the user is not logged
   *   in.
   */
  open fun getCurrentUserUid(): String? {
    return FirebaseAuth.getInstance().currentUser?.uid
  }

  /**
   * Checks if the username exists in the database Once the check is done, the onComplete lambda is
   * called with the result of the check : true if the username exists, false otherwise
   *
   * @param userName the username to check
   */
  open suspend fun ifUsernameExists(userName: String) : Boolean {

    val document = Firebase.firestore
        .collection(COLLECTION)
        .whereEqualTo("userName", userName)
        .get().await()
    return document.isEmpty
  }

  /**
   * Fetches the profile from the database with given username if the username does not exist, the
   * function returns null if two profiles have the same username, one is returned
   *
   * @param userName the username of the user
   */
  open suspend fun fetchFromUserName(userName: String): Profile? {
    val document =
        Firebase.firestore
            .collection(COLLECTION)
            .whereEqualTo("userName", userName)
            .limit(1)
            .get()
            .await()
            .documents
          .firstOrNull()?: return null
    return getFromDocument(document)
  }

  /**
   * Adds or overrides a profile to the database
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
   * Updates a field of a profile in the database
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
        if (ifUsernameExists(value as String)) {
          return
        }
      }
    }

      super.update(id, field, value)
    }

  /** Calls the add function to update the profile in the database */
  open suspend fun update(profile: Profile) {
    this.add(profile)
  }

  /**
   * Updates the interests of a profile in the database
   *
   * @param id the id of the profile
   * @param interests the new interests of the profile
   */
  suspend fun updateInterests(id: String, interests: Set<Interests>) {
    Firebase.firestore
        .collection(COLLECTION)
        .document(id)
        .update("interests", Interests.toCompressedString(interests))
        .await()
  }

  /** Deletes a profile from the database */
  override suspend fun delete(id: String) {
    // delete associated data from other collection TODO
    // delete ratings using registrations to find such events
    // delete registrations
    // (delete User ? No)
    super.delete(id)
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