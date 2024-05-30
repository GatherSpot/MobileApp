package com.github.se.gatherspot.firebase

import android.util.Log
import androidx.room.util.query
import com.github.se.gatherspot.model.IdList
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/** Firebase connection for IdList. */
class IdListFirebaseConnection {
  private val COLLECTION = "ID_LIST"
  private val TAG = "IdListFirebaseConnection"
  private val fcoll = Firebase.firestore.collection(COLLECTION)
  private val batchErrorMsg = "Error writing batch"
  private val getErrorMsg = "get failed with :"

  /**
   * Fetches the IdList from Firebase.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param category The category of the IdList
   * @return the IdList
   */
  suspend fun fetchFromFirebase(
      id: String,
      category: FirebaseCollection,
  ): IdList {
    val tag = category.name
    val query = fcoll.document(tag).collection(id).get().await()
    return IdList(id, query.documents.map { it.id }, category)
  }

  /**
   * Saves the IdList to Firebase.
   *
   * @param idSet The IdList to save
   */
  fun saveToFirebase(idSet: IdList) {
    val tag = idSet.collection.name
    val id = idSet.id
    val data = hashMapOf("ids" to idSet.elements.toList())
    fcoll
        .document(tag)
        .collection(idSet.collection.toString())
        .document(id)
        .set(data)
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
  }

  /**
   * Creates a new list.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param tag The category of the IdList
   * @param elements The elements to add to the list. @onSuccess a lambda function called on success
   * @return MutableLiveData<IdList> that returns the IdList created. Can be directly observed
   */
  suspend fun add(
      id: String,
      tag: FirebaseCollection,
      elements: List<String>,
  ): IdList {
    val batch = Firebase.firestore.batch()
    elements.forEach { event ->
      val docRef = fcoll.document(tag.name).collection(id).document(event)
      batch.set(docRef, mapOf<String, Any>())
    }
    batch
        .commit()
        .addOnSuccessListener { Log.d(TAG, "Batch write succeeded.") }
        .addOnFailureListener { e -> Log.w(TAG, batchErrorMsg, e) }
        .await()
    return IdList(id, elements, tag)
  }

  /**
   * Deletes an element from the list.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param category The category of the IdList
   * @param element The element to delete from the list
   * @param onSuccess a lambda function called on success
   */
  suspend fun deleteElement(
      id: String,
      category: FirebaseCollection,
      element: String,
      onSuccess: () -> Unit
  ) {
    val tag = category.name
    fcoll
        .document(tag)
        .collection(id)
        .document(element)
        .delete()
        .addOnSuccessListener {
          Log.d(TAG, "Element successfully deleted!")
          onSuccess()
        }
        .addOnFailureListener { exception -> Log.d(TAG, getErrorMsg, exception) }
        .await()
  }

  /**
   * Fetches the IdList from Firebase.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param category The category of the IdList
   * @return the IdList
   */
  suspend fun fetch(id: String, category: FirebaseCollection): IdList {
    Log.d(TAG, "Current id: $id")
    val tag = category.name
    Log.d(TAG, "TAG should be FOLLOWERS: $tag")
    val data: IdList
    val querySnapshot: QuerySnapshot = fcoll.document(tag).collection(id).get().await()
    data = IdList(id, querySnapshot.documents.map { it.id }, category)
    Log.d(TAG, "???? ${data.elements}")
    return data
  }

  /**
   * Adds an element to the list.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param category The category of the IdList
   * @param element The element to add to the list
   */
  suspend fun addElement(id: String, category: FirebaseCollection, element: String) {
    val tag = category.name
    fcoll.document(tag).collection(id).document(element).set(mapOf<String, Any>()).await()
  }

  /**
   * Adds two elements to two different lists in a single batch.
   *
   * @param id1 The id of the owner of the first list
   * @param category1 The category of the first list
   * @param element1 The element to add to the first list
   * @param id2 The id of the owner of the second list
   * @param category2 The category of the second list
   * @param element2 The element to add to the second list
   * @return Task<Boolean> that returns true if the elements were added successfully, false
   *   otherwise. This is useful for example when following someone, this ensures we added the user
   *   to the followers list and the user added us to their following list
   */
  suspend fun addTwoInSingleBatch(
      id1: String,
      category1: FirebaseCollection,
      element1: String,
      id2: String,
      category2: FirebaseCollection,
      element2: String,
  ) {
    val tag1 = category1.name
    val tag2 = category2.name
    val batch = Firebase.firestore.batch()
    val docRef1 = fcoll.document(tag1).collection(id1).document(element1)
    val docRef2 = fcoll.document(tag2).collection(id2).document(element2)
    batch.set(docRef1, mapOf<String, Any>())
    batch.set(docRef2, mapOf<String, Any>())
    batch.commit().await()
  }

  /**
   * Removes two elements from two different lists in a single batch.
   *
   * @param id1 The id of the owner of the first list
   * @param category1 The category of the first list
   * @param element1 The element to remove from the first list
   * @param id2 The id of the owner of the second list
   * @param category2 The category of the second list
   * @param element2 The element to remove from the second list
   */
  suspend fun removeTwoInSingleBatch(
      id1: String,
      category1: FirebaseCollection,
      element1: String,
      id2: String,
      category2: FirebaseCollection,
      element2: String,
  ) {
    val tag1 = category1.name
    val tag2 = category2.name
    val batch = Firebase.firestore.batch()
    val docRef1 = fcoll.document(tag1).collection(id1).document(element1)
    val docRef2 = fcoll.document(tag2).collection(id2).document(element2)
    batch.delete(docRef1)
    batch.delete(docRef2)
    batch.commit().await()
  }

  /**
   * Checks if an element exists in the list.
   *
   * @param id The id of the owner of the list
   * @param category The category of the list
   * @param element The element to check if it exists in the list
   * @return MutableLiveData<Boolean> that returns true if the element exists in the list, false
   *   otherwise, can be directly observed
   */
  suspend fun exists(
      id: String,
      category: FirebaseCollection,
      element: String,
  ): Boolean {
    val tag = category.name
    val data = fcoll.document(tag).collection(id).document(element).get().await()
    return data.exists()
  }

  /**
   * Deletes a list.
   *
   * @param id The id of the owner of the list
   * @param category The category of the list
   */
  suspend fun delete(id: String, category: FirebaseCollection) {
    val tag = category.name
    val query = fcoll.document(tag).collection(id).get().await()
    val batch = Firebase.firestore.batch()
    query.documents.forEach { doc -> batch.delete(doc.reference) }
    batch.commit().await()
  }
}
