package com.github.se.gatherspot.model

import com.github.se.gatherspot.CollectionClass

class IdList(override val id: String, var events: List<String>, val typeTag: String) :
    CollectionClass() {
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
     * @param tag the tag of the list
     * @return an empty IdList useful for tests, the creation of a new list, and enabling non
     *   blocking access to the list
     */
    fun empty(id: String, tag: String) = IdList(id, listOf(), tag)
  }
}
