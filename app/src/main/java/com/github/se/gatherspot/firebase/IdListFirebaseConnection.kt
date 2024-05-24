package com.github.se.gatherspot.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.model.IdList
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

/** Firebase connection for IdList. */
class IdListFirebaseConnection {
  private val COLLECTION = "ID_LIST"
  private val TAG = "IdListFirebaseConnection"
  private val fcoll = Firebase.firestore.collection(COLLECTION)
  private val batchErrorMsg = "Error writing batch"
  private val getErrorMsg = "get failed with :"

  /**
   * Fetches the IdList from Firebase. NOTE : The IdList will be initially empty, to use it in a
   * view, you need to update the view using a lambda function that updates the view.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param category The category of the IdList
   * @param update lambda returned when fetched, useful to update the viewModel
   * @return the IdList
   */
  suspend fun fetchFromFirebase(
      id: String,
      category: FirebaseCollection,
      update: () -> Unit
  ): IdList? = suspendCancellableCoroutine { continuation ->
    val tag = category.name
    val idSet = IdList.empty(id, category)
    fcoll
        .document(tag)
        .collection(id)
        .get()
        .addOnSuccessListener { documents ->
          if (documents != null) {
            val data = documents.documents
            val ids = data.map { it.id }
            idSet.elements = ids
            Log.d(TAG, "DocumentSnapshot data: ${data}")
          } else {
            Log.d(TAG, "No such document")
          }
          update()
          continuation.resume(idSet)
        }
        .addOnFailureListener { exception ->
          Log.d(TAG, "get failed with ", exception)
          continuation.resume(null)
        }
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
  fun add(
      id: String,
      tag: FirebaseCollection,
      elements: List<String>,
      onSuccess: () -> Unit
  ): MutableLiveData<IdList> {
    val batch = Firebase.firestore.batch()
    val data = MutableLiveData<IdList>()
    elements.forEach { event ->
      val docRef = fcoll.document(tag.name).collection(id).document(event)
      batch.set(docRef, mapOf<String, Any>())
    }
    batch
        .commit()
        .addOnSuccessListener {
          Log.d(TAG, "Batch write succeeded.")
          data.value = IdList(id, elements, tag)
          onSuccess()
        }
        .addOnFailureListener { e -> Log.w(TAG, batchErrorMsg, e) }
    return data
  }

  /**
   * Deletes an element from the list.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param category The category of the IdList
   * @param element The element to delete from the list
   * @param onSuccess a lambda function called on success
   */
  fun deleteElement(
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
  }

  /**
   * Fetches the IdList from Firebase.
   *
   * @param id The id of the owner of the list (can be owned by a user, event, etc..)
   * @param category The category of the IdList
   * @param onSuccess a lambda function called on success
   * @return the IdList
   */
  suspend fun fetch(id: String, category: FirebaseCollection, onSuccess: () -> Unit): IdList {
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
   * @param onSuccess a lambda function called on success
   */
  fun addElement(id: String, category: FirebaseCollection, element: String, onSuccess: () -> Unit) {
    val tag = category.name
    fcoll
        .document(tag)
        .collection(id)
        .document(element)
        .set(mapOf<String, Any>())
        .addOnSuccessListener {
          Log.d(TAG, "Element successfully added!")
          onSuccess()
        }
        .addOnFailureListener { exception -> Log.d(TAG, getErrorMsg, exception) }
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
  fun addTwoInSingleBatch(
      id1: String,
      category1: FirebaseCollection,
      element1: String,
      id2: String,
      category2: FirebaseCollection,
      element2: String,
      onSuccess: () -> Unit
  ) {
    val tag1 = category1.name
    val tag2 = category2.name
    val batch = Firebase.firestore.batch()
    val docRef1 = fcoll.document(tag1).collection(id1).document(element1)
    val docRef2 = fcoll.document(tag2).collection(id2).document(element2)
    batch.set(docRef1, mapOf<String, Any>())
    batch.set(docRef2, mapOf<String, Any>())
    batch
        .commit()
        .addOnSuccessListener {
          Log.d(TAG, "Batch write succeeded.")
          onSuccess()
        }
        .addOnFailureListener { e -> Log.w(TAG, batchErrorMsg, e) }
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
   * @param onSuccess a lambda function called on success
   */
  fun removeTwoInSingleBatch(
      id1: String,
      category1: FirebaseCollection,
      element1: String,
      id2: String,
      category2: FirebaseCollection,
      element2: String,
      onSuccess: () -> Unit
  ) {
    val tag1 = category1.name
    val tag2 = category2.name
    val batch = Firebase.firestore.batch()
    val docRef1 = fcoll.document(tag1).collection(id1).document(element1)
    val docRef2 = fcoll.document(tag2).collection(id2).document(element2)
    batch.delete(docRef1)
    batch.delete(docRef2)
    batch
        .commit()
        .addOnSuccessListener {
          Log.d(TAG, "Batch write succeeded.")
          onSuccess()
        }
        .addOnFailureListener { e -> Log.w(TAG, batchErrorMsg, e) }
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
  fun exists(
      id: String,
      category: FirebaseCollection,
      element: String,
      onSuccess: () -> Unit
  ): MutableLiveData<Boolean> {
    val tag = category.name
    val data = MutableLiveData<Boolean>()
    fcoll
        .document(tag)
        .collection(id)
        .document(element)
        .get()
        .addOnSuccessListener { d ->
          data.value = d.exists()
          onSuccess()
        }
        .addOnFailureListener { e -> Log.d(TAG, getErrorMsg, e) }
    return data
  }

  /**
   * Deletes a list.
   *
   * @param id The id of the owner of the list
   * @param category The category of the list
   * @param onSuccess a lambda function called on success
   */
  fun delete(id: String, category: FirebaseCollection, onSuccess: () -> Unit) {
    val tag = category.name
    fcoll
        .document(tag)
        .collection(id)
        .get()
        .addOnSuccessListener { result ->
          val batch = Firebase.firestore.batch()
          result.documents.forEach { doc -> batch.delete(doc.reference) }
          batch
              .commit()
              .addOnSuccessListener {
                Log.d(TAG, "Batch write succeeded.")
                onSuccess()
              }
              .addOnFailureListener { e -> Log.w(TAG, batchErrorMsg, e) }
        }
        .addOnFailureListener { exception -> Log.d(TAG, getErrorMsg, exception) }
  }
}
