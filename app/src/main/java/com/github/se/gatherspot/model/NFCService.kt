package com.github.se.gatherspot.model

class NFCService(NFCStatus: NFCStatus) {

  val status: NFCStatus = NFCStatus
  var newEvent: Boolean = false

  fun onEvent(function: (Profile) -> () -> Unit): () -> Unit {
    // Handle NFC event
    newEvent = true
    val profile: Profile = TODO()
    return function(profile)
  }
}

enum class NFCStatus {
  ORGANIZER,
  PARTICIPANT
}
