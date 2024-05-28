package com.github.se.gatherspot.model

/**
 * Wrapper class for the NFC service.
 *
 * @property NFCStatus The status of the user using the NFC service, with respect to the event.
 */
class NFCService(NFCStatus: NFCStatus) {

  val status: NFCStatus = NFCStatus
  var newEvent: Boolean = false

  /**
   * Temporary function to handle NFC events. Supposedly, this function would be called when an NFC
   * event is detected.
   *
   * @param function Function to be called when an NFC event is detected.
   * @return the result of the function passed in with the profile we got from the NFC event as a
   *   parameter.
   */
  fun onEvent(function: (Profile) -> () -> Unit): () -> Unit {
    // Handle NFC event
    newEvent = true
    val profile: Profile = TODO()
    return function(profile)
  }
}

/** Enum class to represent the status of the user using the NFC service. */
enum class NFCStatus {
  ORGANIZER,
  PARTICIPANT
}
