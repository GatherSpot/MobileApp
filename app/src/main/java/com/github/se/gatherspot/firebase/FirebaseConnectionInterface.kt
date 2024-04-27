package com.github.se.gatherspot.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface FirebaseConnectionInterface<T : CollectionClass> {

  val COLLECTION: String
  val TAG: String

  fun getNewID(): String {
    return FirebaseDatabase.getInstance().getReference().child(COLLECTION.lowercase()).push().key!!
  }

  suspend fun fetch(id: String): T? = suspendCancellableCoroutine { continuation ->
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

  fun getFromDocument(d: DocumentSnapshot): T?

  // Find a way to make this work for all classes generically
  fun add(element: T)

  fun update(id: String, field: String, value: Any) {
    Firebase.firestore
        .collection(COLLECTION.lowercase())
        .document(id)
        .update(field, value)
        .addOnFailureListener { exception ->
          Log.e(TAG, "Error updating ${COLLECTION.lowercase()}", exception)
        }
  }

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
