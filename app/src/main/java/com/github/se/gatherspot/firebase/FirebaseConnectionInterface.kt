package com.github.se.gatherspot.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

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

  suspend fun add(element: T)

  suspend fun update(id: String, field: String, value: Any) {
    Firebase.firestore
        .collection(COLLECTION.lowercase())
        .document(id)
        .update(field, value)
        .addOnFailureListener { exception ->
          Log.e(TAG, "Error updating ${COLLECTION.lowercase()}", exception)
        }
  }

  suspend fun delete(id: String) {
    suspendCancellableCoroutine { continuation ->
      Firebase.firestore
          .collection(COLLECTION.lowercase())
          .document(id)
          .delete()
          .addOnSuccessListener(continuation::resume)
          .addOnFailureListener { exception ->
            Log.e(TAG, "Error deleting ${COLLECTION.lowercase()}", exception)
            continuation.resume(exception)
          }
    }
  }
}
