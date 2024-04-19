package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.idList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class idListFirebaseConnection {
  private val db = Firebase.firestore
  fun updateFromFirebase(uid: String, tag: String, update: () -> Unit): idList {
    TODO()
  }

  fun saveToFirebase(idList: idList) {
    val tag = idList.typeTag
    val id = idList.id
    //TODO : check if this good way to store data
    val data =
      hashMapOf(
        idList.typeTag to idList.events
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