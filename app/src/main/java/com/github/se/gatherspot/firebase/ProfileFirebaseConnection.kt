package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

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
    val profile = Profile("", "", "", id, Interests.fromCompressedString("1"))
    Firebase.firestore
        .collection(COLLECTION)
        .document(id)
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
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
   * Checks if the username exists in the database Once the check is done, the onComplete lambda is
   * called with the result of the check : true if the username exists, false otherwise.
   *
   * @param userName the username to check
   * @param onComplete the lambda to call when the check is done
   */
  fun ifUsernameExists(userName: String, onComplete: (Boolean) -> Unit) {

    Firebase.firestore
        .collection(COLLECTION)
        .whereEqualTo("userName", userName)
        .get()
        .addOnSuccessListener { result -> onComplete(result.documents.isNotEmpty()) }
        .addOnFailureListener { onComplete(true) }
  }

  /**
   * Fetches the profile from the database with given username. If two profiles have the same
   * username, one is returned.
   *
   * @param userName the username of the user
   * @return the profile with the given username, or null if the username does not exist
   */
  suspend fun fetchFromUserName(userName: String): Profile? =
      suspendCancellableCoroutine { continuation ->
        Firebase.firestore
            .collection(COLLECTION)
            .whereEqualTo("userName", userName)
            .get()
            .addOnSuccessListener { querysnps ->
              when {
                querysnps.documents.isEmpty() -> continuation.resume(null)
                else -> continuation.resume(getFromDocument(querysnps.documents[0]))
              }
            }
            .addOnFailureListener { exception ->
              Log.d(TAG, exception.toString())
              continuation.resume(null)
            }
      }

  /**
   * Adds or overrides a profile to the database.
   *
   * @param element the profile to add
   */
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

  /**
   * Updates a field of a profile in the database.
   *
   * @param id the id of the profile
   * @param field the field to update : {userName, bio, image, interests}
   * @param value the new value of the field
   */
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
    }

    super.update(id, field, value)
  }

  /**
   * Calls the add function to update the profile in the database
   *
   * @param profile the profile to update
   */
  fun update(profile: Profile) {
    this.add(profile)
  }

  /**
   * Updates the interests of a profile in the database
   *
   * @param id the id of the profile
   * @param interests the new interests of the profile
   */
  fun updateInterests(id: String, interests: Set<Interests>) {
    Firebase.firestore
        .collection(COLLECTION)
        .document(id)
        .update("interests", Interests.toCompressedString(interests))
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
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
