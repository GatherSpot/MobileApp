package com.github.se.gatherspot

import android.util.Log
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

  fun getID(): String {
    return FirebaseDatabase.getInstance().getReference().child(COLLECTION).push().key!!
  }

  fun getNewID(): String {
    return FirebaseDatabase.getInstance().getReference().child(COLLECTION).push().key!!
  }

  suspend fun fetch(id: String): CollectionClass? = suspendCancellableCoroutine { continuation ->
    Firebase.firestore
        .collection(COLLECTION)
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

  fun add(collectionClass: CollectionClass) {
    val userMap: HashMap<String, Any?> = HashMap()
    for (field in collectionClass.javaClass.declaredFields) {
      userMap[field.name] = field.name
    }

    Firebase.firestore
        .collection(COLLECTION)
        .document(collectionClass.id)
        .set(userMap)
        .addOnSuccessListener { Log.d(TAG, "User successfully added!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error creating user", e) }
  }

  fun add(event: Event) {}

  fun delete(id: String) {
    Firebase.firestore.collection(COLLECTION).document(id).delete().addOnFailureListener { exception
      ->
      Log.e(TAG, "Error deleting ${COLLECTION.lowercase()}", exception)
    }
  }
}
