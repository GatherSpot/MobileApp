package com.github.se.gatherspot.model

import com.github.se.gatherspot.CollectionClass

class idList private constructor(
  override val id: String,
  val events: MutableList<String>,
  val typeTag: String
) : CollectionClass() {
  companion object {
    val createdEvents = "createdEvents"
  }
}