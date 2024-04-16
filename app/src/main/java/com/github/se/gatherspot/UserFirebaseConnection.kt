package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class UserFirebaseConnection : FirebaseConnectionInterface {

  override val COLLECTION = FirebaseCollection.USERS.toString()
  override val TAG = "UserFirebase"
  val USERS = FirebaseCollection.USERS.toString().lowercase()

  override fun getFromDocument(d: DocumentSnapshot): User? {
    if (d.getString("uid") == null) {
      return null
    }
    val uid = d.getString("uid")!!
    val username = d.getString("username")!!
    val email = d.getString("email")!!
    val password = d.getString("password")!!

    val map = d.data!!
    // val profile = map["profile"] as HashMap<*, *>
    //      val interests = profile["interests"] as List<String>

    return User(uid, username, email, password)
  }

  override fun add(user: User) {

    val userMap: HashMap<String, Any?> =
        hashMapOf(
            "uid" to user.id,
            "username" to user.username,
            "email" to user.email,
            "password" to user.password,
        )

    Firebase.firestore
        .collection(USERS)
        .document(user.id)
        .set(userMap)
        .addOnSuccessListener { Log.d(TAG, "User successfully added!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error creating user", e) }
  }

  fun deleteCurrentUser() {
    Firebase.auth.currentUser?.delete()?.addOnFailureListener { exception ->
      Log.e(TAG, "Error deleting User", exception)
    }
  }

  fun usernameExists(username: String, onComplete: (Boolean) -> Unit) {

    var res = false
    Firebase.firestore
        .collection(USERS)
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if (document.get("username") == username) {
              Log.d(TAG, "LOL")
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

  fun updateUserInterests(uid: String, interests: List<Interests>) {
    val hm: HashMap<String, Any?> = hashMapOf("profile.interests" to interests)

    Firebase.firestore
        .collection(USERS)
        .document(uid)
        .update(hm)
        .addOnSuccessListener { Log.d(TAG, "Interests sucessfully added!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error for interests", e) }
  }
}
