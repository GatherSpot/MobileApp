package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.IdList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class IdListFirebaseConnection {
  private val db = Firebase.firestore
  private val logTag = "IdListFirebaseConnection"
  fun updateFromFirebase(uid: String, tag: String, update: () -> Unit): IdList {
    val idSet = IdList.empty(uid, tag)
    db.collection(tag).document(uid)
      .get()
      .addOnSuccessListener { document ->
        if (document != null) {
          val data = document.data
          if (data != null) {
            val ids = data["ids"]
            idSet.events = ids as List<String>
          }
          Log.d(logTag, "DocumentSnapshot data: ${document.data}")
        } else {
          Log.d(logTag, "No such document")
        }
        update()
      }
      .addOnFailureListener { exception ->
        Log.d(logTag, "get failed with ", exception)
      }
    return idSet
  }

  fun saveToFirebase(idSet: IdList) {
    val tag = idSet.typeTag
    val id = idSet.id
    //TODO : check if this good way to store data
    val data =
      hashMapOf(
        "ids" to idSet.events.toList()
      )
    db.collection(tag)
      .document(id)
      .set(data)
      .addOnSuccessListener { Log.d(logTag, "DocumentSnapshot successfully written!") }
      .addOnFailureListener { e -> Log.w(logTag, "Error writing document", e) }
  }

  fun deleteFromFirebase(id: String, tag: String) {
    db.collection(tag)
      .document(id)
      .delete()
      .addOnSuccessListener { Log.d(logTag, "DocumentSnapshot successfully deleted!") }
      .addOnFailureListener { e -> Log.w(logTag, "Error deleting document", e) }
  }
}