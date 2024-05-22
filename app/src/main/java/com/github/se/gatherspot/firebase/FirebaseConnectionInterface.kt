package com.github.se.gatherspot.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Interface for Firebase connections.
 *
 * @param T The collection class type
 * @property COLLECTION The collection name
 * @property TAG The tag for logging
 */
interface FirebaseConnectionInterface<T : CollectionClass> {

  val COLLECTION: String
  val TAG: String

  /**
   * Generates a new ID for a collection item.
   *
   * @return The new ID
   */
  fun getNewID(): String {
    return FirebaseDatabase.getInstance().getReference().child(COLLECTION.lowercase()).push().key!!
  }

  /**
   * Fetch a collection item from the database.
   *
   * @param id The ID of the collection item
   * @return The collection item or null
   */
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

  /**
   * Get a collection item from a document.
   *
   * @param d The document
   * @return The collection item or null
   */
  fun getFromDocument(d: DocumentSnapshot): T?

  /**
   * Add a collection item to the database.
   *
   * @param element The collection item
   */
  fun add(element: T)

  /**
   * Update a collection item in the database.
   *
   * @param id The ID of the collection item
   * @param field The field to update
   * @param value The new value
   */
  fun update(id: String, field: String, value: Any) {
    Firebase.firestore
        .collection(COLLECTION.lowercase())
        .document(id)
        .update(field, value)
        .addOnFailureListener { exception ->
          Log.e(TAG, "Error updating ${COLLECTION.lowercase()}", exception)
        }
  }

  /**
   * Delete a collection item from the database.
   *
   * @param id The ID of the collection item
   */
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
