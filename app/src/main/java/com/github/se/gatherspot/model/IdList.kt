package com.github.se.gatherspot.model

import androidx.room.Entity
import com.github.se.gatherspot.firebase.CollectionClass
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection

/**
 * A list of ids owned by a user that will be stored in a certain firebase collection
 *
 * @param id: the id of the user
 * @param elements: the list of ids
 * @param collection: the collection where the list will be stored useful for example to store a
 *   friend list, a list of events the user is attending, etc.
 */
@Entity(tableName = "id_list", primaryKeys = ["id", "collection"])
class IdList(
    override val id: String,
    var elements: List<String>,
    val collection: FirebaseCollection
) : CollectionClass() {

  /**
   * Add an element to the list
   *
   * @param elementId the id of the element to add
   */
  fun add(elementId: String) {
    IdListFirebaseConnection().addElement(id, collection, elementId) {
      elements = elements.plus(elementId)
    }
  }

  /**
   * Remove an element from the list
   *
   * @param elementId the id of the element to remove
   */
  fun remove(elementId: String) {
    IdListFirebaseConnection().deleteElement(id, collection, elementId) {
      elements = elements.minus(elementId)
    }
  }

  companion object {
    /**
     * Create an empty IdList
     *
     * @param id the id of the user
     * @param collection the collection where it will be stored
     * @return an empty IdList useful for tests, the creation of a new list, and enabling non
     *   blocking access to the list
     */
    fun new(id: String, collection: FirebaseCollection, elements: List<String>) =
        IdListFirebaseConnection().add(id, collection, elements) {}

    /**
     * Fetch an IdList from Firebase
     *
     * @param id the id of the list
     * @param collection the collection associated with the list
     * @param onSuccess a callback to execute when the list is fetched
     */
    suspend fun fromFirebase(id: String, collection: FirebaseCollection, onSuccess: () -> Unit) =
        IdListFirebaseConnection().fetch(id, collection) { onSuccess() }

    /**
     * Create an empty IdList
     *
     * @param id the id of the list
     * @param collection the collection associated with the list
     */
    fun empty(id: String, collection: FirebaseCollection) = IdList(id, listOf(), collection)
  }
}
