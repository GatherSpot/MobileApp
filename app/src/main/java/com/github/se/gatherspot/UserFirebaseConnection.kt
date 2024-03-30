package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Category
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.User
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class UserFirebaseConnection {
  companion object {

    const val USERS = "users"
    const val TAG = "UserFirebase"

    fun getUID(): String {
      return FirebaseDatabase.getInstance().getReference().child(USERS).push().key!!
    }

    fun addUser(user: User) {
      val userMap: HashMap<String, Any?> =
          hashMapOf(
              "uid" to user.uid,
              "username" to user.username,
              "email" to user.email,
              "password" to user.password,
          )

      val interests =
          hashMapOf(
              "interests" to user.profile.interests.toList(),
          )

      userMap["profile"] = interests

      Firebase.firestore
          .collection(USERS)
          .document(user.uid)
          .set(userMap)
          .addOnSuccessListener { Log.d(TAG, "User succesfully added!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error creating user", e) }
    }

    fun deleteUser(uid: String) {
      Firebase.firestore.collection(USERS).document(uid).delete().addOnFailureListener { exception
        ->
        Log.e(TAG, "Error deleting Event", exception)
      }
    }

    suspend fun fetchUser(uid: String): User? = suspendCancellableCoroutine { continuation ->
      Firebase.firestore
          .collection(USERS)
          .document(uid)
          .get()
          .addOnSuccessListener { doc ->
            val res = getUserFromDocument(doc)
            continuation.resume(res)
          }
          .addOnFailureListener { exception ->
            Log.d(TAG, exception.toString())
            continuation.resume(null)
          }
    }

    private fun getUserFromDocument(d: DocumentSnapshot): User? {
      if (d.getString("uid") == null) {
        return null
      }
      val uid = d.getString("uid")!!
      val username = d.getString("username")!!
      val email = d.getString("email")!!
      val password = d.getString("password")!!

      val map = d.data!!
      val profile = map["profile"] as HashMap<*, *>
      val interests = profile["interests"] as List<String>

      return User(
          uid,
          username,
          email,
          password,
          Profile(interests.map { s -> Category.valueOf(s) }.toSet()))
    }

    fun updateUserInterests(uid: String, profile: Profile) {
      val hm: HashMap<String, Any?> = hashMapOf("profile.interests" to profile.interests.toList())

      Firebase.firestore
          .collection(USERS)
          .document(uid)
          .update(hm)
          .addOnSuccessListener { Log.d(TAG, "Interests sucessfully added!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error for interests", e) }
    }
  }
}
