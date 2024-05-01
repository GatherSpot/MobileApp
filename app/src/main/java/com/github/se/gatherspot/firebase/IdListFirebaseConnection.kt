package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.IdList
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class IdListFirebaseConnection {
  private val db = Firebase.firestore
  private val logTag = "IdListFirebaseConnection"

  /**
   * Fetches the IdList from Firebase
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..).
   * @param category The category of the IdList.
   * @param update lambda returned when fetched, useful to update the viewModel
   * @return the IdList NOTE : The IdList will be initially empty, to use it in a view, you need to
   *   update the view using with a lambda function that updates the view
   */
  suspend fun fetchFromFirebase(id: String, category: FirebaseCollection, update: () -> Unit): IdList? = suspendCancellableCoroutine { continuation ->
      val tag = category.name
      val idSet = IdList.empty(id, category)
      db.collection(tag)
          .document(id)
          .get()
          .addOnSuccessListener { document ->
              if (document != null) {
                  val data = document.data
                  if (data != null) {
                      val ids = data["ids"]
                      idSet.events = ids as List<String>
                      Log.d(logTag, "DocumentSnapshot data: ${document.data}")
                  }

              } else {
                  Log.d(logTag, "No such document")
              }
              update()
              continuation.resume(idSet)
          }
          .addOnFailureListener { exception ->
              Log.d(logTag, "get failed with ", exception)
              continuation.resume(null)
          }
  }
  /**
   * Saves the IdList to Firebase
   *
   * @param idSet The IdList to save.
   */
  fun saveToFirebase(idSet: IdList) {
    val tag = idSet.collection.name
    val id = idSet.id
    // TODO : check if this good way to store data
    val data = hashMapOf("ids" to idSet.events.toList())
    db.collection(tag)
        .document(id)
        .set(data)
        .addOnSuccessListener { Log.d(logTag, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(logTag, "Error writing document", e) }
  }
  /**
   * Deletes the IdList from Firebase
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..).
   * @param category The category of the IdList.
   */
  fun deleteFromFirebase(id: String, category: FirebaseCollection) {
    db.collection(category.name)
        .document(id)
        .delete()
        .addOnSuccessListener { Log.d(logTag, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(logTag, "Error deleting document", e) }
  }
}
