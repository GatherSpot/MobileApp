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
@Entity(
  tableName = "id_list",
  primaryKeys = ["id","collection"]
)
class IdList(
  override val id: String,
  var elements: List<String>,
  val collection: FirebaseCollection
) : CollectionClass() {
  fun add(eventId: String) {
    IdListFirebaseConnection().addElement(id, collection, eventId) { elements = elements.plus(eventId) }
  }

  fun remove(eventId: String) {
    IdListFirebaseConnection().deleteElement(id, collection, eventId) {
      elements = elements.minus(eventId)
    }
  }

  companion object {
    fun of(id: String, collection: FirebaseCollection, elements: List<String>) =
        IdListFirebaseConnection().add(id, collection, elements) {}

    suspend fun fromFirebase(id: String, collection: FirebaseCollection, onSuccess: () -> Unit) =
        IdListFirebaseConnection().fetch(id, collection) { onSuccess() }
    fun empty(id: String, collection: FirebaseCollection) = IdList(id, listOf(), collection)
  }
}
