package com.github.se.gatherspot.firebase

import android.util.Log
import com.github.se.gatherspot.model.IdList
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class IdListFirebaseConnection {
  private val COLLECTION = "ID_LIST"
  private val TAG = "IdListFirebaseConnection"
  private val db = Firebase.firestore

  /**
   * Fetches the IdList from Firebase
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..).
   * @param category The category of the IdList.
   * @param update lambda returned when fetched, useful to update the viewModel
   * @return the IdList NOTE : The IdList will be initially empty, to use it in a view, you need to
   *   update the view using with a lambda function that updates the view
   */
  fun fetch(id: String, category: FirebaseCollection, onSuccess: () -> Unit): IdList {
    val tag = category.name
    val idSet = IdList.empty(id, category)
    db.collection(COLLECTION)
        .document(tag)
        .collection(id)
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            idSet.add(document.id)
          }
          onSuccess()
          Log.d(TAG, "DocumentSnapshot data: ${result.documents}")
        }
        .addOnFailureListener { exception -> Log.d(TAG, "get failed with :", exception) }
    return idSet
  }
  /**
   * Sets the IdList to Firebase
   *
   * @param idSet The IdList to save.
   */
  fun set(idSet: IdList) {
    val tag = idSet.collection.name
    val id = idSet.id
    val batch = db.batch()

    idSet.events.forEach { event ->
      val docRef = db.collection(COLLECTION).document(tag).collection(id).document(event)
      batch.set(docRef, mapOf<String,Any>())
    }
    batch
        .commit()
        .addOnSuccessListener { Log.d(TAG, "Batch write succeeded.") }
        .addOnFailureListener { e -> Log.w(TAG, "Error writing batch", e) }
  }

  /**
   * Deletes an element from the list
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..).
   * @param category The category of the IdList.
   * @param element The element to delete from the list.
   * @return Task<Boolean> that returns true if the element was deleted successfully, false otherwise.
   */
  fun deleteElement(id: String, category: FirebaseCollection, element: String) : Task<Boolean> {
    val task = TaskCompletionSource<Boolean>()
    val tag = category.name
    db.collection(COLLECTION)
        .document(tag)
        .collection(id).document(element).delete()
        .addOnSuccessListener {
          Log.d(TAG, "Element successfully deleted!")
          task.setResult(true) }
        .addOnFailureListener { exception -> Log.d(TAG, "get failed with :", exception)
        task.setResult(false) }
    return task.task
  }

  /**
   * Adds an element to the list
   * @param id The id of the owner of the list (can be owned by a user, event, etc..).
   * @param category The category of the IdList.
   * @param element The element to add to the list.
   * @return Task<Boolean> that returns true if the element was added successfully, false otherwise.
   */
  fun addElement(id: String, category: FirebaseCollection, element: String): Task<Boolean>{
    val task = TaskCompletionSource<Boolean>()
    val tag = category.name
    db.collection(COLLECTION)
        .document(tag)
        .collection(id).document(element).set(mapOf<String,Any>())
        .addOnSuccessListener { Log.d(TAG, "Element successfully added!")
        task.setResult(true) }
        .addOnFailureListener { exception -> Log.d(TAG, "get failed with :", exception)
        task.setResult(false) }
    return task.task
  }

  /**
   * Adds two elements to two different lists in a single batch
   * @param id1 The id of the owner of the first list.
   * @param category1 The category of the first list.
   * @param element1 The element to add to the first list.
   * @param id2 The id of the owner of the second list.
   * @param category2 The category of the second list.
   * @param element2 The element to add to the second list.
   * @return Task<Boolean> that returns true if the elements were added successfully, false otherwise.
   * This is useful for example when following someone, this ensures we added the user to the followers list and the user added us to their following list.
    */
  fun addTwoInSingleBatch(id1: String, category1: FirebaseCollection, element1: String,id2: String, category2: FirebaseCollection, element2: String): Task<Boolean>{
    val task = TaskCompletionSource<Boolean>()
    val tag1 = category1.name
    val tag2 = category2.name
    val batch = db.batch()
    val docRef1 = db.collection(COLLECTION).document(tag1).collection(id1).document(element1)
    val docRef2 = db.collection(COLLECTION).document(tag2).collection(id2).document(element2)
    batch.set(docRef1, mapOf<String,Any>())
    batch.set(docRef2, mapOf<String,Any>())
    batch
        .commit()
        .addOnSuccessListener { Log.d(TAG, "Batch write succeeded.")
        task.setResult(true) }
        .addOnFailureListener { e -> Log.w(TAG, "Error writing batch", e)
        task.setResult(false) }
    return task.task
  }
  /**
   * Removes two elements from two different lists in a single batch
   * @param id1 The id of the owner of the first list.
   * @param category1 The category of the first list.
   * @param element1 The element to remove from the first list.
   * @param id2 The id of the owner of the second list.
   * @param category2 The category of the second list.
   * @param element2 The element to remove from the second list.
   * @return Task<Boolean> that returns true if the elements were removed successfully, false otherwise.
   * This is useful for example when unfollowing someone, this ensures we removed the user from the followers list and the user removed us from their following list.
    */
  fun removeTwoInSingleBatch(id1: String, category1: FirebaseCollection, element1: String,id2: String, category2: FirebaseCollection, element2: String): Task<Boolean>{
    val task = TaskCompletionSource<Boolean>()
    val tag1 = category1.name
    val tag2 = category2.name
    val batch = db.batch()
    val docRef1 = db.collection(COLLECTION).document(tag1).collection(id1).document(element1)
    val docRef2 = db.collection(COLLECTION).document(tag2).collection(id2).document(element2)
    batch.delete(docRef1)
    batch.delete(docRef2)
    batch
        .commit()
        .addOnSuccessListener { Log.d(TAG, "Batch write succeeded.")
        task.setResult(true) }
        .addOnFailureListener { e -> Log.w(TAG, "Error writing batch", e)
        task.setResult(false) }
    return task.task
  }
  /**
   * Checks if an element exists in the list
   * @param id The id of the owner of the list.
   * @param category The category of the list.
   * @param element The element to check if it exists in the list.
   * @return Boolean true if the element exists in the list, false otherwise.
   */
  fun exists(id: String, category: FirebaseCollection, element: String,result: (Boolean) -> Unit) {
    val tag = category.name
    db.collection(COLLECTION)
        .document(tag)
        .collection(id).document(element).get()
        .addOnSuccessListener { result(true) }
        .addOnFailureListener { result(false) }
  }
}