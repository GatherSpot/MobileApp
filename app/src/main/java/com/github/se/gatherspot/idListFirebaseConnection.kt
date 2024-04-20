package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.IdList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class idListFirebaseConnection {
  private val db = Firebase.firestore
  fun updateFromFirebase(uid: String, tag: String, update: () -> Unit): IdList {
    var idList = IdList.empty(uid, tag)
    val docRef = db.collection(tag).document(uid)
    docRef.get()
      .addOnSuccessListener { document ->
        if (document != null) {
          Log.d(tag, "DocumentSnapshot data: ${document.data}")
          idList.events = document.toObject<List<String>>() ?: listOf()
          update()
        } else {
          Log.d(tag, "No such document")
        }
      }
      .addOnFailureListener { exception ->
        Log.d(tag, "get failed with ", exception)
      }
    return idList
  }

  fun saveToFirebase(idList: IdList) {
    val tag = idList.typeTag
    val id = idList.id
    //TODO : check if this good way to store data
    val data =
      hashMapOf(
        "ids" to idList.events
      )
    db.collection(tag)
      .document(id)
      .set(data)
      .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully written!") }
      .addOnFailureListener { e -> Log.w(tag, "Error writing document", e) }
  }

  fun deleteFromFirebase(id: String, tag: String) {
    db.collection(tag)
      .document(id)
      .delete()
      .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully deleted!") }
      .addOnFailureListener { e -> Log.w(tag, "Error deleting document", e) }
  }
}