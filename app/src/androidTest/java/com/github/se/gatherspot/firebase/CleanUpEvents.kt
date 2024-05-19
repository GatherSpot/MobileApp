package com.github.se.gatherspot.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.Test

class CleanUpEvents {

  @Test
  fun running() {
    cleanCollection()
  }

  val TAG = "CleanUpEvents"
  val efc = EventFirebaseConnection()

  fun cleanCollection() {
    Firebase.firestore
        .collection(efc.COLLECTION)
        .whereNotEqualTo("organizerID", "")
        .get()
        .addOnSuccessListener { querySnapshot ->
          Log.d(TAG, "Found ${querySnapshot.documents.size} documents with non empty organizerID")
          querySnapshot.documents.forEach { document ->
            Firebase.firestore
                .collection("clean_events")
                .document(document.id)
                .set(document.data!!)
                .addOnSuccessListener {
                  Log.d(TAG, "DocumentSnapshot successfully moved to clean_events : ${document.id}")
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error moving document", e) }
          }
        }
        .addOnFailureListener { exception -> Log.d(TAG, exception.toString()) }
  }

  fun retrieveEvents() {
    Firebase.firestore
        .collection("clean_events")
        .get()
        .addOnSuccessListener { querySnapshot ->
          Log.d(TAG, "Found ${querySnapshot.documents.size} documents in clean_events")
          querySnapshot.documents.forEach { document ->
            val event = efc.getFromDocument(document)
            if (event != null) {
              efc.add(event)
            }
          }
        }
        .addOnFailureListener { exception -> Log.d(TAG, exception.toString()) }
  }
}
