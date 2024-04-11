package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.ProfileFirebaseConnection.Companion.addDefaultProfile
import com.github.se.gatherspot.ProfileFirebaseConnection.Companion.deleteProfile
import com.github.se.gatherspot.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
              "email" to user.email,
              "password" to user.password,
          )

      Firebase.firestore
          .collection(USERS)
          .document(user.uid)
          .set(userMap)
          .addOnSuccessListener { Log.d(TAG, "User successfully added!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error creating user", e) }
    }


      // document du User
      fun deleteUserDoc(uid: String) {
          deleteProfile(uid)
          Log.d(TAG, "Deleting user with uid: $uid")
          Firebase.firestore.collection(USERS).document(uid).delete().addOnFailureListener { exception
            ->
            Log.e(TAG, "Error deleting Event", exception)
          }
      }


      // authentification info
      fun deleteCurrentUserAuth() {
      Firebase.auth.currentUser?.delete()?.addOnFailureListener { exception ->
        Log.e(TAG, "Error deleting User", exception)
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
      val email = d.getString("email")!!
      val password = d.getString("password")!!


      return User(uid, email, password)
    }


  }
}
