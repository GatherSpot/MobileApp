package com.github.se.gatherspot.model

import com.github.se.gatherspot.model.event.Event

/**
 * Wrapper class for the NFC service.
 *
 * @property NFCStatus The status of the user using the NFC service, with respect to the event.
 */
class NFCService(NFCStatus: NFCStatus) {

  val status: NFCStatus = NFCStatus
  var newEvent: Boolean = false // Needs to be set to true when an NFC event happens

  /**
   * Temporary function to handle NFC events. Supposedly, this function would be called when an NFC
   * event is detected.
   *
   * @param function Function to be called when an NFC event is detected.
   * @return the result of the function passed in with the profile we got from the NFC event as a
   *   parameter.
   */
  private fun getProfile(): Profile? {
    return null
  }

  fun onEvent(function: (Profile) -> () -> Unit, event: Event): () -> Unit {
    // Handle NFC event
    // newEvent = true
    // val profile = getProfile()
    // EventUIViewModel(event).attendEvent(profile.id)
    // return function(profile)
    return {}
  }
}

/** Enum class to represent the status of the user using the NFC service. */
enum class NFCStatus {
  ORGANIZER,
  PARTICIPANT
}
