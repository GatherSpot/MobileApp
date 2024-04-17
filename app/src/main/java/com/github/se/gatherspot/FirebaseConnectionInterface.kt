package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.User
import com.github.se.gatherspot.model.chat.Chat
import com.github.se.gatherspot.model.event.Event
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

interface FirebaseConnectionInterface {

  val COLLECTION: String
  val TAG: String

  fun getNewID(): String {
    return FirebaseDatabase.getInstance().getReference().child(COLLECTION.lowercase()).push().key!!
  }

  suspend fun fetch(id: String): CollectionClass? = suspendCancellableCoroutine { continuation ->
    Firebase.firestore
        .collection(COLLECTION.lowercase())
        .document(id)
        .get()
        .addOnSuccessListener { doc ->
          val res = getFromDocument(doc)
          continuation.resume(res)
        }
        .addOnFailureListener { exception ->
          Log.d(TAG, exception.toString())
          continuation.resume(null)
        }
  }

  fun getFromDocument(d: DocumentSnapshot): CollectionClass?

  // Find a way to make this work for all classes generically
  fun add(event: Event) {}

  fun add(user: User) {}

  fun add(profile: Profile) {}
    fun add(chat: Chat) {}

  fun delete(id: String) {
    Firebase.firestore
        .collection(COLLECTION.lowercase())
        .document(id)
        .delete()
        .addOnFailureListener { exception ->
          Log.e(TAG, "Error deleting ${COLLECTION.lowercase()}", exception)
        }
  }
}
