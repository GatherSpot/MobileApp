package com.github.se.gatherspot.model

import com.github.se.gatherspot.CollectionClass
import com.github.se.gatherspot.FirebaseCollection

/**
 * A list of ids owned by a user that will be stored in a certain firebase collection
 *
 * @param id: the id of the user
 * @param events: the list of ids
 * @param collection: the collection where the list will be stored useful for example to store a
 *   friend list, a list of events the user is attending, etc.
 */
class IdList(
    override val id: String,
    var events: List<String>,
    val collection: FirebaseCollection
) : CollectionClass() {
  fun add(eventId: String) {
    events = events.plus(eventId)
  }

  fun remove(eventId: String) {
    events = events.minus(eventId)
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
    fun empty(id: String, collection: FirebaseCollection) = IdList(id, listOf(), collection)
  }
}
