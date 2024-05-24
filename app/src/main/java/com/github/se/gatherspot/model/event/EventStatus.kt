package com.github.se.gatherspot.model.event

/**
 * Enum class for the status of an event
 *
 * @property CREATED The event has been created but not started yet.
 * @property ON_GOING The event is currently happening.
 * @property COMPLETED The event has already finished.
 */
enum class EventStatus {
  CREATED,
  ON_GOING,
  COMPLETED
}
